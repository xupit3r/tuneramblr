(ns tuneramblr.views.main
  (:require [tuneramblr.views.common :as common]
            [tuneramblr.models.user.umanage :as umanage])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(def NOT_LOGGED_IN_MESSAGE "Either you forgot to login or you need to join tuneramblr!")

;; layout for logged in users
(defn layout-logged-in [username]
  (common/layout
    [:div#ui_body_wrapper {:class "container"}
     [:div#ui_body_main {:class "span-24 last"}
      [:div#ui_head
       [:div {:class "span-24 last"}
        [:h1#banner [:img {:src "/img/tuneramblr_logo.png"}]]
        [:div#functions
         [:div {:class "fn_btn"}
          [:a#fn_logout {:href "/user/logout"}
           (str "Logout as " username)]]
         [:div {:class "fn_btn"} 
          [:a#fn_about {:href "javascript:APP.about()"} 
           "About tuneramblr"]]]]]
      [:div#ui_body
        [:div#autogen 
         [:h5 "Current Conditions"]
         [:div#current_conditions ]]]
      [:div#ui_foot
       [:div {:class "span-24 last"}
        [:div#tuneramblr_copy
         "&copy; 2012 Joe D'Alessandro"]
        [:a#back_labs
         {:href "http://www.backgroundlabs.com",
          :title="Background Labs"}
         [:img {:src "http://www.backgroundlabs.com/images/backgroundlabs88x15.gif", 
                :border="0",
                :alt="Background Labs"}]]]]]]))

;; layout for non-logged in users
(defn layout-not-logged-in [username]
  (common/layout
    [:div#ui_body_wrapper {:class "container"}
     [:div#ui_body_main {:class "span-24 last"}
      [:div#ui_head
       [:div {:class "span-24 last"}
        [:h1#banner [:img {:src "/img/tuneramblr_logo.png"}]]
        [:div#functions
         [:div {:class "fn_btn"}
          [:a#fn_acct_create {:href "/user/add"} 
           "Create Account"]]
         [:div {:class "fn_btn"}
          [:a#fn_login {:href "/user/login"} 
           "Login"]]
         [:div {:class "fn_btn"} 
          [:a#fn_about {:href "javascript:APP.about()"} 
           "About tuneramblr"]]]]]
      [:div#ui_body
       [:div {:class "span-24 last"}
        [:div#join_message {:style "width:300px"}
         [:p {:class "jmessage"}
          NOT_LOGGED_IN_MESSAGE]]
        [:div#mobile-screen 
         [:img {:src "/img/mobile-screenshot.png"}]]]]
      [:div#ui_foot
       [:div {:class "span-24 last"}
        [:div#tuneramblr_copy
         "&copy; 2012 Joe D'Alessandro"]
        [:a#back_labs
         {:href "http://www.backgroundlabs.com",
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
