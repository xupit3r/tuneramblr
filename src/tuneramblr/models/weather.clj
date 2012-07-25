(ns tuneramblr.models.weather
  (:require [http.async.client :as http]
            [tuneramblr.trprops :as props])
  (:use [clojure.data.json :only (read-json json-str)]))

;;;; logic for working with weather
;;;; - make calls to some API for
;;;;   pulling weather information

;; weather underground
;; (used by tuneramblr-mobile)

;; weather underground API key
(def WU_API_KEY_KEY :wu.api.key)
(def WU_API_KEY (props/read-str-prop WU_API_KEY_KEY))

;; URLs
(def BASE_SERVICE_URL "http://api.wunderground.com/api/")
(def GEO_LOOKUP "geolookup")
(def CONDITIONS_FEATURE "conditions")
(def RETURN_JSON_FORMAT "json")

;; keys in the JSON response
;; NOTE: temps in fahrenheit
(def CURRENT_CONDITIONS :current_observation)
(def CURRENT_QUALITATIVE :weather)
(def CURRENT_TEMPERATURE :temp_f)

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
;; FIXME: determine which HTTP library I want
;; to use. http.async seems to be A LOT more
;; flexible than clj-http.
(defn call-weather-api [apiUrl]
  (with-open [client (http/create-client)]
    (let [response (http/GET client apiUrl)]
      (-> 
        response
        http/await
        http/string
        read-json))))

;; get weather information
(defn weather? [lat lng]
  (->>
    (build-query lat lng)
    (build-url)
    (call-weather-api)))

;; pull the current conditions 
;; from the retrieved weather data
(defn current-conditions? [wdata]
  (CURRENT_CONDITIONS wdata))

;; pull the qualitative weather
;; report from the current conditions
(defn qualitative? [cc]
  (CURRENT_QUALITATIVE cc))

;; pull the temperature 
;; from the current conditions
(defn temperature? [cc]
  (CURRENT_TEMPERATURE cc))