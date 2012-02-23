(ns songmap.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

;; possible files to include (css, js, etc.)
(def includes 
  {:blueprint (include-css "/css/screen.css" "/css/ie.css")
   :songmap.css (include-css "/css/songmap.css")
   :forms.css (include-css "/css/forms.css")
   :overcast.css (include-css "/css/overcast.css")
   :titan-font (include-css "http://fonts.googleapis.com/css?family=Titan+One")
   :chelsea-font (include-css "http://fonts.googleapis.com/css?family=Chelsea+Market")
   :sonsie-font (include-css "http://fonts.googleapis.com/css?family=Sonsie+One")
   :map-api (include-js "http://maps.google.com/maps/api/js?sensor=false")
   :jquery (include-js "/js/jquery.js")
   :jquery.ui (include-js "/js/jquery-ui.js")
   :app.js (include-js "/js/app.js") 
   :map.js (include-js "/js/map.js") 
   :handlers.js (include-js "/js/handlers.js") 
   :ui.js (include-js "/js/ui.js")})

(defpartial build-head [title to-include]
  [:head
   [:title title]
   (map #(get includes %) to-include)]) 


(defpartial layout [& content]
  (html5
    (build-head "GeoBeat"
                [:blueprint
                :songmap.css
                :titan-font
                :sonsie-font
                :map-api
                :jquery
                :app.js
                :map.js
                :handlers.js
                :ui.js])
    [:body
     [:div#wrapper
      content]]))
