(ns tuneramblr.models.song
  (:use monger.operators)
  (:require [tuneramblr.models.util :as util]
            [tuneramblr.models.image :as image]
            [tuneramblr.models.location :as location]
            [tuneramblr.models.gmusic :as gmusic]
            [monger.collection :as mc]
            [monger.query :as mq]
            [clojure.set :as cset])
  (:import (java.util Calendar TimeZone)))

;;;; functions for working with song
;;;; data

;; the delimiter for userdefined metadata
(def USER_DEF_DELIM ",")

;; creates a list (vector) of all monitored 
;; metadata for a given song
(defn join-meta [{userdef :userdef 
                  weather :weather}]
  (concat (.split userdef USER_DEF_DELIM)
          (.split weather USER_DEF_DELIM)))

;; add data to the songs collection
(defn add [data]
  (let [mdata (assoc 
                data 
                :location 
                (location/formatted-address? 
                  (location/address? (:lat data) 
                                     (:lng data))))]
  (if (mc/insert "songs"
                 (if (:img mdata)
                   (image/add-img
                     (assoc mdata :img (util/dec-img mdata)))
                   mdata))
    {:added true, 
     :message (str "We got " 
                   (:title mdata) 
                   ".  Ramble on!")}
    {:added false, 
     :message (str "Whoops!  We dropped the ball.  " 
                   (:title mdata) 
                   " was not added.")})))

;; add a new song to the model
(defn add-song [songdata]
  ;we will want to pull any weather or user defined data
  ;from the songdata and add it to the model, probably 
  ;after we attempt to add the song.
  (add songdata))

;; get songs for a user
;; this will return a map of
;; songs recorded by this user
;; (note the _id property
;;  will be removed)
(defn get-songs-by-username [username]
  (map util/no-id (mc/find-maps "songs" 
                                {:username username})))

;; "near by" will be defined as a 10 mile radius 
;; (for right now)
(def NEAR_BY_MILES 10.0)

;; get all songs that appear within a 10 mile 
;; radius of the user's location
(defn get-songs-near-by [lat lng] 
  (let [clat (util/m2lat NEAR_BY_MILES)
        clng (util/m2lng NEAR_BY_MILES lat)]
    ;; fetch all songs where the lat/lng +/- 
    ;; the degrees equivalent of NEAR_BY_MILES
    (mc/find-maps "songs"
                  {:lat {$gt (- lat clat)
                         $lt (+ lat clat)}
                   :lng {$gt (- lng clng)
                         $lt (+ lng clng)}})))



;; convert to hours into the day
(defn hours-into-day [tstamp tz]
  (let [cal (Calendar/getInstance)]
    (.setTimeInMillis cal tstamp)
    (.setTimeZone cal (TimeZone/getTimeZone tz))
    (.get cal Calendar/HOUR_OF_DAY))) 

;; get songs before some date
(defn get-songs-before-date [tstamp]
  (mc/find-maps "songs"
                {:tstamp {$lt tstamp}}))

;; get songs after some date
(defn get-songs-after-date [tstamp]
  (mc/find-maps :songs
                {:tstamp {$gt tstamp}}))

;; take a timestamp and convert it into
;; a discrete (enumerated) time value 
;; (e.g. "morning")
(defn get-discrete-time [tstamp tz]
  (let [hours-in (hours-into-day tstamp tz)]
    (cond
      (< hours-in 12) "Morning"
      (< hours-in 17) "Afternoon"
      true "Evening")))

;; if we have a user, pull songs 
;; for that user, otherwise just 
;; get the songs near by.  the return 
;; value of this function is a map of 
;; songs and metadata frequencies.
(defn get-songs [user lat lng]
  (let [result (if user
                 (map util/no-id 
                      (get-songs-by-username user))
                 (map util/no-id 
                      (get-songs-near-by lat lng)))]
    {:songs (map (fn [sng] 
                   (assoc sng :metadata 
                          (apply 
                            array-map 
                            (flatten 
                              (map 
                                #(vector (keyword %) %)
                                (join-meta sng)))))) 
                 result)
     :freqs (util/word-freq
              (mapcat (fn [sng]
                        (join-meta sng)) result))}))

;; builds a frequency map of 
;; the meta-data associated with the track
(defn build-freqs [songs]
  (util/word-freq
    (mapcat (fn [sng]
              (join-meta sng)) songs)))

