(ns songmap.models.image
  (:require [songmap.models.util :as util])
  (:use somnium.congomongo))


;;;; crazy stuff can happen to images 
;;;; here.

;; add an image to the store
;; it is assumed that the image is 
;; a byte array.  Note, this places 
;; the image in mongo's GridFS store. 
(defn add-img [song]
  (when (:img song)
    (let [iname (util/get-iname song)]
      (if (insert-file!
            :imgFs
            (:img song)
            :filename iname
            :contentType "image/jpeg")
        (assoc song :img iname)
        (assoc song :img nil)))))

;; retrieve an image from the store
;; this retrieves an image from the 
;; gridFs datastore.
(defn get-img [iname]
  (stream-from 
    :imgFs 
    (fetch-one-file :imgFs
                    :where {:filename iname})))
                  
                     

