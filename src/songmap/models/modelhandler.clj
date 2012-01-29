(ns songmap.models.modelhandler
   (:require [songmap.models.song :as song]))


;;;; SONG METHODS ;;;;

;; strip out the id keys
(defn no-id [mp]
  (dissoc mp :_id))

;; need to define what "near by"
;; means. once we do that, we 
;; can return the songs (anonymously)
;; for display on the map
(defn get-songs-near-by [lat lng] 
  [])
  

;; if we have a user, pull songs 
;; for that user, otherwise just 
;; get the songs near by
(defn get-songs [user lat lng]
  (if user
      (map no-id (song/get-songs user lat lng))
      (map no-id (get-songs-near-by lat lng))))

;; add a new song to the model
(defn add-song [songdata]
  ;we will want to pull any weather or user defined data
  ;from the songdata and add it to the model, probably 
  ;after we attempt to add the song.
  (song/add songdata))


;;;; METADATA METHODS ;;;;

;; get metadata associated with a particular user
;; this will return a mapping of metadata keywords 
;; an the frequencies at which they are used
(defn get-meta [id]
  {:one 1,
   :two 2,
   :three 3,
   :four 4,
   :five 5})



;;;; PLAYLIST METHODS ;;;;

(defn get-playlist [id]
  ["this" "is" "a" "playlist"])