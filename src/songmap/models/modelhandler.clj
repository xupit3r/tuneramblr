(ns songmap.models.modelhandler
   (:require [songmap.models.song :as song]
             [songmap.models.util :as util]))


;;;; SONG METHODS ;;;;

;; the delimiter for userdefined metadata
(def USER_DEF_DELIM ",");

;; strip out the id keys
;; (they are useless outside 
;;  the context of the DB)
(defn no-id [mp]
  (dissoc mp :_id))

(defn join-meta [{userdef :userdef 
                  weather :weather}]
  (concat (.split userdef USER_DEF_DELIM)
          (.split weather USER_DEF_DELIM)))
  

;; if we have a user, pull songs 
;; for that user, otherwise just 
;; get the songs near by
(defn get-songs [user lat lng]
  (let [result (if user
                 (map no-id (song/get-songs user lat lng))
                 (map no-id (song/get-songs-near-by lat lng)))]
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


;;;; PLAYLIST METHODS ;;;;

(defn get-playlist [id]
  ["this" "is" "a" "playlist"])