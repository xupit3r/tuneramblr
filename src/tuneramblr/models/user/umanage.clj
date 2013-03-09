(ns tuneramblr.models.user.umanage
  (:require [tuneramblr.models.util :as util]
            [tuneramblr.models.gmusic :as gmusic]
            [noir.util.crypt :as crypt] 
            [noir.session :as session]
            [noir.cookies :as cookie]
            [noir.validation :as vali]
            [monger.collection :as mc]
            [monger.conversion :as monc])
  (:use [monger.operators :only [$set]]))

;;;; user management methods

;; gravatar URL
(def GRAVATAR_URL "http://www.gravatar.com/avatar/")

;; get the user associated with this
;; session
(defn me []
  (session/get :username))

;; builds a user session
;; this is being used to 
;; populate the session 
;; when a user returns and 
;; a cookie has been identified
(defn user-session-init [username]
  (when (not (session/get :username))
    (session/put! :username username)))

;; grab a user from the database
(defn pull-user [username]
  (mc/find-one-as-map "users" {:username username})) 


;; creates a user and automatically logs them in
(defn create-user [{:keys [email username password] :as user}]
  (if (mc/insert "users" {:email email
                          :username username 
                          :password (crypt/encrypt password)})
    (session/put! :username username)
    (vali/set-error :username "Could not create user.")))


;; retrieve the stored password
(defn lookup-password [username]
  (mc/find-one-as-map "users" {:username username}))

;; checks that the provided username/password combo is
;; valid
(defn u-p-combo-good? [username password]
  (let [{stored-pass :password} (pull-user username)]
    (and stored-pass 
         (crypt/compare password stored-pass))))

;; authenticate a user
(defn login! [{:keys [username password] :as user}]
  (if (u-p-combo-good? username password)
    (session/put! :username username)
    (vali/set-error :username "Invalid username/password combo")))

;; google music info (tokens) to the user info
(defn add-gmusic-info [username 
                       {xt :xt sjsaid :sjsaid auth :Auth}]
  (mc/update "users" {:username username}
             {$set
              {:xt xt
               :sjsaid sjsaid
               :Auth auth}}))

;; retrieve google music info (tokens) 
(defn get-gmusic-info [username]
  (let [userinfo (pull-user username)]
    {:xt (:xt userinfo)
     :sjsaid (:sjsaid userinfo)
     :Auth (:Auth userinfo)}))

;; determine if a user is linked to Google Music
(defn gmusic-linked? [username]
  (let [userinfo (pull-user username)]
    (and
      (:xt userinfo)
      (:sjsaid userinfo)
      (:Auth userinfo))))

; import user library
(defn importUserLibrary [username authSession]
  (let [userTracks (gmusic/getUserLibrary authSession)]
    (mc/insert-batch "library"
                     (map #(assoc % :username username) userTracks))))

;; generate a URL to grab an image from gravatar
(defn get-gravatar-url []
  (let [userinfo (pull-user (me))]
    (str GRAVATAR_URL
         (util/md5 (clojure.string/lower-case
                     (clojure.string/trimr
                       (:email userinfo)))))))

;; logs a user out:
;; 1. clears session
(defn logout! []
  (session/clear!))


;;;; mobile user management ;;;;

;; mobile login will not build a session for the user, but will 
;; instead build a cookie to be used for future requests
(defn mobile-login! [{:keys [username password] :as user}]
  (if (u-p-combo-good? username password) 
    {:authresult true}
    {:authresult false}))
  

