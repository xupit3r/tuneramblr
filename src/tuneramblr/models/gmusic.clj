(ns tuneramblr.models.gmusic
  (import [java.net URLEncoder])
  (:require [clj-http.client :as client])
  (:use tuneramblr.trprops
        [clojure.data.json :only (read-json json-str)]))

;; the token that separates the auth tokens
(def AUTH_TOKEN_SEP "\n")

;; defines the User-Agent to use for the requests
(def GOOGLE_MUSIC_USER_AGENT "Music Manager (1, 0, 24, 7712 - Windows)")

;; Google Play URLs
(def GOOGLE_LOGIN_URL "https://www.google.com/accounts/ClientLogin")
(def GOOGLE_PLAY_LOGIN "https://play.google.com/music/listen")
(def GOOGLE_PLAY_SEARCH_URL "https://play.google.com/music/services/search?u=0&xt=")
(def GOOGLE_PLAY_PLAY_URL "https://play.google.com/music/play?u=0&pt=e&songid=")
(def GOOGLE_PLAY_GET_LIBRARY_URL "https://play.google.com/music/services/loadalltracks?u=0&xt=")

;; the auth cookies to pull from play login
(def PLAY_COOKIE_XT "xt")
(def PLAY_COOKIE_SJSAID "sjsaid")

;; builds the authorization header
(defn authHeader [{auth :Auth}]
  (str "GoogleLogin auth=" auth))

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

;; makes the actual request to login
;; to Play
(defn makePlayLoginRequest [tokens]
  {:Auth (:Auth tokens)
   :resp
   (client/post 
     GOOGLE_PLAY_LOGIN
     {:headers {"Authorization" (authHeader tokens)}
      :form-params {:u "0" :hl "en"}
      :force-redirects true})})

;; pull user session auth cookies
(defn pullAuthCookies [result]
  (let [cookies (:cookies (:resp result))]
    {:Auth (:Auth result)
     :xt (:value
           (get cookies PLAY_COOKIE_XT))
     :sjsaid (:value 
              (get cookies PLAY_COOKIE_SJSAID))}))

;; logs into Google Play and
;; returns the necessary auth cookies
;; and parameters
(defn loginToPlay [username password]
  (->>
    (makeAuthRequest username password)
    (pullAuthTokens)
    (makePlayLoginRequest)
    (pullAuthCookies)))

;; perform a song search on the user's
;; library and return the results
(defn songSearch [search authSession]
  (:results
    (read-json
      (:body
        (client/post 
          (str GOOGLE_PLAY_SEARCH_URL (:xt authSession))
          {:headers {"Authorization" (authHeader authSession)}
           :form-params {:json (json-str {:q search})}}
          {:as :json})))))

;; returns response from the URL request
(defn makeUrlRequest [songId authSession]
  (client/get
    (str GOOGLE_PLAY_PLAY_URL songId)
    {:headers {"Authorization" (authHeader authSession)}
     :cookies {"xt" {:value (:xt authSession)}
               "sjsaid" {:value (:sjsaid authSession)}}}
    {:as :json}))

;; retrieves a URL from which the song can
;; be streamed
(defn songPlayUrl [songId authSession]
  (try
    {:url (:url 
            (read-json 
              (:body (makeUrlRequest songId authSession))))}
  (catch Exception e
    {:url "BAD URL"})))

;; get all the users tracks from gmusic
(defn getUserLibrary
  ([authSession] (getUserLibrary authSession true []))
  ([authSession ct userLib]
    (cond 
      (not ct) (map (fn [el]
                      {:title (:title el)
                       :artist (:artist el)
                       :album (:album el)
                       :duration (:durationMillis el)
                       :playId (:id el)
                       :artUrl (:albumArtUrl el)
                       :playRating (:rating el)
                       }) userLib)
      :else (let [libChunk 
                  (read-json
                    (:body 
                      (client/post
                        (str GOOGLE_PLAY_GET_LIBRARY_URL (:xt authSession))
                        {:headers {"Authorization" (authHeader authSession)}
                         :form-params {:json (json-str {:continuationToken ct})}}
                        {:as :json})))]
              (recur
                authSession
                (:continuationToken libChunk)
                (concat userLib (:playlist libChunk)))))))

;; determines if we have a good 
;; Google Play session (one that 
;; is authorized to make requests)
(defn goodSession? [authSession]
  (and (:songs (songSearch "" authSession)) true))

;; gets a new Google Play session
(defn getNewAuthSession [oldAuthSession]
  (pullAuthCookies 
    (makePlayLoginRequest oldAuthSession)))

;;;; helpful util stuff ;;;;

;; some helpful labels
(def MORE true)
(def NO_MORE false)

(defn pair [param value more]
  (str param "=" (URLEncoder/encode value "UTF-8")
       (if more "&" "")))