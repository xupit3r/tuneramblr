(ns tuneramblr.models.song
  (:use somnium.congomongo)
  (:require [tuneramblr.models.util :as util]
            [tuneramblr.models.image :as image]))

;;;; functions for working with song
;;;; data in the DB

;; add data to the songs collection
(defn add [data]
  (if (insert! :songs 
               (image/add-img
                 (assoc data :img (util/dec-img data))))
    {:added true, 
     :message (str (:title data) " was added to the database")}
    {:added false, 
     :message (str (:title data) " was not added to the database")}))

;; get songs for a user
;; the songs will be limited 
;; by latitutude and longitude
(defn get-songs [username lat lng]
  (fetch :songs :where {:username username}))

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
    (fetch :songs
           :where {:lat {:$gt (- lat clat)
                         :$lt (+ lat clat)}
                   :lng {:$gt (- lng clng)
                         :$lt (+ lng clng)}})))
    