(ns songmap.views.playlists
  (:require [songmap.models.playlist :as playlist]
            [songmap.models.user.umanage :as umanage]
            [ring.util.response :as rr]
            [noir.response :as response])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))


;;;; playlist interface


;; generates a new playlist given the title 
;; and set of songs to be included. the response 
;; will be metadata related to the playlist (it 
;; is expected that a link will be generated to 
;; the list)
(defpage [:post "/playlists/gen"] {:keys [title songs]}
  (response/json
    (playlist/generate (umanage/me)
                     title
                     songs)))

;; get all of the user's playslists. if there 
;; are none, respond with an empty JSON map
(defpage [:post "/playlists/ulists"] {}
  (if (not (nil? (umanage/me)))
    (response/json
      (playlist/lists-by-user (umanage/me)))
    (response/json {})))
          

;; retrieve a saved playlist.  be sure to add 
;; the content-disposition header property to 
;; indicate to the browser that it should prompt 
;; to save/open the file
(defpage "/playlists/get/:pname" {pname :pname}
  (let [plist (playlist/get-playlist pname)]
    (rr/header
      (response/xml 
        (:playlist plist))
      "content-disposition"
      (str "attachment; " + (:title plist) + ".xml"))))

