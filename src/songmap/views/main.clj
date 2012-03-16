(ns songmap.views.main
  (:require [songmap.views.common :as common]
            [songmap.models.user.umanage :as umanage])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

;; test space (eventually the main page)
(defpage "/" []
  (let [username (umanage/me)]
    (common/layout
      [:div {:id "ui_head"}
       [:div {:class "span-24 last"}
        [:h1 {:id "banner"} "tuneramblr"]
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
          [:a {:id "fn_about"} "About tuneramblr"]]]]
       [:div {:class "span-24 last"}
        [:h3 {:id "subbanner"} "your music, your world"]]]
      [:div {:id "ui_body"}
       [:div {:class "span-24 last"}
        [:div {:id "metacloud", :style "width:100%;height: 150px"}
         [:div {:id "cloud_holder", :style "width:100%;height: 200px"}]]]
       [:div {:class "span-24 last"}
        [:div {:id "map", :style "width: 100%;height: 300px"}]]
       [:div {:class "span-12"}
        [:div {:id "tracks", :style "width:100%"}
         [:div {:id "tracks_holder", :style "height: 300px;overflow-y:auto"}
          [:table {:id "tracks_table", :style "width:100%;height: 300px"}]]]]
       [:div {:class "span-12 last"}
        [:div {:id "playlists", :style "width:100%;height: 300px"}
         [:div {:id "playlists_holder", :style "height: 300px;overflow-y:auto"}
          [:table {:id "playlists_table", :style "width:100%;height: 300px"}]]]]]
      [:div {:id "ui_foot"}
       [:div {:class "container"}
        [:p {:id "footer_copy"}
         "&copy; 2012 Joe D'Alessandro"]]])))