;; builds a sequence of images
(defn build-imgs [songs]
  (filter #(not (nil? %))
          (map (fn [sng]
                 (:img sng)) songs)))

;;;; Map/Reduce song info ;;;;

;; mash the track properties together
;; to form a unique way of identify the
;; track
(defn mashem [song]
  (clojure.string/join "_"
                      [(:artist song)
                       (:album song)
                       (:title song)]))

;; create tuples of the track and its 
;; associated properties
(defn tuplize-meta [songs]
  (map #(vector (mashem %)
                (concat (.split (:userdef %) USER_DEF_DELIM)
                        (.split (:weather %) USER_DEF_DELIM))) songs))

;; group together each instance of 
;; a track and all assocaited properties
(defn groupem [tuplized]
  (->>
    (group-by first tuplized)
    (map (fn [[k v]]
           (list k (map second v))))
    (map (fn [[k v]]
           (list k (flatten v))))))

;; build up a map of phrase
;; frequency counts
(defn bfmap [flatem]
  (->>
    (map (fn [[k v]]
           {k (util/word-freq v)}) flatem)
    (apply merge-with conj)))
       
;; builds a mapping of
;; of track meta data 
;; and frequency counts
(defn track-meta [songs]
  (->>
    (tuplize-meta songs)
    (groupem)
    (bfmap)))

;; creates a set of tuples of track data
(defn tuplize [songs]
  (map #(vector 
          (mashem %) %) 
       songs))

;; perform a set union 
;; on two potentionally
;; non-set collections
(defn set-union [s1 s2]
  (cset/union
    (util/to-set s1)
    (util/to-set s2)))

;; merge all of the track 
;; records
(defn mergem [groupem]
  (map (fn [[k v]]
         (reduce 
           #(merge-with set-union %1 %2) {} v)) 
       groupem))

(defn fix-meta [mergem]
  (map 
    (fn [v]
      (assoc v 
             :metadata 
             (util/word-freq
               (concat
                 (clojure.string/split
                   (clojure.string/join 
                     USER_DEF_DELIM 
                     (util/to-set 
                       (:weather v))) #",")
                 (filter #(not 
                            (clojure.string/blank? %))
                         (clojure.string/split
                           (clojure.string/join
                             USER_DEF_DELIM 
                             (util/to-set (:userdef v))) #","))))))
    mergem))


;; merges all unique track 
;; records into a single 
;; record
(defn merge-tracks [songs]
  (->>
    (tuplize songs)
    (groupem)
    (mergem)
    (fix-meta)))


;; get data for the timeline
(defn get-timeline-data [username]
  (map util/no-id 
       (mq/with-collection "songs"
                           (mq/find {:username username})
                           (mq/fields [])
                           (mq/sort {:tstamp -1}))))
  
  
;; track selection based on current meta data
;; winfo - the comma delimited set of adjectives describing
;; the current weather conditions
;; latlng - the latlng location
;; dtime - timestamp
;; watcha - what the user is currently doing
(defn applic-track [winfo latlng dtime watcha]
  (let [lat (Double/valueOf (:lat latlng))
        lng (Double/valueOf (:lng latlng))]
    (let [wq (map #(hash-map :weather {$regex %})
                  (clojure.string/split winfo #","))
          clat (util/m2lat NEAR_BY_MILES)
          clng (util/m2lng NEAR_BY_MILES lat)]
      (util/rand-ele
        (map util/no-id
             (mc/find-maps 
               "songs" 
               {$and
                [{$or 
                  [{$or wq}
                   {:lat {$gt (- lat clat)
                          $lt (+ lat clat)}
                    :lng {$gt (- lng clng)
                          $lt (+ lng clng)}}
                   {:ctype {$ne "skip"}}]}
                 {:userdef {$regex watcha}}]}))))))

;; selects a completely random track
(defn getRandomTrack [username] 
  (let [cnt (mc/count "library" {:username username})]
    (util/no-id
     (first
      (mq/with-collection "library"
        (mq/find {:username username})
        (mq/limit -1)
        (mq/skip (int (rand cnt))))))))

;; selects a track that you haven't listened
;; to recently or a track that has never been 
;; listened to. outside of these constraints, 
;; the track should be "random"
(defn getSomethingNew [] )
  
  
