(ns songmap.models.playlist
  (:use somnium.congomongo))

;;;; functions for working with playlist
;;;; data in the DB

;; add data to the songs collection
(defn add [data]
  (if (insert! :playlists data)
    {:added true, 
     :message (str (:title data) " was added to the database")}
    {:added false, 
     :message (str (:title data) " was not added to the database")}))

