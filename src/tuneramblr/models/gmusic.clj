(ns tuneramblr.models.gmusic
  (import [java.net URLEncoder])
  (:require [clj-http.client :as client])
  (:use tuneramblr.trprops))

;;;; we just want to be able to create 
;;;; a playlist, given a set of tracks

;; makes stuff more readable
(def AUTH_TOKEN_SEP "\n")

;; defines the User-Agent to use for the requests
(def GOOGLE_MUSIC_USER_AGENT "Music Manager (1, 0, 24, 7712 - Windows)")

;; Google Play URLs
(def GOOGLE_LOGIN_URL "https://www.google.com/accounts/ClientLogin")
(def GOOGLE_PLAY_LOGIN "https://play.google.com/music/listen")
(def GOOGLE_MUSIC_ADD_PLAYLIST_URL "https://play.google.com/music/services/addplaylist?u=0&xt=")

;; the auth cookies to pull from play login
(def PLAY_COOKIE_XT "xt")
(def PLAY_COOKIE_SJSAID "sjsaid")


;; makes a request to the auth service
;; returns the full response, which includes
;; the auth token
(defn makeAuthRequest [username password]
  (client/post 
    GOOGLE_LOGIN_URL
    {:form-params
     {:service "sj"
      :Email username
      :Passwd password}}
    {:as :json}))

;; builds a map of response tokens
;; (token name -> token value)
(defn pullAuthTokens [{body :body}]
  (into {}
        (into #{} 
              (map 
                (fn [tp] 
                  (let [pair (.split tp "=")]
                    [(keyword (first pair)) 
                     (second pair)]))
                (.split body AUTH_TOKEN_SEP)))))

(defn makePlayRequest [tokens]
  {:Auth (:Auth tokens)
   :resp
   (client/post 
     GOOGLE_PLAY_LOGIN
     {:headers {"Authorization" (str "GoogleLogin auth=" (:Auth tokens))}
      :form-params {:u "0" :hl "en"}
      :force-redirects true
      :throw-exceptions false})})

;; pull user session auth cookies
(defn pullAuthCookies [result]
  (let [cookies (:cookies (:resp result))]
    {:Auth (:Auth result)
     :xt (:value
           (get cookies PLAY_COOKIE_XT))
     :sjaid (:value 
              (get cookies PLAY_COOKIE_SJSAID))}))

;; logs into Google Play and
;; returns the necessary auth cookies
;; and parameters
(defn loginToPlay [username password]
  (->>
    (makeAuthRequest username password)
    (pullAuthTokens)
    (makePlayRequest)
    (pullAuthCookies)))

;; adds a playlist to the account tied to the 
;; auth token
(defn addPlaylist [listname auth]
  (client/post 
    (str GOOGLE_MUSIC_ADD_PLAYLIST_URL auth)
    {:headers 
     {"User-Agent" GOOGLE_MUSIC_USER_AGENT
      "Authorization" auth}}
    {:form-params
     {:title listname}}
     {:as :json}))


;;;; helpful util stuff ;;;;

;; some helpful labels
(def MORE true)
(def NO_MORE false)

(defn pair [param value more]
  (str param "=" (URLEncoder/encode value "UTF-8")
       (if more "&" "")))