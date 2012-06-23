(ns tuneramblr.models.gmusic
  (:require [clj-http.client :as client]))

;;;; we just want to be able to create 
;;;; a playlist, given a set of tracks

;; handle to the cookie store
(def GOOGLE_COOKIE_STORE (clj-http.cookies/cookie-store))

;; makes stuff more readable
(def START_AUTH_KEY "Auth=")
(def START_AUTH_KEY_LENGTH (.length START_AUTH_KEY))
(def END_AUTH_KEY "\n")

;; Google Music URLs
(def GOOGLE_LOGIN_URL "https://www.google.com/accounts/ClientLogin")
(def GOOGLE_MUSIC_SERVICE_URL "https://play.google.com/music/listen?hl=en&u=0")
(def GOOGLE_MUSIC_GET_PLAYLISTS_URL "https://play.google.com/music/services/loadplaylist")


;; retrieves the auth token from the
;; the login request
(defn authtoken [{body :body}]
  (let [start (+ (.indexOf 
                   body 
                   START_AUTH_KEY) 
                 START_AUTH_KEY_LENGTH)]
    (let [end (.indexOf 
                body 
                END_AUTH_KEY
                start)]
      (.trim (.substring body start end)))))

;; login to the Google Music service
(defn glogin [username password]
  (let [token (authtoken (client/post GOOGLE_LOGIN_URL
                                      {:form-params
                                       {:service "sj"
                                        :Email username
                                        :Passwd password}
                                       :cookie-store GOOGLE_COOKIE_STORE}
                                      {:as :json}))]
    ; just throw the auth token back for the time being
    ; TODO: finish me!
    token))
