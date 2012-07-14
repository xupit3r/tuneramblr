(ns tuneramblr.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

;; possible files to include (css, js, etc.)
(def includes 
  {:blueprint (include-css "/css/screen.css" "/css/ie.css")
   :tuneramblr.css (include-css "/css/tuneramblr.css")
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
   :jquery.masonry.js (include-js "/js/jquery.masonry.js")
   :jquery.imagesloaded.js (include-js "/js/jquery.imagesloaded.js")
   :impromptu (include-js "/js/impromptu.js")
   :impromptu-css (include-css "/css/impromptu.css")
   :datatables (include-js "/js/dataTables.js")
   :drawium (include-js "http://serve.drawium.com/7371748_1023.js")
   :table-css (include-css "/css/table.css")
   :app.js (include-js "/js/app.js")
   :images.js (include-js "/js/images.js") 
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
                :tuneramblr.css
                :titan-font
                :sonsie-font
                :jquery
                :jqcloud
                :jqcloud-css
                :jquery.masonry.js
                :impromptu
                :impromptu-css
                :datatables
                :drawium
                :table-css
                :app.js
                :images.js
                :map.js
                :leaflet
                :leaflet-css
                :handlers.js
                :ui.js])
    [:body
     [:div#wrapper
      content]]))
