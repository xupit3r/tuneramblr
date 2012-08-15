(ns tuneramblr.views.songs
  (:require [noir.response :as response]
            [noir.session :as session]
            [tuneramblr.views.common :as common]
            [tuneramblr.models.song :as song]
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
    (response/json (song/get-songs user 
                                   (Double/valueOf lat) 
                                   (Double/valueOf lng)))))

;; mobile song addition (POST)
;; TODO: add the timezone here
(defpage [:post "/mobile/songs/add"] {:keys [lat lng artist title album weather userdef username password img tstamp ctype]}
  (if (umanage/mobile-login! {:username username :password password})
    (response/json (song/add-song {:username username
                                   :lat (Double/valueOf lat)
                                   :lng (Double/valueOf lng)
                                   :artist artist
                                   :title title 
                                   :album album 
                                   :weather weather
                                   :userdef userdef
                                   :img img
                                   :tstamp (Long/valueOf tstamp)
                                   :ctype ctype}))
    (response/json {:added false :message (str "failed to authenticate user: " username)})))
