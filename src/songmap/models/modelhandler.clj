(ns songmap.models.modelhandler
   (:require [songmap.models.song :as song]))


;;;; SONG CRUD METHODS ;;;;

;; pull songs within a certain lattitude and 
;; longitude from the model
(defn get-songs [lat lng]
  ; mock data for right now
  [{:lat 40.4,
    :lng -80.08,
    :artist "artist 1",
    :title "title 1",
    :album "album 1",
    :genre "genre 1"} 
   {:lat 40.5,
    :lng -80.09,
    :artist "artist 2",
    :title "title 2",
    :album "album 2",
    :genre "genre 2"} 
   {:lat 40.2,
    :lng -80.00,
    :artist "artist 3",
    :title "title 3",
    :album "album 3",
    :genre "genre 3"} 
   {:lat 40.4,
     :lng -80.09,
     :artist "artist 4",
     :title "title 4",
     :album "album 4",
     :genre "genre 4"}])

;; add a new song to the model
(defn add-song [songdata]
  ;we will want to pull any weather or user defined data
  ;from the songdata and add it to the model, probably 
  ;after we attempt to add the song.
  (song/add {:lat (:lat songdata)
                :lng (:lng songdata)
                :artist (:artist songdata)
                :title (:title songdata) 
                :album (:album songdata)
                :genre (:genre songdata)}))


;;;; METADATA CRUD METHODS ;;;;

;; get metadata associated with a particular user
;; this will return a mapping of metadata keywords 
;; an the frequencies at which they are used
(defn get-meta [id]
  {:one 1,
   :two 2,
   :three 3,
   :four 4,
   :five 5})



;;;; PLAYLIST CRUD METHODS ;;;;

(defn get-playlist [id]
  ["this" "is" "a" "playlist"])