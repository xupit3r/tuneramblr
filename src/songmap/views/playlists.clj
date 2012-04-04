(ns songmap.views.playlists
  (:require [songmap.models.playlist :as playlist]
            [songmap.models.user.umanage :as umanage]
            [noir.response :as response])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))


;;;; playlist interface


;; generates a new playlist given the title 
;; and set of songs to be included.  the response
;; will be of an undecided standard format
(defpage [:post "/playlist/gen"] {:keys [title songs]}
  (response/xml
    (playlist/generate (umanage/me)
                     title
                     songs)))

