(ns tuneramblr.views.about  
  (:require [noir.response :as response]
            [noir.session :as session]
            [tuneramblr.views.common :as common])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(def ABOUT_TEXT
  (str
    "tuneramblr is an application for collecting and analyzing all kinds of metadata"
    " (weather, images, attitudes, company, etc.) related to your music listening habits."
    " It aims to allow exploration of your musical interests by creating playlists based "
    " on metadata that you may not typically consider."))
    

;; return about text
(defpage "/about" []
  (response/json 
    {:about ABOUT_TEXT}))

