(ns songmap.models.modelhandler
   (:require [songmap.models.song :as song]))


;;;; SONG METHODS ;;;;

;; strip out the id keys
;; (they are useless outside 
;;  the context of the DB)
(defn no-id [mp]
  (dissoc mp :_id))
  

;; if we have a user, pull songs 
;; for that user, otherwise just 
;; get the songs near by
(defn get-songs [user lat lng]
  (if user
      (map no-id (song/get-songs user lat lng))
      (map no-id (song/get-songs-near-by lat lng))))

;; add a new song to the model
(defn add-song [songdata]
  ;we will want to pull any weather or user defined data
  ;from the songdata and add it to the model, probably 
  ;after we attempt to add the song.
  (song/add songdata))


;;;; PLAYLIST METHODS ;;;;

(defn get-playlist [id]
  ["this" "is" "a" "playlist"])