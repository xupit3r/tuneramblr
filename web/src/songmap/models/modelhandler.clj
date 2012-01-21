(ns songmap.models.modelhandler
   (:require [songmap.models.song :as song]))


;;;; SONG CRUD METHODS ;;;;

;; pull songs within a certain lattitude and 
;; longitude from the model
(defn get-songs [lat lng]
  ; just give me the only record in the
  ; db, for right now
  (song/find-records {:id "1"}))

;; add a new song to the model
(defn add-song [songdata]
  ;we will want to pull any weather or user defined data
  ;from the songdata and add it to the model, probably 
  ;after we attempt to add the song.
  (song/create {:lat (:lat songdata)
                :lng (:lng songdata)
                :artist (:artist songdata)
                :title (:title songdata) 
                :album (:album songdata)
                :genre (:genre songdata)}))


;;;; METADATA CRUD METHODS ;;;;

;; get metadata associated with a particular user
(defn get-meta [id]
  ["this" "is" "some" "metadata"])



;;;; PLAYLIST CRUD METHODS ;;;;

(defn get-playlist []
  ["this" "is" "a" "playlist"])