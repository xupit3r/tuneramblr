(ns songmap.models.user.umanage
  (:require [noir.util.crypt :as crypt] 
            [noir.session :as session]
            [noir.cookies :as cookie]
            [noir.validation :as vali])
  (:use somnium.congomongo))

;;;; user management methods

;; get the username associated with
(defn me []
  (session/get :username))

;; grab a user from the database
(defn pull-user [username]
  (fetch-one :user :where {:username username}))


;; create a user
(defn create-user [{:keys [email username password] :as user}]
  (if (insert! :users {:email email
                       :username username 
                       :password (crypt/encrypt password)})
    (do
      (session/put! :username username)
      (cookie/put! :username username))
    (vali/set-error :username "Could not create user.")))


;; retrieve the stored password
(defn lookup-password [username]
  (fetch-one :users :where {:username username}))

;; authenticate a user
(defn login! [{:keys [username password] :as user}]
  (let [{stored-pass :password} (pull-user username)]
    (if (and stored-pass
             (crypt/compare password stored-pass))
      (do
        (session/put! :username username)
        (cookie/put! :username username))
      (vali/set-error :username "Invalid username/password combo"))))
  

