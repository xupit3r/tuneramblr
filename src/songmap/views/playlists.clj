(ns songmap.views.playlists
  (:require [songmap.models.playlist :as playlist]
            [songmap.models.user.umanage :as umanage]
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

;; get all of the user's playslists
(defpage [:post "/playlists/ulists"] {}
  (if (not (nil? (umanage/me)))
    (response/json
      (playlist/lists-by-user (umanage/me)))
    (response/json {})))
          

;; retrieve a saved playlist
(defpage "/playlists/get/:pname" {pname :pname}
  (response/xml 
    (playlist/get-playlist pname)))

