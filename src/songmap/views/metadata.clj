(ns songmap.views.metadata
    (:require [noir.response :as response]
            [songmap.views.common :as common]
            [songmap.models.modelhandler :as modelhandler])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

;; metadata will be spread across several
;; models (user defined, weather, etc.)

;; handles a request for metadata
;; this includes weather and user defined
;; properties
(defpage [:post "/metadata/get"] {:keys [user]}
  (response/json (modelhandler/get-meta user)))
