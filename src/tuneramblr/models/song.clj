(ns tuneramblr.models.song
  (:use monger.operators)
  (:require [tuneramblr.models.util :as util]
            [tuneramblr.models.image :as image]
            [monger.collection :as mc])
  (:import (java.util Calendar)))

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
  (if (mc/insert "songs"
                 (if (:img data)
                   (image/add-img
                     (assoc data :img (util/dec-img data)))
                   data))
    {:added true, 
     :message (str "We got " 
                   (:title data) 
                   ".  Ramble on!")}
    {:added false, 
     :message (str "Whoops!  We dropped the ball.  " 
                   (:title data) 
                   " was not added to the database")}))

;; add a new song to the model
(defn add-song [songdata]
  ;we will want to pull any weather or user defined data
  ;from the songdata and add it to the model, probably 
  ;after we attempt to add the song.
  (add songdata))

;; get songs for a user
;; the songs will be limited 
;; by latitutude and longitude
(defn get-songs-by-username [username]
  (mc/find-maps "songs" 
           {:username username}))

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
;; TODO: this should accept a timezone
(defn hours-into-day [tstamp]
  (let [cal (Calendar/getInstance)]
    (.setTimeInMillis cal tstamp)
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
(defn get-discrete-time [tstamp]
  (let [hours-in (hours-into-day tstamp)]
    (cond
      (< hours-in 12) "morning"
      (< hours-in 17) "afternoon"
      true "evening")))

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

;;;; Map/Reduce song info ;;;;

(defn mashem [song]
  (clojure.string/join "_"
                      [(:artist song)
                       (:album song)
                       (:title song)]))

(defn tuplize [songs]
  (map #(vector (mashem %)
                (concat (.split (:userdef %) USER_DEF_DELIM)
                        (.split (:weather %) USER_DEF_DELIM))) songs))

(defn groupem [tuplized]
  (->>
    (group-by first tuplized)
    (map (fn [[k v]]
           {k (map second v)}))
    (apply merge-with conj)))

(defn track-meta [songs]
  (->>
    (tuplize songs)
    (groupem)))