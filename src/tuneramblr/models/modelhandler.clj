(ns tuneramblr.models.modelhandler
   (:require [tuneramblr.models.song :as song]
             [tuneramblr.models.util :as util]
             [monger.conversion :as monc]))


;;;; SONG METHODS ;;;;

;; the delimiter for userdefined metadata
(def USER_DEF_DELIM ",")

;; converts a DB object returned from
;; the database into a Clojure map. removes
;; the unnecessary _id from the record
(defn db-to-map [dbo]
  (dissoc (monc/from-db-object dbo true) 
          :_id))

;; creates a list (vector) of all monitored 
;; metadata for a given song
(defn join-meta [{userdef :userdef 
                  weather :weather}]
  (concat (.split userdef USER_DEF_DELIM)
          (.split weather USER_DEF_DELIM)))
  

;; if we have a user, pull songs 
;; for that user, otherwise just 
;; get the songs near by.  the return 
;; value of this function is a map of 
;; songs and metadata frequencies.
(defn get-songs [user lat lng]
  (let [result (if user
                 (map db-to-map (song/get-songs user lat lng))
                 (map db-to-map (song/get-songs-near-by lat lng)))]
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
     
;; add a new song to the model
(defn add-song [songdata]
  ;we will want to pull any weather or user defined data
  ;from the songdata and add it to the model, probably 
  ;after we attempt to add the song.
  (song/add songdata))