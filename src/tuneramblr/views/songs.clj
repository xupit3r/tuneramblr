(ns tuneramblr.views.songs
  (:require [noir.response :as response]
            [noir.session :as session]
            [tuneramblr.views.common :as common]
            [tuneramblr.models.modelhandler :as modelhandler]
            [tuneramblr.models.user.umanage :as umanage])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

;;;; GENERAL NOTE:
;;;; if a request parameter must be 
;;;; interpreted as a type other than String 
;;;; you must be sure to EXPLICTLY convert/cast 
;;;; to the desired type

;; handles a request for songs near a defined location
(defpage [:post "/songs/get"] {:keys [lat lng]}
  (let [user (umanage/me)]
    (response/json (modelhandler/get-songs user 
                                           (Double/valueOf lat) 
                                           (Double/valueOf lng)))))

;; handle the addition of a song (POST)
;; TODO: add the timezone here
(defpage [:post "/songs/add"] {:keys [lat lng artist title album genre weather userdef img tstamp]}
  (let [user (umanage/me)]
    (response/json (modelhandler/add-song {:username user
                                           :lat (Double/valueOf lat)
                                           :lng (Double/valueOf lng)
                                           :artist artist
                                           :title title 
                                           :album album 
                                           :genre genre
                                           :weather weather
                                           :userdef userdef
                                           :img img
                                           :tstamp (Long/valueOf tstamp)}))))

;; mobile song addition (POST)
;; TODO: add the timezone here
(defpage [:post "/mobile/songs/add"] {:keys [lat lng artist title album genre weather userdef username password img tstamp]}
  (if (umanage/login! {:username username :password password})
    (response/json (modelhandler/add-song {:username username
                                           :lat (Double/valueOf lat)
                                           :lng (Double/valueOf lng)
                                           :artist artist
                                           :title title 
                                           :album album 
                                           :genre genre
                                           :weather weather
                                           :userdef userdef
                                           :img img
                                           :tstamp (Long/valueOf tstamp)}))
    (response/json {:added false :message (str "failed to authenticate user: " username)})))
