(ns tuneramblr.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css include-js html5]]))

;; possible files to include (css, js, etc.)
(def includes 
  {:bootstrap.css (include-css "/css/bootstrap.css")
   :bootstrap-responsive.css (include-css "/css/bootstrap-responsive.css")
   :tuneramblr.css (include-css "/css/tuneramblr.css")
   :datatables.css (include-css "/css/table.css")
   :jqcloud.css (include-css "/css/jqcloud.css")
   :bootstrap.js (include-js "/js/bootstrap.js")
   :bootstrap-carousel.js (include-js "/js/bs-carousel.js")
   :jquery.js (include-js "/js/jquery.js")
   :jqcloud.js (include-js "/js/jqcloud.js")
   :datatables.js (include-js "/js/dataTables.js")
   :app.js (include-js "/js/app.js")
   :images.js (include-js "/js/images.js") 
   :handlers.js (include-js "/js/handlers.js")
   :ui.js (include-js "/js/ui.js")})

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
               :datatables.css
               :jqcloud.css
               :jquery.js
               :bootstrap.js
               :bootstrap-carousel.js
               :jqcloud.js
               :datatables.js
               :app.js
               :images.js
               :handlers.js
               :ui.js]))

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


;; puts together an HTML layout
(defpartial layout [& content]
  (html5
    (build-common-head "tuneramblr")
    [:body
     [:div.container content]]))
