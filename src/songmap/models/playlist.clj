(ns songmap.models.playlist
  (:use somnium.congomongo
        hiccup.page-helpers))

;;;; functions for working with playlist
;;;; data in the DB

;; add playlist to the playlists collection
(defn add [data]
  (if (insert! :playlists data)
    {:added true, 
     :message (str (:title data) " was added to the database")}
    {:added false, 
     :message (str (:title data) " was not added to the database")}))

;; generate a playlist from 
;; the provided songs
(defn generate [user title songs]
  (xhtml (map (fn [song]
                [:song
                 [:title (:title song)]
                 [:artist (:artist song)]
                 [:album (:album song)]])
              songs)))

;; TODO: add generation for anon user



