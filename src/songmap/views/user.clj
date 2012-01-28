(ns songmap.views.user
  (:require [songmap.models.user.umanage :as umanage]
            [noir.response :as response]
            [noir.validation :as vali]
            [songmap.views.common :as common])
  (:use [noir.core :only [defpage defpartial render]]
        [hiccup.form-helpers]
        [hiccup.core :only [html]]
        [hiccup.page-helpers :only [html5]]))


;;;; handling user login/logout and creation activities


;;;; user creation ;;;;

;; some basic user rules
(def MIN-USERNAME-LENGTH 5)
(def MAX-USERNAME-LENGTH 20)
(def MIN-PASSWORD-LENGTH 6)


;; TODO: REMOVE ME! ;;
(def LOCK-USER-CREATION true)

;; define a user specific layout
;; (this layout will be used in 
;;  conjunction with all of the user 
;;  forms)
(defpartial layout [title & content]
  (html5
    [:head
     [:title title]]
    [:body 
     content]))

;; validate user form
(defn valid? [{:keys [email username password re-password]}]
  (vali/rule (vali/has-value? email)
             [:email "We require an email address, please enter one."])
  (vali/rule (vali/is-email? email)
             [:email "Please enter a valid email address."])
  (vali/rule (vali/has-value? username)
             [:username "We require an username, please enter one."])
  (vali/rule (vali/min-length? username MIN-USERNAME-LENGTH)
             [:username (str 
                          "We require that your username be a minimum of " 
                          MIN-USERNAME-LENGTH 
                          " characters long.")])
  (vali/rule (vali/max-length? username MAX-USERNAME-LENGTH)
                          [:username (str 
                          "We require that your username be a maximum of " 
                          MAX-USERNAME-LENGTH 
                          " characters long.")])
  (vali/rule (vali/min-length? password MIN-PASSWORD-LENGTH)
                          [:password (str 
                          "We require that your password be a minimum of " 
                          MIN-PASSWORD-LENGTH 
                          " characters long.")])
  (vali/rule (not (= password re-password))
             [:re-password "The entered passwords must match!"])
  (not (vali/errors? :email :username :password :re-password)))

;; our error partial
(defpartial error-disp [[first-error]]
  [:p.error first-error])

(defpartial user-fields [{:keys [email username password re-password]}]
  (vali/on-error :email error-disp)
  (label "email" "Email address: ")
  (text-field "email" email)
  (vali/on-error :username error-disp)
  (label "username" "Desired username: ")
  (text-field "username" username)
  (vali/on-error :password error-disp)
  (label "password" "Enter desired password: ")
  (password-field "password" nil)
  (label "password" "Re-enter password: ")
  (password-field "password" nil)
  (vali/on-error :re-password error-disp))


;; GET user creation form
(defpage  "/user/add" {:as user}
  (layout
    "new songmap account"
    (form-to [:post "/user/add"]
             (user-fields user)
             (submit-button "Create user"))))
  

;; handle the user creation POST
(defpage [:post "/user/add"] {:as user}
  (if (valid? user)
    (layout
      "User Created!"
      (if LOCK-USER-CREATION
        [:p "User creation is locked down."]
        [:p (str "The user "
                 (umanage/create-user
                   (:email user)
                   (:username user)
                   (:password user))
                 " has been created!")]))
    (render "/user/add" user)))
  
  
  
 
;; user login
(defpage "/user/login/page" {}
  (html5
    [:table
     [:tr
      [:td {:class "label"} "username:&nbsp;"]
      [:td [:input {:type "text"
                    :name "username"}]]]
     [:tr
      [:td {:class "label"} "password: &nbsp;"]
      [:td [:input {:type "password"
                    :name "password"}]]]]
    [:input {:type "button"
             :value "Login"}]))

