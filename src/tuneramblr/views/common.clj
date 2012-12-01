(ns tuneramblr.views.common
  (:require [tuneramblr.models.user.umanage :as umanage]
            [noir.validation :as vali])
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css include-js html5]]))

;; async JS load snippet
(defpartial build-async-js [jsfile]
  [:script
   (str "(function(d) {"
        "var js = d.createElement('script');"
        "js.src = '" jsfile "';"
        "(d.head || d.getElementsByTagName('head')[0]).appendChild(js);"
        "}(document));")])

;; possible files to include (css, js, etc.)
(def includes 
  {:bootstrap.css (include-css "/vendor/css/bootstrap.css")
   :bootstrap-responsive.css (include-css "/vendor/css/bootstrap-responsive.css")
   :tuneramblr.css (include-css "/css/tuneramblr.css")
   :jqcloud.css (include-css "/vendor/css/jqcloud.css")
   :jqplot.css (include-css "/vendor/css/jqplot.css")
   :leaflet.css (include-css "/vendor/css/leaflet.css")
   :blue.monday.css (include-css "/vendor/css/blue.monday.css")
   :leaflet.js (include-js "/vendor/js/leaflet.js")
   :bootstrap.js (include-js "/vendor/js/bootstrap.js")
   :jquery.js (include-js "/vendor/js/jquery.js")
   :jplayer.js (include-js "/vendor/js/jplayer.js")
   :bootstrap-carousel.js (include-js "/vendor/js/bs-carousel.js")
   :boostrap-modal.js (include-js "/vendor/js/bs-modal.js")
   :jqcloud.js (include-js "/vendor/js/jqcloud.js")
   :jqplot.js (include-js "/vendor/js/jqplot.js")
   :jqplot-pie.js (include-js "/vendor/js/jqplot-pie.js")
   :jstz.js (include-js "/vendor/js/jstz.js")
   :app.js (include-js "/js/app.js")
   :ttable.js (include-js "/js/ttable.js")
   :timeline.js (include-js "/js/timeline.js")
   :tmap.js (include-js "/js/tmap.js")
   :images.js (include-js "/js/images.js")
   :listen.js (include-js "/js/listen.js")
   :tuneramblr.js (include-js "/js/tuneramblr.js")})

;; builds a representation of the page's head
(defpartial build-head [title to-include]
  [:head
   [:title title]
   (map #(get includes %) 
        to-include)])

;; builds a common representation of the page's head
;; which includes the common necessary libraries
(defpartial build-common-head [title]
  (build-head title
              [:bootstrap.css
               :boostrap-responsive.css
               :tuneramblr.css
               :leaflet.css
               :jqplot.css
               :jquery.js
               :jqplot.js
               :jqplot-pie.js
               :bootstrap.js
               :bootstrap-carousel.js
               :tuneramblr.js
               :app.js
               :images.js]))

;; builds a common representation of the page's head
;; which includes the common necessary libraries
(defpartial build-common-footer []
  [:div {:class "footer"}
   [:p#tuneramblr_copy
    "&copy; 2012 Joe D'Alessandro"]])

;; builds the common navigation bar for the app
(defpartial build-nav-bar [username active]
  [:div.navbar
   [:div.navbar-inner
    [:div.container
     [:a.brand {:href "/"} "tuneramblr"]
     [:ul.nav
      [:li (when (= :home active) {:class "active"}) 
       [:a {:href "/"} "Home"]]
      (when (and username (umanage/gmusic-linked? username))
        [:li (when (= :user-listen active) {:class "active"}) 
         [:a {:href "/user/listen"} "Listen!"]])
      (when (and username (not (umanage/gmusic-linked? username)))
        [:li (when (= :user-gmusic active) {:class "active"})
         [:a {:href "/user/gmusic"} 
          "Link Google Music"]])
      [:li (when (= :login active) {:class "active"}) 
       (if username
         [:a {:href "/user/logout"} 
          (str "Logout " username)]
         [:a {:href "/user/login"} 
          (str "Login")])]]]]])

;; modal dialog
(defpartial build-modal-dialog [modal-id modal-header & modal-body]
  [:div.modal.hide {:id  modal-id}
   [:div.modal-header
    [:button.close {:type "button"
                    :data-dismiss "modal"} "X"]
    [:h3 modal-header]]
   [:div.modal-body {:id (str modal-id "_body")} 
                     modal-body]
   [:div.modal-footer ]])

;; build the control-group-form class
(defn get-control-group-class [field]
  (if 
    (vali/errors? field)
                  {:class "control-group error"}
                  {:class "control-group"}))

;; generic error display layout
(defpartial error-disp [[first-error]]
  [:p.help-inline first-error])

;; puts together an HTML layout
(defpartial layout [& content]
  (html5
    (build-common-head "tuneramblr")
    [:body
     [:div.container content]]))

;; creates a loading div
(defpartial loading-div []
  [:div#loading_div.center 
   {:style "width: 200px"} "&nbsp;"])
