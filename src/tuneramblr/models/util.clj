(ns tuneramblr.models.util
  (import  org.apache.commons.codec.binary.Base64))

;;;; general utility functions

;; get the current system time
(defn current-time []
  (System/currentTimeMillis))

;; removes the internal DB id from the map
(defn no-id [dbo]
  (dissoc dbo :_id))

;; image content type map
(def content-types {:jpeg "image/jpeg"})


;;;; maths ;;;;;

;; sum up values in a list
(defn sum [lst]
  (apply + lst))


;;;; distance utility functions ;;;;

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


;;;; stats'n'at ;;;;

;; creates a vector of tuples 
;; the first of each tuple is the 
;; word/phrase and the second is 
;; a talley of one (to indicate that 
;; this current tuple represents a 
;; single instance of the word/phrase)
(defn tuplize [phrases]
      (map #(vector % 1) phrases))

;; creates a grouping of all unique instances 
;; of words (tokens) in a list
(defn group-instances [tuplized]
  (->> 
    (group-by first tuplized)
    (map (fn [[k v]]
           {k (map second v)}))
    (apply merge-with conj)))

;; sum up the value portion 
;; of a key value pair
(defn sum-v [[k v]]
  {k (sum v)})

;; build a final mapping of frequency 
;; counts of word/phrases 
;; (word/phrase -> frequency)
(defn build-freq-map [instances]
    (apply merge 
           (map sum-v instances)))


;; builds a frequency count
;; of words/phrases appearing 
;; in a string
(defn word-freq [phrases]
  (->> 
    (tuplize phrases)
    (group-instances)
    (build-freq-map)))

;;;; image util functions


;; decodes a base 64 encoded image
;; this is using commons-codec 1.3 
;; which does not have a decode 
;; method that accepts a string 
;; so, we need to pull the bytes,
;; decode.  returns the decoded 
;; byte array as that will be what 
;; we will be storing
(defn dec-img [{img :img}]  
  (Base64/decodeBase64
    (.getBytes img)))

;; define an image name
(defn get-iname [{user :username
                  lat :lat
                  lng :lng}]
  (str user 
       (.hashCode lat)
       (.hashCode lng)
       (current-time)))

