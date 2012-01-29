(ns songmap.models.song
   (:use somnium.congomongo))

;;;; functions for working with song
;;;; data in the DB

;; add data to the songs collection
(defn add [data]
  (if (insert! :songs data)
    {:added true, 
     :message (str (:title data) " was added to the database")}
    {:added false, 
     :message (str (:title data) " was not added to the database")}))

;; get songs for a user
;; the songs will be limited 
;; by latitutude and longitude
(defn get-songs [username lat lng]
  (println (str "username: " username))
  (fetch :songs :where {:username username}))