(ns tuneramblr.views.main
  (:require [tuneramblr.views.common :as common]
            [tuneramblr.models.user.umanage :as umanage])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

;; layout for logged in users
(defn layout-logged-in [username]
  (common/layout
    (common/build-nav-bar username :home)
    [:div#stats.row
     [:div.span2
      [:img#user_img.img-rounded
       {:src (umanage/get-gravatar-url)}]
      [:ul.nav.nav-pills.nav-stacked
       [:li#user-tracks.active 
        [:a {:href "#"} "Tracks"]]
       [:li#user-map
        [:a {:href "#"} "Map"]]]]
     [:div.span10
      [:h2 (str username"'s Track History")]
      [:div#tracks_table_container
      [:table#tracks_table.table ]]]]
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
