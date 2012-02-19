(ns songmap.views.user
  (:require [songmap.models.user.umanage :as umanage]
            [noir.response :as response]
            [noir.validation :as vali]
            [noir.session :as session]
            [noir.cookies :as cookie]
            [songmap.views.common :as common])
  (:use [noir.core :only [defpage defpartial render]]
        [hiccup.form-helpers]
        [hiccup.core :only [html]]
        [hiccup.page-helpers :only [html5 include-css]]))


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
    (common/build-head title
                       [:blueprint
                        :songmap.css
                        :forms.css
                        :sonsie-font])
    [:body
     [:div {:class "sm-form"}
      [:h3 title]
      content]]))

;; validate user form
(defn valid? [{:keys [email username password re-password]}]
  (if (not (umanage/pull-user username))
    (do
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
                 [:re-password "The entered passwords must match!"]))
    (vali/set-error :username "This username already exists, :-( "))
  (not (vali/errors? :email :username :password :re-password)))
  
;; our error partial
(defpartial error-disp [[first-error]]
  [:p.error first-error])

(defpartial user-fields [{:keys [email username password re-password]}]
  [:table
   [:tr
    [:td (label "email" "Email address: ")]
    [:td (text-field "email" email)]
    [:td (vali/on-error :email error-disp)]]
   [:tr
    [:td (label "username" "Desired username: ")]
    [:td (text-field "username" username)]
    [:td (vali/on-error :username error-disp)]]
  [:tr
   [:td (label "password" "Enter desired password: ")]
   [:td (password-field "password" nil)]
   [:td (vali/on-error :password error-disp)]]
  [:tr
   [:td (label "password" "Re-enter password: ")]
   [:td (password-field "password" nil)]
   [:td (vali/on-error :re-password error-disp)]]])


;; user creation page (GET)
(defpage  "/user/add" {:as user}
  (layout
    "Create a Songmap Account"
    (form-to [:post "/user/add"]
             (user-fields user)
             (submit-button "Create user"))))
  

;; handle the user creation (POST)
(defpage [:post "/user/add"] {:as user}
  (if (valid? user)
      (if LOCK-USER-CREATION
        [:p "User creation is locked down."]
        (do
          (umanage/create-user user)
          (response/redirect "/test")))
      (render "/user/add" user)))
  
  

;;;; login/logout ;;;;

;; setup user login form content
(defpartial user-login-fields [{:keys [username]}]
  (vali/on-error :username error-disp)
  [:table
   [:tr
    [:td (label "username" "Username: ")]
    [:td (text-field "username" username)]]
   [:tr
    [:td (label "password" "Password: ")]
    [:td (password-field "password" nil)]]])
  
 
;; user login page (GET)
(defpage "/user/login" {:as user}
  (layout
    "Songmap Login"
    (form-to [:post "/user/login"]
             (user-login-fields user)
             (submit-button "Login"))))

;; handle authentication (POST)
(defpage [:post "/user/login"] {:as user}
  (if (umanage/login! user)
    (response/redirect "/test")
    (render "/user/login" user)))

;; log a user out
(defpage "/user/logout" {}
  (umanage/logout!)
  (response/redirect "/test"))


;;;; mobile login/logout logic ;;;;

;; this will either return:
;; 1. a cookie if user was authenticated
;; 2. no cookie if the user was not authenticated
(defpage [:post "user/mobile/login"] {:as user}
  (umanage/mobile-login! user)
  (response/empty))