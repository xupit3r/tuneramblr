(ns tuneramblr.models.image
  (:require [tuneramblr.models.util :as util]
            [monger.gridfs :as gfs])
  (:use [monger.gridfs :only [store-file make-input-file filename content-type metadata]])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]
           [javax.imageio ImageIO]
           [org.imgscalr Scalr]))


;;;; crazy stuff can happen to images 
;;;; here.

;; the desired scaled image dimension
;; (in pixels)
(def SCALED_IMAGE_DIMENSION 200)

;; scales an image to the scaled
;; dimensions: sd (assumed to be a square)
(defn scale-img [img sd]
  (let [imgBuff (ImageIO/read
                  (new ByteArrayInputStream img))]
    (let [baos (new ByteArrayOutputStream)]
      (ImageIO/write
        (Scalr/resize imgBuff sd nil)
        "jpeg"
        baos)
      (.flush baos) 
      (let [barr (.toByteArray baos)]
        (.close baos)
        barr))))

;; add an image to the store
;; it is assumed that the image is 
;; a byte array.  Note, this places 
;; the image in mongo's GridFS store. 
(defn add-img [song]
  (when (:img song)
    (let [iname (util/get-iname song)]
      (if (store-file
            (make-input-file 
              (scale-img (:img song) 
                         SCALED_IMAGE_DIMENSION))
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
                  
                     

