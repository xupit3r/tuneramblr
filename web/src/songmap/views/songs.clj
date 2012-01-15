(ns songmap.views.songs
  (:require [noir.response :as response]
            [songmap.views.common :as common]
            [songmap.models.songs :as songs])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

;; handles a request for songs
(defpage [:post "/songs/get"] {:keys [lat lng]}
  (response/json (songs/get-songs lat lng)))

;; handle the addition of a song
(defpage [:post "/songs/add"] {:keys [lat lng songname]}
  (response/json (songs/add-song lat lng songname)))

