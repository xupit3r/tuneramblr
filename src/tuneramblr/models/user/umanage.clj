(ns tuneramblr.models.user.umanage
  (:require [tuneramblr.models.util :as util] 
            [noir.util.crypt :as crypt] 
            [noir.session :as session]
            [noir.cookies :as cookie]
            [noir.validation :as vali]
            [monger.collection :as mc]
            [monger.conversion :as monc]))

;;;; user management methods

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
             {:xt xt
              :sjsaid sjsaid
              :Auth auth}))

;; retrieve google music info (tokens) 
(defn get-gmusic-info [username]
  (let [userinfo (pull-user username)]
    {:xt (:xt userinfo)
     :sjsaid (:sjsaid userinfo)
     :Auth (:Auth userinfo)}))
  

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
  

