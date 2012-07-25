(ns tuneramblr.models.weather
  (:require [clj-http.client :as client])
  (:use [tuneramblr.trprops]))

;;;; logic for working with weather
;;;; - make calls to some API for
;;;;   pulling weather information

;; weather underground
;; (used by tuneramblr-mobile)

;; weather underground API key
(def WU_API_KEY_KEY :wu.api.key)
(def WU_API_KEY (read-str-prop WU_API_KEY_KEY))

;; URLs
(def BASE_SERVICE_URL "http://api.wunderground.com/api/")
(def GEO_LOOKUP "geolookup")
(def CONDITIONS_FEATURE "conditions")
(def RETURN_JSON_FORMAT "json")

;; keys in the JSON response
;; NOTE: temps in fahrenheit
(def CURRENT_CONDITIONS "current_observation")
(def CURRENT_QUALITATIVE "weather")
(def CURRENT_TEMPERATURE "temp_f")

;; build the query portion of the URL
(defn build-query [lat lng]
  (let [queryStr (str "/q/" lat "," lng)
        formatStr (str "." RETURN_JSON_FORMAT)]
    (str queryStr formatStr)))

;; build the full URL
(defn build-url [queryUrl]
  (str
    BASE_SERVICE_URL
    WU_API_KEY "/"
    GEO_LOOKUP "/"
    CONDITIONS_FEATURE
    queryUrl))

;; make call to API
;; FIXME: response is coming back with
;; Content-encoding: deflate
;; clj-http doesn't seem to like this
(defn call-weather-api [apiUrl]
  (client/get apiUrl {:as :json}))

;; get weather information
(defn weather? [lat lng]
  (->>
    (build-query lat lng)
    (build-url)
    (call-weather-api)))
  
    
  
  
