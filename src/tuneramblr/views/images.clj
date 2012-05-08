(ns tuneramblr.views.images
  (:require [tuneramblr.models.image :as image]
            [noir.response :as response])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

;;;; All kinds of awesome request options 
;;;; for images (this is the interface through 
;;;; which image operations will occur).

;; test
(defpage "/image/ugen" {}
  (response/empty))

;; get image.  note: the route pattern 
;; (by default) will only match up the first 
;; . or / 
(defpage "/image/ugen/:iname" {iname :iname}
  (response/content-type
    "image/jpeg"
    (image/get-img iname)))
  
  


