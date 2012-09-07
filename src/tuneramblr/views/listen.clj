(ns tuneramblr.views.listen
  (:require [tuneramblr.views.common :as common]
            [tuneramblr.models.gmusic :as gmusic]
            [tuneramblr.models.weather :as weather]
            [tuneramblr.models.location :as location]
            [tuneramblr.models.song :as song]
            [tuneramblr.models.util :as util]
            [tuneramblr.models.user.umanage :as umanage]
            [noir.response :as response]
            [noir.validation :as vali])
  (:use [noir.core :only [defpage defpartial render]]
        [hiccup.form]
        [hiccup.core :only [html]]
        [hiccup.page :only [html5 include-css]]))

;; builds a common representation of the page's head
;; which includes the common necessary libraries
(defpartial build-listen-head [title]
  (common/build-head 
    title
    [:bootstrap.css
     :boostrap-responsive.css
     :tuneramblr.css
     :jqplot.css
     :jquery.js
     :jplayer.js
     :jqplot.js
     :jqplot-pie.js
     :bootstrap.js
     :bootstrap-carousel.js
     :tuneramblr.js
     :listen.js]))

;; define a user space specific layout
(defpartial layout [title location & content]
  (html5
    (build-listen-head title)
    [:body
     [:div {:class "container"}
      (common/build-nav-bar (umanage/me) location)
      [:div#audio_content
       content]]]))

;; user account management page
(defpage  "/user/listen" {}
  (layout
    "Listen!"
    :user-listen
    [:div#metadata
     [:div#metad_location 
      [:div#metad_location_val 
       [:span.metad_lbl.label "Location"]
       [:span.metad_text ]]]
     [:div#metad_weather
      [:div#metad_weather_val 
       [:span.metad_lbl.label "Weather"]
       [:span.metad_text ]]]
     [:div#metad_time
      [:div#metad_time_val 
       [:span.metad_lbl.label "Time of Day"]
       [:span.metad_text ]]]
     [:div#metad_track
      [:div#metad_track_val 
       [:span.metad_lbl.label "Track Info"]
       [:span.metad_text ]]]]
    [:p#player_controls
      [:a#play {:href "javascript:;"} "Play"] 
      [:a#pause {:href "javascript:;"} "Pause"] " | "
      [:a#stop {:href "javascript:;"} "Stop"] " | "
      [:a#mute {:href "javascript:;"} "Mute"]
      [:a#unmute {:href "javascript:;"} "Unmute"] " | "
      [:span#currentTime ] "/" [:span#duration ]]
    [:div#audio_player ]))

;; get the audio for the current
;; situation
(defpage "/user/listen/get/audio" {:keys [lat lng curtime]}
  (let [authSession (umanage/get-gmusic-info (umanage/me))
         winfo  (->> 
                     (weather/weather? lat lng)
                     (weather/prettyweather))
            linfo (location/formatted-address? lat lng)
            tinfo (song/get-discrete-time (Long/valueOf curtime))]
    (let [atrack (song/applic-track winfo {:lat lat
                                           :lng lng} tinfo)]
      (let [sresults (gmusic/songSearch (:title atrack) authSession)]
        (let [track (first (:songs sresults))]
          (response/json
            {:url (gmusic/songPlayUrl (:id track) authSession)
             :track track
             :weather winfo
             :location linfo
             :time tinfo}))))))