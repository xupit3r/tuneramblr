(ns songmap.models.image
  (:require [songmap.models.util :as util])
  (:use somnium.congomongo))


;;;; crazy stuff can happen to images 
;;;; here.

;; add an image to the store
;; this places the image in mongo's
;; GridFS. returns the name of
(defn add-img [song]
  (when (:img song)
    (let [iname (util/get-iname song)]
      (if (insert-file! 
            (:img song)
            :filename iname)
        (assoc song :img iname)
        (assoc song :img nil)))))

;; retrieve an image from the store
;; this retrieves an image from the 
;; 
(defn get-img [iname]
  ; just get it!
 )
                  
                     

