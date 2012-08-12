(ns tuneramblr.views.main
  (:require [tuneramblr.views.common :as common]
            [tuneramblr.models.user.umanage :as umanage])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(def NOT_LOGGED_IN_MESSAGE "Either you forgot to login or you need to join tuneramblr!")

;; layout for logged in users
(defn layout-logged-in [username]
  (common/layout
    (common/build-nav-bar username :home)
    [:div {:class "carousel slid"}
     [:div {:class "active item"}
      [:div {:class "hero-unit center-text"}
       [:h1 "tuneramblr"]
       [:p "Music is a journey, find the right stuff for where you are."]]]
     [:div {:class "item"}
      [:div {:class "row well"}
       [:div {:class "span3"} "&nbsp;"]
       [:div {:class "span3"}
        [:img {:src "/img/mobile-screenshot.png"}]]
       [:div {:class "span4"}
        [:h2 (str "Monitor what you are listening to, where you "
                  "are listening to it, and a multitude of other "
                  "factors (weather, imagery, time of day, etc.) "
                  "using our mobile application.")]]
       [:div {:class "span1"} "&nbsp;"]
       [:div {:class "span1"} "&nbsp;"]]]
     [:div {:class "item"}
      [:div {:class "row well"}
       [:div {:class "span4"} "&nbsp;"]
       [:div {:class "span4"} 
        [:h2 (str "Listen to music that will fit the current state "
                  "of your world, build smart playlists, and view "
                  "trends about your listening habits.  Learn why "
                  "you enjoy a particular song in a particular situation.")]]
       [:div {:class "span4"} "&nbsp;"]]]]
    [:div {:class "footer"}
      [:p#tuneramblr_copy
       "&copy; 2012 Joe D'Alessandro"]]))

;; layout for non-logged in users
(defn layout-not-logged-in [username]
  (common/layout
    (common/build-nav-bar username :home)
    [:div {:class "carousel slid"}
     [:div {:class "active item"}
      [:div {:class "hero-unit center-text"}
       [:h1 "tuneramblr"]
       [:p "Music is a journey, find the right stuff for where you are."]]]
     [:div {:class "item"}
      [:div {:class "row well"}
       [:div {:class "span3"} "&nbsp;"]
       [:div {:class "span3"}
        [:img {:src "/img/mobile-screenshot.png"}]]
       [:div {:class "span4"}
        [:h2 (str "Monitor what you are listening to, where you "
                  "are listening to it, and a multitude of other "
                  "factors (weather, imagery, time of day, etc.) "
                  "using our mobile application.")]]
       [:div {:class "span1"} "&nbsp;"]
       [:div {:class "span1"} "&nbsp;"]]]
     [:div {:class "item"}
      [:div {:class "row well"}
       [:div {:class "span4"} "&nbsp;"]
       [:div {:class "span4"} 
        [:h2 (str "Listen to music that will fit the current state "
                  "of your world, build smart playlists, and view "
                  "trends about your listening habits.  Learn why "
                  "you enjoy a particular song in a particular situation.")]]
       [:div {:class "span4"} "&nbsp;"]]]]
    [:div {:class "footer"}
      [:p#tuneramblr_copy
       "&copy; 2012 Joe D'Alessandro"]]))


;; the main page for the app.
(defpage "/" []
  (let [username (umanage/me)]
    (if username
      (layout-logged-in username)
      (layout-not-logged-in username))))
