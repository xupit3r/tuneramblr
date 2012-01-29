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



