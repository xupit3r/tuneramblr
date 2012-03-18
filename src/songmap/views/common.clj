(ns songmap.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

;; possible files to include (css, js, etc.)
(def includes 
  {:blueprint (include-css "/css/screen.css" "/css/ie.css")
   :songmap.css (include-css "/css/songmap.css")
   :forms.css (include-css "/css/forms.css")
   :titan-font (include-css "http://fonts.googleapis.com/css?family=Titan+One")
   :chelsea-font (include-css "http://fonts.googleapis.com/css?family=Chelsea+Market")
   :sonsie-font (include-css "http://fonts.googleapis.com/css?family=Sonsie+One")
   :leaflet (include-js "http://code.leafletjs.com/leaflet-0.3.1/leaflet.js")
   :leaflet-css (include-css "http://code.leafletjs.com/leaflet-0.3.1/leaflet.css")
   :jquery (include-js "/js/jquery.js")
   :jquery.ui (include-js "/js/jquery-ui.js")
   :jqcloud (include-js "/js/jqcloud.js")
   :jqcloud-css (include-css "/css/jqcloud.css")
   :datatables (include-js "/js/datatables.js")
   :table-css (include-css "/css/table.css")
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
    (build-head "tuneramblr"
                [:blueprint
                :songmap.css
                :titan-font
                :sonsie-font
                :jquery
                :jqcloud
                :jqcloud-css
                :datatables
                :table-css
                :app.js
                :map.js
                :leaflet
                :leaflet-css
                :handlers.js
                :ui.js])
    [:body
     [:div#wrapper
      content]]))
