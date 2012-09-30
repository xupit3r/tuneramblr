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
     :blue.monday.css
     :jquery.js
     :jplayer.js
     :jstz.js
     :bootstrap.js
     :tuneramblr.js
     :listen.js]))

;; define a user space specific layout
(defpartial layout [title location & content]
  (html5
    (build-listen-head title)
    [:body
     [:div {:class "container"}
      (common/build-nav-bar (umanage/me) location)
      [:div.row-fluid
       [:div#audio_content.offset3
        content]]]]))

;; defines the markup for a jPlayer
(defpartial jplayer-layout []
   [:div#jquery_jplayer_1.jp-jplayer ]
   [:div#jp_container_1.jp-audio
    [:div.jp-type-single
     [:div.jp-gui.jp-interface
      [:ul.jp-controls
       [:li 
        [:a.jp-play 
         {:href "javascript:;" :tabindex 1} "Play"]]
       [:li 
        [:a.jp-pause 
         {:href "javascript:;" :tabindex 1} "Pause"]]
       [:li 
        [:a.jp-stop 
         {:href "javascript:;" :tabindex 1} "Stop"]]
       [:li 
        [:a.jp-mute 
         {:href "javascript:;" :tabindex 1 :title "Mute"} "Mute"]]
       [:li 
        [:a.jp-unmute 
         {:href "javascript:;" :tabindex 1 :title "Unmute"} "Unmute"]]
       [:li 
        [:a.jp-volume-max 
         {:href "javascript:;" :tabindex 1 :title "Max Volume"} "Max Volume"]]]
      [:div.jp-progress
       [:div.jp-seek-bar
        [:div.jp-play-bar ]]]
      [:div.jp-volume-bar
       [:div.jp-volume-bar-value ]]
      [:div.jp-time-holder
       [:div.jp-current-time ]
       [:div.jp-duration ]
       [:ul.jp-toggles
        [:li.jp-repeat 
         [:a 
          {:href "javascript:;" :tabindex 1 :title="Repeat"} "Repeat"]]
        [:li.jp-repeat 
         [:a 
          {:href-off "javascript:;" :tabindex 1 :title="Repeat Off"} "Repeat Off"]]]]]
     [:div.jp-title
      [:ul
       [:li#jp-track-title ]
       [:li#jp-track-artist ]
       [:li#loading_div "&nbsp;"]]]
     [:div.jp-no-solution
      [:span "Update Required"]
      "To play the media you will need to either update your browser to a recent version or update your"
      [:a {:href "http://get.adobe.com/flashplayer"} "Flash plugin."]]]]) 
      

;; listen view (metadata + player)
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
       [:span.metad_text ]]]]
    (jplayer-layout)))

;; get the audio for the current
;; situation
(defpage "/user/listen/get/audio" {:keys [lat lng curtime tz]}
  (let [gmSession (umanage/get-gmusic-info (umanage/me))
         winfo  (->> 
                     (weather/weather? lat lng)
                     (weather/prettyweather))
            linfo (location/formatted-address? lat lng)
            tinfo (song/get-discrete-time (Long/valueOf curtime) tz)]
    (let [atrack (song/applic-track winfo {:lat lat
                                           :lng lng} tinfo)
          authSession     (if (not (gmusic/goodSession? gmSession))
                            (let [newAuth (gmusic/getNewAuthSession gmSession)]
                              (umanage/add-gmusic-info (umanage/me) newAuth) newAuth)
                            gmSession)]
      (let [sresults (gmusic/songSearch (:title atrack) authSession)]
        (let [track (first (:songs sresults))]
          (let [playUrlResp (gmusic/songPlayUrl (:id track) authSession)]
          (response/json
            {:url (:url playUrlResp)
             :track track
             :weather winfo
             :location linfo
             :time tinfo})))))))