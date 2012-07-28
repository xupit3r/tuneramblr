(ns tuneramblr.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css include-js html5]]))

;; possible files to include (css, js, etc.)
(def includes 
  {:blueprint (include-css "/css/screen.css" "/css/ie.css")
   :tuneramblr.css (include-css "/css/tuneramblr.css")
   :forms.css (include-css "/css/forms.css")
   :jquery (include-js "/js/jquery.js")
   :jquery.ui (include-js "/js/jquery-ui.js")
   :jquery.ui-css (include-css "/css/jquery-ui.css")
   :jqcloud (include-js "/js/jqcloud.js")
   :jqcloud-css (include-css "/css/jqcloud.css")
   :jquery.masonry.js (include-js "/js/jquery.masonry.js")
   :jquery.imagesloaded.js (include-js "/js/jquery.imagesloaded.js")
   :impromptu (include-js "/js/impromptu.js")
   :impromptu-css (include-css "/css/impromptu.css")
   :datatables (include-js "/js/dataTables.js")
   :table-css (include-css "/css/table.css")
   :app.js (include-js "/js/app.js")
   :images.js (include-js "/js/images.js") 
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
                :jquery
                :impromptu
                :impromptu-css
                :datatables
                :table-css
                :app.js
                :images.js
                :handlers.js
                :ui.js])
    [:body
     [:div#wrapper content]
     [:div {:class "modal"} ]]))
