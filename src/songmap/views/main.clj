(ns songmap.views.main
  (:require [songmap.views.common :as common]
            [songmap.models.user.umanage :as umanage])
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
  (let [username (umanage/me)]
    (common/layout
      [:div {:id "ui_head"}
       [:div {:class "span-24 last"}
        [:h1 {:id "banner"} "SONGMAP"]
        [:div {:id "functions"}
         (when (not username)
           [:div {:class "fn_btn"}
            [:a {:id "fn_acct_create"
                 :href "/user/add"} "Create Account"]])
         [:div {:class "fn_btn"}
          (if username
            [:a {:id "fn_logout"
                 :href "/user/logout"} 
             (str "Logout  as " username)]
            [:a {:id "fn_login"
                 :href "/user/login"} "Login"])]
         [:div {:class "fn_btn"} 
          [:a {:id "fn_settings"} "Settings"]]
         [:div {:class "fn_btn"} 
          [:a {:id "fn_about"} "About Songmap"]]]]
       [:div {:class "span-24 last"}
        [:h3 {:id "subbanner"} "your music, your world"]]]
      [:div {:id "ui_body"}
       [:div {:class "span-7", :style "height: 700px"}
        [:div {:id "metaside"}
         (if username
           [:h3 (str username "'s Metadata")]
           [:h3 "Metadata Near You"])]]
       [:div {:class "span-17 last"}
        [:div {:id "map", :style "width: 100%;height: 700px"}]]
       [:div {:class "span-17 last prepend-7"}
        [:div {:id "playlist"}
         (if username
           [:h3 (str username "'s Playlists")]
           [:h3 "Playlists Near You"])]]]
      [:div {:id "ui_foot"}
       [:div {:class "container"}
        [:p {:id "footer_copy"}
         "&copy; 2012 Joe D'Alessandro"]]])))
