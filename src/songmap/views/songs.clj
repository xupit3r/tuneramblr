(ns songmap.views.songs
  (:require [noir.response :as response]
            [songmap.views.common :as common]
            [songmap.models.songs :as songs])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

;; handles a request for songs
(defpage [:post "/songs"] {:keys [lat lng]}
  (response/json [{:lat lat
                  :lng lng
                  :name "SONG 1"
                  :content "this is some content for the marker"}]))

