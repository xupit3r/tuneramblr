(ns songmap.views.songs
  (:require [noir.response :as response]
            [noir.session :as session]
            [songmap.views.common :as common]
            [songmap.models.modelhandler :as modelhandler]
            [songmap.models.user.umanage :as umanage])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

;; handles a request for songs
(defpage [:post "/songs/get"] {:keys [lat lng]}
  (let [user (umanage/me)]
    (response/json (modelhandler/get-songs user lat lng))))

;; handle the addition of a song (POST)
(defpage [:post "/songs/add"] {:keys [lat lng artist title album genre weather userdef]}
  (let [user (umanage/me)]
    (response/json (modelhandler/add-song {:username user
                                           :lat lat
                                           :lng lng
                                           :artist artist
                                           :title title 
                                           :album album 
                                           :genre genre
                                           :weather weather
                                           :userdef userdef}))))

;; handle the addition of a song (GET)
(defpage [:get "/songs/add"] {:keys [lat lng artist title album genre weather userdef]}
  (let [user (umanage/me)]
    (response/json (modelhandler/add-song {:username user
                                           :lat lat
                                           :lng lng
                                           :artist artist
                                           :title title 
                                           :album album 
                                           :genre genre
                                           :weather weather
                                           :userdef userdef}))))

;; mobile song addition (GET)
(defpage [:get "/mobile/songs/add"] {:keys [lat lng artist title album genre weather userdef username password]}
  (if (umanage/login! {:username username :password password})
    (response/json (modelhandler/add-song {:username username
                                           :lat lat
                                           :lng lng
                                           :artist artist
                                           :title title 
                                           :album album 
                                           :genre genre
                                           :weather weather
                                           :userdef userdef}))
    (response/json {:added false :message "failed to authenticate user"})))

;; mobile song addition (POST)
(defpage [:post "/mobile/songs/add"] {:keys [lat lng artist title album genre weather userdef username password]}
  (if (umanage/login! {:username username :password password})
    (response/json (modelhandler/add-song {:username username
                                           :lat lat
                                           :lng lng
                                           :artist artist
                                           :title title 
                                           :album album 
                                           :genre genre
                                           :weather weather
                                           :userdef userdef}))
    (response/json {:added false :message (str "failed to authenticate user: " username)})))



