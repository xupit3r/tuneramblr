(ns tuneramblr.views.common
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
  {:bootstrap.css (include-css "/css/bootstrap.css")
   :bootstrap-responsive.css (include-css "/css/bootstrap-responsive.css")
   :tuneramblr.css (include-css "/css/tuneramblr.css")
   :jqcloud.css (include-css "/css/jqcloud.css")
   :jqplot.css (include-css "/css/jqplot.css")
   :bootstrap.js (include-js "/js/bootstrap.js")
   :jquery.js (include-js "/js/jquery.js")
   :bootstrap-carousel.js (build-async-js "/js/bs-carousel.js")
   :boostrap-modal.js (build-async-js "/js/bs-modal.js")
   :jqcloud.js (build-async-js "/js/jqcloud.js")
   :jqplot.js (build-async-js "/js/jqplot.js")
   :jqplot-pie.js (build-async-js "/js/jqplot-pie.js")
   :app.js (build-async-js "/js/app.js")
   :images.js (build-async-js "/js/images.js")})

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
               :jqplot.css
               :jquery.js
               :jqplot.js
               :jqplot-pie.js
               :bootstrap.js
               :bootstrap-carousel.js
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
      [:li (when (= :login active) {:class "active"}) 
       (if username
         [:a {:href "/user/logout"} 
          (str "Logout " username)]
         [:a {:href "/user/login"} 
          (str "Login")])]
      [:li (when (= :user-add active) {:class "active"})
       [:a {:href "/user/add"} "Create an Account"]]]]]])

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


;; puts together an HTML layout
(defpartial layout [& content]
  (html5
    (build-common-head "tuneramblr")
    [:body
     [:div.container content]]))
