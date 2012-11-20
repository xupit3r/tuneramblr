(ns tuneramblr.views.user
  (:require [tuneramblr.models.user.umanage :as umanage]
            [noir.response :as response]
            [noir.validation :as vali]
            [noir.session :as session]
            [tuneramblr.views.common :as common]
            [tuneramblr.models.song :as song]
            [tuneramblr.models.util :as util]
            [tuneramblr.models.weather :as weather]
            [tuneramblr.models.location :as location])
  (:use [noir.core :only [defpage defpartial render]]
        [hiccup.form]
        [hiccup.core :only [html]]
        [hiccup.page :only [html5 include-css]]))

;;;; user creation ;;;;

;; some basic user rules
(def MIN-USERNAME-LENGTH 3)
(def MAX-USERNAME-LENGTH 20)
(def MIN-PASSWORD-LENGTH 6)

;; build the control-group-form class
(defn get-control-group-class [field]
  (if 
    (vali/errors? field)
                  {:class "control-group error"}
                  {:class "control-group"}))

;; define a user space specific layout
;; (this layout will be used in 
;;  conjunction with all of the user 
;;  forms)
(defpartial layout [title location & content]
  (html5
    (common/build-common-head title)
    [:body
     [:div {:class "container"}
      (common/build-nav-bar nil location)
      [:div {:id "form_content"
             :class "well"}
       [:h3 title]
       content]]]))

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
  [:p.help-inline first-error])

(defpartial user-fields [{:keys [email username password re-password]}]
  [:div (get-control-group-class :email)
   (label {:class "control-label"} "email" "Email address: ")
   [:div {:class "controls"}
    (text-field "email" email)
    (vali/on-error :email error-disp)]]
  [:div (get-control-group-class :username)
   (label {:class "control-label"} "username" "Desired username: ")
   [:div {:class "controls"}
    (text-field "username" username)
    (vali/on-error :username error-disp)]]
  [:div (get-control-group-class :password)
   (label {:class "control-label"} "password" "Desired password: ")
   [:div {:class "controls"}
    (password-field "password" nil)
    (vali/on-error :password  error-disp)]]
  [:div (get-control-group-class :re-password)
   (label {:class "control-label"} "password" "Re-enter password: ")
   [:div {:class "controls"}
    (password-field "password" nil)
    (vali/on-error :re-password error-disp)]]
  [:div
   [:div {:class "controls"}
    (submit-button {:class "btn"} "Create user")]])


;; user creation page (GET)
(defpage  "/user/add" {:as user}
  (layout
    "Create a tuneramblr Account"
    :user-add
    (form-to {:class "form-horizontal"}
             [:post "/user/add"]
             (user-fields user))))
  

;; handle the user creation (POST)
(defpage [:post "/user/add"] {:as user}
  (if (valid? user)
    (do
      (umanage/create-user user)
      (response/redirect "/"))
    (render "/user/add" user)))
  
  

;;;; login/logout ;;;;

;; login specific error display
(defpartial login-error-display [[first-error]]
  [:div.alert.alert-error
   (str "&nbsp;" first-error)])

;; setup user login form content
(defpartial user-login-fields [{:keys [username]}]
  (vali/on-error :username login-error-display)
  [:div {:class "control-group"}
   (label {:class "control-label"} "username" "Username: ")
   [:div {:class "controls"}
    (text-field {:class "input-small"}
                "username" 
                nil)]]
  [:div {:class "control-group"}
   (label {:class "control-label"} "password" "Password: ")
   [:div {:class "controls"}
    (password-field {:class "input-small"}
                    "password" 
                    nil)]]
  [:div {:class "control-group"}
   [:div {:class "controls"}
    (submit-button {:class "btn btn-primary"}
                   "Login")]])

 
;; user login page (GET)
(defpage "/user/login" {:as user}
  (layout
    ""
    :login
    [:div.row-fluid
     [:div#login_content.offset3
      (form-to {:class "form-horizontal"}
               [:post "/user/login"]
               (user-login-fields user))]]))

;; handle authentication (POST)
(defpage [:post "/user/login"] {:as user}
  (if (umanage/login! user)
    (response/redirect "/")
    (render "/user/login" user)))

;; log a user out
(defpage "/user/logout" {}
  (umanage/logout!)
  (response/redirect "/"))

;;;; web session setup ;;;;

;; setup a base user session
(defpage [:post "/user/base/tracks"] {:as latlng}
  (let [username (umanage/me)
        songs (song/get-songs-by-username username)]
      (response/json 
        {:freqs (song/build-freqs songs)
         :imgs (song/build-imgs songs)
         :songs (song/merge-tracks songs)})))

;; send back the timeline display data
(defpage "/user/base/timeline" []
  (html5
    (map
      #(vector :div.track_entry
         [:div.track_meta
          [:div.track_date (util/format-date (:tstamp %))]
          [:div.track_name (str (:title %) 
                                " by " (:artist %))]
          [:div (:location %)]
          [:div (:weather %)]]
         (when (not (nil? (:img %)))
           [:img.track_img.img-rounded 
            {:src (str "/image/ugen/" (:img %))}])) 
      (song/get-timeline-data 
        (umanage/me)))))


;;;; mobile login/logout logic ;;;;

;; logs a user in
(defpage [:post "/mobile/login"] {:as user}
  (response/json 
    (umanage/mobile-login! user)))