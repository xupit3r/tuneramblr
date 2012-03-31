(ns songmap.views.images
  (:require [songmap.models.image :as image]
            [noir.response :as response])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

;;;; All kinds of awesome request options 
;;;; for images (this is the interface through 
;;;; which image operations will occur).


;; image content type map
(def content-types {:jpeg "image/jpeg"})

;; test
(defpage "/image/ugen" {}
  (response/empty))

;; get image
(defpage "/image/ugen/:iname" {iname :iname}
  (response/content-type
    (:jpeg content-types)
    (image/get-img iname)))
  
  


