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


;;;; stats help

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
     
        



