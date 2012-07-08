(ns tuneramblr.models.gmusic
  (import [java.net URLEncoder])
  (:require [clj-http.client :as client])
  (:use tuneramblr.trprops))

;;;; we just want to be able to create 
;;;; a playlist, given a set of tracks

;; handle to the cookie store
(def GOOGLE_COOKIE_STORE (clj-http.cookies/cookie-store))

;; makes stuff more readable
(def AUTH_TOKEN_SEP "\n")

;; Google Music URLs
(def GOOGLE_LOGIN_URL "https://www.google.com/accounts/ClientLogin")
(def GOOGLE_MUSIC_SERVICE_URL "https://play.google.com/music/listen?hl=en&u=0")
(def GOOGLE_MUSIC_GET_PLAYLISTS_URL "https://play.google.com/music/services/loadplaylist")

;; Google OAuth 2.0 stuff

;; oauth 2.0 property keys
(def OAUTH_2_REDIRECT_KEY :tr.google.oa2.redirect)
(def OAUTH_2_CLIENTID_KEY :tr.google.oa2.clientid)
(def OAUTH_2_SECRET_KEY :tr.google.oa2.secret)
(def OAUTH_2_EMAIL_KEY :tr.google.oa2.email)
(def OAUTH_2_PROMPT_KEY :tr.google.oa2.prompt)
(def OAUTH_2_SCOPE_KEY :tr.google.oa2.scope)

;; Google OAUTH 2.0 URL keys
(def OA_RESPONSE_TYPE "response_type")
(def OA_CLIENT_ID "client_id")
(def OA_REDIRECT_URI "redirect_uri")
(def OA_STATE "state")
(def OA_SCOPE "scope")
(def OA_ACCESS_TYPE "access_type")
(def OA_APPROVAL_PROMPT "approval_prompt")

;; oauth 2.0 values
(def REDIRECT (read-str-prop OAUTH_2_REDIRECT_KEY))
(def CLIENT_ID (read-str-prop OAUTH_2_CLIENTID_KEY))
(def CLIENT_SECRET (read-str-prop OAUTH_2_SECRET_KEY))
(def APPROVAL_PROMPT (read-str-prop OAUTH_2_PROMPT_KEY))
(def SCOPE (read-str-prop OAUTH_2_SCOPE_KEY))
(def GOOGLE_OAUTH_URL "https://accounts.google.com/o/oauth2/auth")

;; builds a map of response tokens
;; (token name -> token value)
(defn getAuthTokens [{body :body}]
  (into {}
        (into #{} 
              (map 
                (fn [tp] 
                  (let [pair (.split tp "=")]
                    [(keyword (first pair)) 
                     (second pair)]))
                (.split body AUTH_TOKEN_SEP)))))

;; makes a request to the auth service
;; returns the full repsone from the request
(defn makeAuthRequest [username password]
  ; is there an XT cookie in this cookie set?
  ; SEE: clientlogin.py for details about how this
  ; is to work
  (client/post GOOGLE_LOGIN_URL
               {:form-params
                {:service "sj"
                 :Email username
                 :Passwd password}
                :cookie-store GOOGLE_COOKIE_STORE}
               {:as :json}))


;;;; SETTING UP THE OAUTH URL ;;;;

;; not needed right now, but
;; just wanted to put forth 
;; some of the initial effort

;; some helpful labels
(def MORE true)
(def NO_MORE false)

(defn pair [param value more]
  (str param "=" (URLEncoder/encode value "UTF-8")
       (if more "&" "")))

(defn oauth-url []
  (str 
    GOOGLE_OAUTH_URL "?"
    (pair OA_RESPONSE_TYPE "code" MORE)
    (pair OA_CLIENT_ID CLIENT_ID MORE)
    (pair OA_REDIRECT_URI REDIRECT MORE )
    (pair OA_APPROVAL_PROMPT APPROVAL_PROMPT MORE)
    (pair OA_SCOPE SCOPE NO_MORE)))