(ns songmap.views.welcome
  (:require [songmap.views.common :as common]
            [songmap.models.playlist :as playlist]
            [songmap.models.metadata :as metadata])
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
        [:div {:class "fn_btn"}
               [:a {:id "fn_login"} "Login"]]
        [:div {:class "fn_btn"} 
         [:a {:id "fn_settings"} "Settings"]]
        [:div {:class "fn_btn"} 
         [:a {:id "fn_about"} "About"]]]]
     [:div {:class "span-24 last"}
      [:h3 {:id "subbanner"} "your music, your world"]]]
    [:div {:id "ui_body"}
     [:div {:class "span-7", :style "height: 700px"}
      [:div {:id "metaside"}
       (common/gen-ul (metadata/gimme-metadata))]]
     [:div {:class "span-17 last"}
      [:div {:id "map", :style "width: 100%;height: 700px"}]]
     [:div {:class "span-7"}
      [:div [:p "&nbsp;"]]]
     [:div {:class "span-17 last"}
      [:div {:id "playlist"}
       (common/gen-ul (playlist/gimme-playlist))]]]
    [:div {:id "ui_foot"}
     [:div {:class "container"}]]))
