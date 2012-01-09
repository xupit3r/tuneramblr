(ns songmap.views.welcome
  (:require [songmap.views.common :as common])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

;; landing page (for right now)
(defpage "/" []
  (common/layout
    [:div {:id "land-title"} 
     [:h1 "Welcome to Songmap"]
     [:p "We will be up and running soon!"]]))

;; test space (eventually the main page)
(defpage "/test" []
  (common/layout
    [:div {:id "ui_head"}
     [:div {:class "span-24 last"}
      [:h1 {:id "banner"} "SONGMAP"]
      [:div {:id "functions"}
       [:a {:id "fn_login"} "Login"]
       [:a {:id "fn_settings"} "Settings"]
       [:a {:id "fn_about"} "About"]]]
     [:div {:class "span-24 last"}
      [:h3 {:id "subbanner"} "Map your musical interests!"]]]
    [:div {:id "ui_body"}
     [:div {:class "span-7 border", :style "height: 700px"}
      [:div {:id "metaside"}
       [:h3 [:a {:href "#"} "Songs"]]
       [:div {:id "songs"}]
       [:h3 [:a {:href "#"} "Playlists"]]
       [:div {:id "playlists"}]
       [:h3 [:a {:href "#"} "Locations"]]
       [:div {:id "locations"}]
       [:h3 [:a {:href "#"} "Tags"]]
       [:div {:id "tags"}]]]
     [:div {:class "span-17 last"}
      [:div {:id "map", :style "width: 100%;height: 700px"}]]]
    [:div {:id "ui_foot"}
     [:div {:class "container"}]]))
       