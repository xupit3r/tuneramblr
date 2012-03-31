(ns songmap.views.images
  (:require [songmap.models.image :as image]
            [noir.response :as response])
  (:use [noir.core :only [defpage]]))

;;;; All kinds of awesome request options 
;;;; for images (this is the interface through 
;;;; which image operations will occur).


;; image content type map
(def content-types {:jpeg "image/jpeg"})


;; get image defpage "/image/<user>/<image id>"
(defpage "/image/ugen/:id" {:keys [id]}
  (response/content-type
    (:jpeg content-types)
    (image/get-img id)))
  
  


