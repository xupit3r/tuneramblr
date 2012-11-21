(ns tuneramblr.views.gmusic
  (:require [tuneramblr.views.common :as common]
            [tuneramblr.models.gmusic :as gmusic]
            [tuneramblr.models.user.umanage :as umanage]
            [noir.response :as response]
            [noir.validation :as vali])
  (:use [noir.core :only [defpage defpartial render]]
        [hiccup.form]
        [hiccup.core :only [html]]
        [hiccup.page :only [html5 include-css]]))

;; build the control-group-form class
(defn get-control-group-class [field]
  (if 
    (vali/errors? field)
                  {:class "control-group error"}
                  {:class "control-group"}))

;; our error partial
(defpartial error-disp [[first-error]]
  [:p.help-inline first-error])

;; define a user space specific layout
(defpartial layout [title location & content]
  (html5
    (common/build-common-head title)
    [:body
     [:div {:class "container"}
      (common/build-nav-bar (umanage/me) location)
      [:div {:id "form_content"
             :class "well"}
       [:h3 title]
       content]]]))

(defn validGoogleLogin [authSession]
  (and
    (:Auth authSession)
    (:xt authSession)
    (:sjsaid authSession)))

;; validate user form
(defn gmusic-valid? [{:keys [g-username g-password]}]
  (do
    (vali/rule
      (vali/has-value? g-username)
      [:g-username "Enter an email address."])
    (vali/rule
      (vali/is-email? g-username)
      [:g-username "Enter a valid email address."])
    (vali/rule
      (vali/has-value? g-password)
      [:g-password "Enter a password."]))
  (not (vali/errors? :g-username)))

(defpartial gmusic-fields [{:keys [g-username g-password]}]
  [:div (get-control-group-class :g-username)
   (label {:class "control-label"} "g-username" "Google Email: ")
   [:div {:class "controls"}
    (text-field "g-username" g-username)
    (vali/on-error :g-username error-disp)]]
  [:div (get-control-group-class :g-password)
   (label {:class "control-label"} "g-password" "Google Password: ")
   [:div {:class "controls"}
    (password-field "g-password" nil)
    (vali/on-error :g-password error-disp)]]
  [:div
   [:div {:class "controls"}
    (submit-button {:class "btn"} "Update")]])

;; user account management page
(defpage  "/user/gmusic" {:as user}
  (layout
    "Link to Google Music"
    :user-gmusic
    (form-to {:class "form-horizontal"}
             [:post "/user/gmusic"]
             (gmusic-fields user))))

(defpage [:post "/user/gmusic"] {:as gmi}
  (if (gmusic-valid? gmi)
    (try
      (let [authSession (gmusic/loginToPlay 
                          (:g-username gmi) 
                          (:g-password gmi))]
        (if (validGoogleLogin authSession)
          (do
            (umanage/add-gmusic-info
              (umanage/me) 
              authSession)
            (response/redirect "/user/listen"))
          (do
            (vali/set-error :g-username "Bad account info.")
            (render "/user/gmusic" gmi))))
      (catch Exception e
        (do
          (vali/set-error :g-username "Bad account info.")
          (render "/user/gmusic" gmi))))
    (render "/user/gmusic" gmi)))

;; display a modal dialog for the user
;; login to Google Music
(defpage "/user/gmusic/login/modal" {}
  (html5
    (form-to {:class "form-horizontal"}
             [:post "/user/gmusic"]
             (gmusic-fields {}))))
  



