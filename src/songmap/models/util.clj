(ns songmap.models.util)

;;;; general utility functions

;; get user home
(defn user-home []
  (System/getProperty "user.home"))

;; get the file seperator for this OS
(defn file-sep []
  (System/getProperty "file.separator"))

;; get the line separator for this OS
(defn line-sep []
  (System/getProperty "line.separator"))

(defn current-time []
  (System/currentTimeMillis))


;;;; distance utility functions

(def EARTH_RADIUS 3960.0)
(def DEGREES_TO_RADIANS (/ (Math/PI) 180.0))
(def RADIANS_TO_DEGREES (/ 180.0 (Math/PI)))

;; miles to lattitude
(defn m2lat [miles]
  (* (/ miles EARTH_RADIUS)
     RADIANS_TO_DEGREES))

;; miles to longitude (need lat for this)
(defn m2lng [miles lat]
  (* (/ miles 
        (* EARTH_RADIUS 
           (Math/cos (* lat 
                   DEGREES_TO_RADIANS))))
     RADIANS_TO_DEGREES))
     
        



