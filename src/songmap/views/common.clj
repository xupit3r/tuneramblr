(ns songmap.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]])) 

(defpartial layout [& content]
  (html5
    [:head
     [:title "songmap"]
     (include-css "/css/screen.css"
                  "/css/ie.css"
                  "/css/reset.css" 
                  "/css/songmap.css"
                  "/css/overcast.css"
                  "http://fonts.googleapis.com/css?family=Chelsea+Market")
     (include-js "http://maps.google.com/maps/api/js?sensor=false"
                 "/js/jquery.js"
                 "/js/jquery-ui.js"
                 "/js/app.js" 
                 "/js/map.js" 
                 "/js/handlers.js" 
                 "/js/ui.js") ]
    [:body
     [:div#wrapper
      content]]))


(defpartial gen-ul [lst]
  (html5
    [:ul
     (for [el lst]
       [:li el])]))

(defpartial gen-ol [lst]
  (html5
    [:ol
     (for [el lst]
       [:li el])]))
