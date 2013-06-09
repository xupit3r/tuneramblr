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
        [hiccup.page :only [html5 include-js]]))

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
        content]]
      (common/build-modal-dialog
        "gmusic-modal"
        "Login to Google Music")
      (common/build-modal-dialog
        "watcha-modal"
        "Watcha doing?")]
     (common/build-common-footer)]))

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
       [:ul.love-hate-list
        [:li#love-track
         [:i.icon-thumbs-up]]
        [:li#hate-track
         [:i.icon-thumbs-down]]]]]
     [:div.jp-title
      [:ul
       [:li#jp-track-title ]
       [:li#jp-track-artist ]
       [:li#loading_div "&nbsp;"]]]
     [:div.love-hate-container]
     [:div.jp-no-solution
      [:span "Update Required"]
      "To play the media you will need to either update your browser to a recent version or update your"
      [:a {:href "http://get.adobe.com/flashplayer"} "Flash plugin."]]]]) 
      

;; listen view (metadata + player)
(defpage  "/user/listen" {:keys [mode]}
  (layout
    "Listen!"
    :user-listen
    [:div#track-lookup 
     [:div#track-lookup-progress.progress.progress-striped.active
      [:div#track-lookup-notice.bar {:style "width: 100%"} 
       "Finding the right track for you, please wait!"]]]
    [:div#jp-cont 
     (jplayer-layout)]
    [:div#track-alert.alert.alert-info.center-text ]))

;; fields for the watcha doing? dialog content
(defpartial watcha-fields []
  [:div (common/get-control-group-class :w-doing)
   (label {:class "control-label"} "w-doing" "What are you up to?")
   [:div.controls
    (text-field "w-doing" "")
    (vali/on-error :w-doing common/error-disp)]
   (label {:class "control-label"} "pmode" "Want to Learn?")
   [:div.controls
    (check-box {} "pmode")]]
  [:div
   [:div.controls
    (submit-button {:class "btn"} "Submit")]])

;; content for the watcha doing? dialog
(defpage "/user/listen/watcha/modal" []
  (html5
    (form-to {:id "watcha-modal-form" :class "form-horizontal"}
             [:post ""]
             (watcha-fields))
    (include-js "/js/watcha-modal.js")))

;; check if we have a good google music
;; session
(defpage "/user/listen/check/session" {}
  (response/json
    {:gsession 
     (gmusic/goodSession? 
      (umanage/get-gmusic-info (umanage/me)))}))

;; submit love or hate for a track
(defpage [:post "/user/listen/lovehate"] 
  {:keys [lat lng curtime tz watcha weather location artist title album lovehate playId]}
  (response/json 
   (song/add-song {:username (umanage/me)
                   :lat (Double/valueOf lat)
                   :lng (Double/valueOf lng)
                   :artist artist
                   :title title 
                   :album album 
                   :weather weather
                   :watcha watcha
                   :timezone tz
                   :tstamp (Long/valueOf curtime)
                   :playId playId
                   :ctype lovehate})))


;; get meta data about this current point in time
;; includes: weather, location, more specific time info
;; NOTE: should only be called when it is absolutely 
;; needed. this is a very expensive call to make, it 
;; hits several other services to get its data
(defpage "/user/listen/get/why" {:keys [lat lng]}
  (response/json
   {:weather (->> 
            (weather/weather? lat lng)
            (weather/prettyweather))
    :location (location/formatted-address? lat lng)}))


;; get the audio for the current situation/station
(defpage "/user/listen/get/audio" {:keys [lat lng curtime tz watcha pMode why]}
  (let [gmSession (umanage/get-gmusic-info (umanage/me))]
    (cond
     (not pMode) ; predictor/radio mode
     (let [atrack (song/applic-track 
                   (:weather why) 
                   {:lat lat :lng lng} 
                   (song/get-discrete-time 
                    (Long/valueOf curtime) tz) 
                   watcha)]
       (let [sresults (gmusic/songSearch 
                       (:title atrack) gmSession)]
         (response/json
          {:url (:url (gmusic/songPlayUrl 
                       (:id  (first (:songs sresults))) gmSession))
           :track  (first (:songs sresults))
           :location (:location why)
           :weather (:weather why)
           :gsession true})))
     :else ; learner mode
     (let [atrack (song/getRandomTrack 
                   (umanage/me))]
       (response/json
        {:url (:url (gmusic/songPlayUrl (:playId atrack) gmSession))
         :track atrack
         :location (:location why)
         :weather (:weather why)
         :gsession true})))))
