(ns tuneramblr.views.main
  (:require [tuneramblr.views.common :as common]
            [tuneramblr.models.user.umanage :as umanage])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(def NOT_LOGGED_IN_MESSAGE "Either you forgot to login or you need to join tuneramblr!")

;; layout for logged in users
(defn layout-logged-in [username]
  (common/layout
    [:div {:id "ui_body_wrapper", :class "container"}
     [:div {:id "ui_body_main", :class "span-24 last"}
      [:div {:id "ui_head"}
       [:div {:class "span-24 last"}
        [:h1 {:id "banner"} [:img {:src "/img/tuneramblr_logo.png"}]]
        [:div {:id "functions"}
         [:div {:class "fn_btn"}
          [:a {:id "fn_logout"
               :href "/user/logout"}
           (str "Logout as " username)]]
         [:div {:class "fn_btn"} 
          [:a {:id "fn_about"
               :href "javascript:APP.about()"} "About tuneramblr"]]]]]
      [:div {:id "ui_body"}
       [:div {:id "body_tabs"}
        [:ul
         [:li [:a {:href "#tab-autogen"} "Use Current Conditions"]]
         [:li [:a {:href "#tab-usergen"} "Pick Your Posion"]]]
        [:div {:id "tab-autogen"} ]
        [:div {:id "tab-usergen"}
         [:div
          [:div {:id "playlists"} "&nbsp;"]]
         [:div
          [:div {:id "btn_playlist", :class "fn_btn"}
           [:a {:id "fn_playlist"} "Generate a Playlist"]]]
         [:div
          [:div {:id "metacloud"}
           [:div {:id "cloud_holder", :style "width:300px;height: 200px"}]]]
         [:div
          [:div {:id "img_grid", :style "200px;height: 300px"}]]]]]
      [:div {:id "ui_foot"}
       [:div {:class "span-24 last"}
        [:div {:id "tuneramblr_copy"}
         "&copy; 2012 Joe D'Alessandro"]
        [:a {:id "back_labs",
             :href "http://www.backgroundlabs.com",
             :title="Background Labs"}
         [:img {:src "http://www.backgroundlabs.com/images/backgroundlabs88x15.gif", 
                :border="0",
                :alt="Background Labs"}]]]]]]))

;; layout for non-logged in users
(defn layout-not-logged-in [username]
  (common/layout
    [:div {:id "ui_body_wrapper", :class "container"}
     [:div {:id "ui_body_main", :class "span-24 last"}
      [:div {:id "ui_head"}
       [:div {:class "span-24 last"}
        [:h1 {:id "banner"} [:img {:src "/img/tuneramblr_logo.png"}]]
        [:div {:id "functions"}
         [:div {:class "fn_btn"}
          [:a {:id "fn_acct_create"
               :href "/user/add"} "Create Account"]]
         [:div {:class "fn_btn"}
          [:a {:id "fn_login"
               :href "/user/login"} "Login"]]
         [:div {:class "fn_btn"} 
          [:a {:id "fn_about"
               :href "javascript:APP.about()"} "About tuneramblr"]]]]]
      [:div {:id "ui_body"}
       [:div {:class "span-24 last"}
        [:div {:id "join_message", :style "width:300px"}
         [:p {:class "jmessage"}
          NOT_LOGGED_IN_MESSAGE]]]]
      [:div {:id "ui_foot"}
       [:div {:class "span-24 last"}
        [:div {:id "tuneramblr_copy"}
         "&copy; 2012 Joe D'Alessandro"]
        [:a {:id "back_labs",
             :href "http://www.backgroundlabs.com",
             :title="Background Labs"}
         [:img {:src "http://www.backgroundlabs.com/images/backgroundlabs88x15.gif", 
                :border="0",
                :alt="Background Labs"}]]]]]]))


;; the main page for the app.
(defpage "/" []
  (let [username (umanage/me)]
    (if username
      (layout-logged-in username)
      (layout-not-logged-in username))))
