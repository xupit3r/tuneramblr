(ns tuneramblr.trprops
  (:require clojure.java.io))

;; load a properties file
(defn load-props [file-name]
  (with-open [^java.io.Reader reader (clojure.java.io/reader file-name)] 
    (let [props (java.util.Properties.)]
      (.load props reader)
      (into {} (for [[k v] props] [(keyword k) (read-string v)])))))

;; read the properties file
(def TUNERAMBLR_PROPERTIES
  ;; replace this with your properties file
  (load-props "props/tuneramblr.properties"))

(defn read-str-prop [property]
  (str (get TUNERAMBLR_PROPERTIES
            property)))

(defn read-int-prop [property]
  (int (get TUNERAMBLR_PROPERTIES
            property)))

