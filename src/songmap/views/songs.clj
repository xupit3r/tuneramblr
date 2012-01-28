(ns songmap.views.songs
  (:require [noir.response :as response]
            [songmap.views.common :as common]
            [songmap.models.modelhandler :as modelhandler])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

;; handles a request for songs
(defpage [:post "/songs/get"] {:keys [user lat lng]}
  (response/json (modelhandler/get-songs user lat lng)))

;; handle the addition of a song (POST)
(defpage [:post "/songs/add"] {:keys [user lat lng artist title album genre weather userdef]}
  (response/json (modelhandler/add-song {:user user
                                         :lat lat
                                         :lng lng
                                         :artist artist
                                         :title title 
                                         :album album 
                                         :genre genre
                                         :weather weather
                                         :userdef userdef})))

;; handle the addition of a song (GET)
(defpage [:get "/songs/add"] {:keys [user lat lng artist title album genre weather userdef]}
  (response/json (modelhandler/add-song {:user user
                                         :lat lat
                                         :lng lng
                                         :artist artist
                                         :title title 
                                         :album album 
                                         :genre genre
                                         :weather weather
                                         :userdef userdef})))

