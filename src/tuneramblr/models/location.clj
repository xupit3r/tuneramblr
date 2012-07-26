(ns tuneramblr.models.location
  (:require [http.async.client :as http])
  (:use [clojure.data.json :only (read-json json-str)]))

;;;; logic for working with locations

;; we will use the Google Geocoding API
;; - use reverse geocoding to lookup 
;;   a street address based on some 
;;   lat and lng

;; URL stuff
(def BASE_SERVICE_URL "http://maps.googleapis.com/maps/api/geocode/")
(def RETURN_JSON_FORMAT "json")
(def LATLNG_PARAM_KEY "latlng")
(def SENSOR_PARAM_KEY "sensor")

;; JSON keys
(def RESULTS :results)
(def FORMATTED_ADDRESS :formatted_address)

;; builds a query string
;; given a lattitude and longitude
(defn build-query [lat lng]
  (str
    "?" 
    LATLNG_PARAM_KEY "="
    lat "," lng
    "&" SENSOR_PARAM_KEY "=false"))

;; builds a well formatted URL
;; given a query string
(defn build-url [queryStr]
  (str
    BASE_SERVICE_URL
    RETURN_JSON_FORMAT
    queryStr))

;; makes a call to the location
;; API given a well formatted URL
(defn call-location-api [apiUrl]
  (with-open [client (http/create-client)]
    (let [response (http/GET client apiUrl)]
      (-> 
        response
        http/await
        http/string
        read-json))))

;; finds the best guess at 
;; a human readable street 
;; address, given a latitude
;; and longitude
(defn address? [lat lng]
  (->>
    (build-query lat lng)
    (build-url)
    (call-location-api)
    (RESULTS)
    (first)))

;; retrieves the formatted address
;; from a map of address data
(defn formatted-address? [adata]
  (FORMATTED_ADDRESS adata))
    

