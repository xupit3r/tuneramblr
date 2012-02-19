(ns songmap.models.user.umanage
  (:require [songmap.models.util :as util] 
            [noir.util.crypt :as crypt] 
            [noir.session :as session]
            [noir.cookies :as cookie]
            [noir.validation :as vali])
  (:use somnium.congomongo))

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
  (fetch-one :users :where {:username username}))


;; creates a user and automatically logs them in
(defn create-user [{:keys [email username password] :as user}]
  (if (insert! :users {:email email
                       :username username 
                       :password (crypt/encrypt password)})
    (session/put! :username username)
    (vali/set-error :username "Could not create user.")))


;; retrieve the stored password
(defn lookup-password [username]
  (fetch-one :users :where {:username username}))

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


;; logs a user out:
;; 1. clears session
(defn logout! []
  (session/clear!))


;;;; mobile user management ;;;;

;; mobile login will not build a session for the user, but will 
;; instead build a cookie to be used for future requests
(defn mobile-login! [{:keys [username password] :as user}]
  (when (u-p-combo-good? username password) 
    (cookie/put! :username username)
    (cookie/put! :mobile 1)))

;; logout of the mobile application will be
;; consist of desctroying the cookie on the
;; client side
  

