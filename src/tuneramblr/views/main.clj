(ns tuneramblr.views.main
  (:require [tuneramblr.views.common :as common]
            [noir.validation :as vali]
            [tuneramblr.models.user.umanage :as umanage])
  (:use [hiccup.form] 
        [noir.core :only [defpage defpartial]]
        [hiccup.page :only [include-js html5]]))


;; our error partial
(defpartial error-disp [[first-error]]
  [:p.help-inline first-error])

;; build the control-group-form class
(defn get-control-group-class [field]
  (if 
    (vali/errors? field)
                  {:class "control-group error"}
                  {:class "control-group"}))

;; sets up the user settings fields
(defpartial user-setting-fields [userinfo]
  [:div (get-control-group-class :email)
   (label {:class "control-label"} "email" "Email: ")
   [:div {:class "controls"}
    (text-field "email" (:email userinfo))
    (vali/on-error :email error-disp)]]
  [:div (get-control-group-class :username)
   (label {:class "control-label"} "username" "Username: ")
   [:div {:class "controls"}
    (text-field "username" (:username userinfo))
    (vali/on-error :username error-disp)]]
  [:div
   [:div {:class "controls"}
    (submit-button {:class "btn btn-primary"}
                   "Update")]])


;; timeline display
(defpage "/content/timeline" []
  (html5
    [:div
     (get common/includes :timeline.js)
     [:h2 (str (umanage/me) "'s Timeline")]
     [:div#timeline_container ]]))

;; tracks table
(defpage "/content/ttable" []
  (html5
    [:div
     (get common/includes :ttable.js)
     [:h2 (str (umanage/me) "'s Tracks")]
     [:div#tracks_table_container
      [:table#tracks_table.table ]]]))

;; tracks map
(defpage "/content/tmap" []
  (html5
    [:div
     (get common/includes :leaflet.js)
     (get common/includes :tmap.js)
     [:h2 (str (umanage/me) "'s Map")]
     [:div#tracks_map_container
      [:div#tracks_map {:style "height: 500px"}]]]))



;; user creation page
(defpage  "/content/usettings" []
  (html5
    (form-to {:class "form-horizontal"}
             [:post "/user/update"]
             (user-setting-fields 
               (umanage/pull-user (umanage/me))))))
  

;; layout for logged in users
(defn layout-logged-in [username]
  (common/layout
    (common/build-nav-bar username :home)
    [:div#stats.row
     [:div.span2
      [:img#user_img.img-rounded
       {:src (umanage/get-gravatar-url)}]
      [:ul#nav-stack.nav.nav-pills.nav-stacked
       [:li#user-timeline.active 
        [:a {:href "#"} "Timeline"]]
       [:li#user-tracks
        [:a {:href "#"} "Tracks"]]
       [:li#user-map
        [:a {:href "#"} "Map"]]
       [:li#user-settings
        [:a {:href "#"} "Settings"]]]]
     [:div#home_content.span10 ]]
    (common/build-modal-dialog 
      "ti-dialog" 
      "Stats on your track"
      [:div#ti_chart_div {:style "height:267;width:480"} ])
    (common/build-common-footer)))

;; layout for non-logged in users
(defn layout-not-logged-in [username]
  (common/layout
    (common/build-nav-bar username :home)
    [:div.carousel.slid
     [:div.active.item
      [:div.hero-unit.center-text
       [:h1 "tuneramblr"]
       [:p "Music is a journey, find the right stuff for where you are."]]]
     [:div.item
      [:div.row.well
       [:div.span3 "&nbsp;"]
       [:div.span3
        [:img {:src "/img/mobile-screenshot.png"}]]
       [:div.span4
        [:h2 (str "Monitor what you are listening to, where you "
                  "are listening to it, and a multitude of other "
                  "factors (weather, imagery, time of day, etc.) "
                  "using our mobile application.")]]
       [:div.span1 "&nbsp;"]
       [:div.span1 "&nbsp;"]]]
     [:div.item
      [:div.row.well
       [:div.span4 "&nbsp;"]
       [:div.span4 
        [:h2 (str "Listen to music that will fit the current state "
                  "of your world, build smart playlists, and view "
                  "trends about your listening habits.  Learn why "
                  "you enjoy a particular song in a particular situation.")]]
       [:div.span4 "&nbsp;"]]]]
    (common/build-common-footer)))


;; the main page for the app.
(defpage "/" []
  (let [username (umanage/me)]
    (if username
      (layout-logged-in username)
      (layout-not-logged-in username))))
