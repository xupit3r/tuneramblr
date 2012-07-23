(ns tuneramblr.models.image
  (:require [tuneramblr.models.util :as util]
            [monger.gridfs :as gfs])
  (:use [monger.gridfs :only [store-file make-input-file filename content-type metadata]]))


;;;; crazy stuff can happen to images 
;;;; here.

;; add an image to the store
;; it is assumed that the image is 
;; a byte array.  Note, this places 
;; the image in mongo's GridFS store. 
(defn add-img [song]
  (when (:img song)
    (let [iname (util/get-iname song)]
      (if (store-file
            (make-input-file (:img song))
            (filename iname)
            (metadata {:format "jpeg"})
            (content-type "image/jpeg"))
        (assoc song :img iname)
        (assoc song :img nil)))))

;; retrieve an image from the store
;; this retrieves an image from the 
;; gridFs datastore.
(defn get-img [iname]
  (->
    (gfs/find-one {:filename iname})
    (.getInputStream)))
                  
                     

