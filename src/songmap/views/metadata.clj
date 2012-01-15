(ns songmap.views.metadata
    (:require [noir.response :as response]
            [songmap.views.common :as common]
            [songmap.models.metadata :as metadata])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

;; handles a request for songs
(defpage [:post "/metadata"] {:keys [id]}
  (response/json (metadata/get-meta id)))

