(ns tuneramblr.models.song
  (:use somnium.congomongo)
  (:require [tuneramblr.models.util :as util]
            [tuneramblr.models.image :as image])
  (:import (java.util Calendar)))

;;;; functions for working with song
;;;; data

;; add data to the songs collection
(defn add [data]
  (if (insert! :songs
               (if (:img data)
                 (image/add-img
                   (assoc data :img (util/dec-img data)))
                 data))
    {:added true, 
     :message (str "We got " 
                   (:title data) 
                   ".  Ramble on!")}
    {:added false, 
     :message (str "Whoops!  We dropped the ball.  " 
                   (:title data) 
                   " was not added to the database")}))

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



;; convert to hours into the day
;; TODO: this should accept a timezone
(defn hours-into-day [tstamp]
  (let [cal (Calendar/getInstance)]
    (.setTimeInMillis cal tstamp)
    (.get cal Calendar/HOUR_OF_DAY))) 

;; get songs before some date
(defn get-songs-before-date [tstamp]
  (fetch :songs
         :where {:tstamp {:lt$ tstamp}}))

;; get songs after some date
(defn get-songs-after-date [tstamp]
  (fetch :songs
         :where {:tstamp {:gt$ tstamp}}))

;; take a timestamp and convert it into
;; a discrete (enumerated) time value 
;; (e.g. "morning")
(defn get-discrete-time [tstamp]
  (let [hours-in (hours-into-day tstamp)]
    (cond
      (< hours-in 12) "morning"
      (< hours-in 17) "afternoon"
      true "evening")))
    
    