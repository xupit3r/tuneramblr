(ns tuneramblr.views.gmusic
  (:require [tuneramblr.views.common :as common]
            [tuneramblr.models.gmusic :as gmusic]
            [tuneramblr.models.user.umanage :as umanage]
            [noir.response :as response]
            [noir.validation :as vali])
  (:use [noir.core :only [defpage defpartial render]]
        [hiccup.form]
        [hiccup.core :only [html]]
        [hiccup.page :only [html5 include-js]]))

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
    (submit-button {:class "btn"} "Submit")]])

;; user account management page
(defpage  "/user/gmusic" {:as user}
  (layout
    "Link to Google Music"
    :user-gmusic
    (form-to {:class "form-horizontal"}
             [:post "/user/gmusic"]
             (gmusic-fields user))))

;; perform the addition of the google credentials
;; to the datastore
(defn process-add [auths]
  (if (validGoogleLogin auths)
    (do
      (umanage/add-gmusic-info
        (umanage/me) 
        auths)
      (umanage/importUserLibrary (umanage/me) auths)
      true)
    (do 
      (vali/set-error :g-username "Bad account info.") false)))
    
;; process a google credentials submission request
(defpage [:post "/user/gmusic"] {:as gmi}
  (if (gmusic-valid? gmi)
    (try
      (if (process-add 
            (gmusic/loginToPlay 
              (:g-username gmi) 
              (:g-password gmi)))
        (response/redirect "/user/listen")
        (render "/user/gmusic" gmi))
    (catch Exception e
      (do
        (vali/set-error :g-username "Bad account info.")
        (render "/user/gmusic" gmi))))
    (render "/user/gmusic" gmi)))

;; handle the submission of google music login
;; credentials from the google music modal
(defpage [:post "/user/gmusic/login/modal/submit"] {:as gmi}
  (try
    (if (process-add 
          (gmusic/loginToPlay 
            (:g-username gmi) 
            (:g-password gmi)))
      (response/json {:gsession true})
      (response/json {:gsession false}))
    (catch Exception e
      (do
        (vali/set-error :g-username "Bad account info.")
        (response/json {:gsession false})))))

;; display a modal dialog for the user
;; login to Google Music
(defpage "/user/gmusic/login/modal" {}
  (html5
    (form-to {:id "gmusic-modal-form" :class "form-horizontal"}
             [:post ""]
             (gmusic-fields {}))
    (include-js "/js/gmusic-modal.js")))
