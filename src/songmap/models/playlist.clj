(ns songmap.models.playlist
  (:require [songmap.models.util :as util])
  (:use somnium.congomongo
        hiccup.page-helpers))

;;;; functions for working with playlist
;;;; data in the DB

;; add playlist to the playlists collection
(defn add [data]
  (if (insert! :playlists data)
    {:added true,
     :pname (:pname data),
     :title (:title data),
     :message (str (:title data) " was added to the database")}
    {:added false,
     :title (:title data),
     :message (str (:title data) " was not added to the database")}))

;; retrieve a playlist given a pname (id)
;; note this only returns the play list content
(defn get-playlist [pname]
  (:playlist
    (fetch-one 
      :playlists 
      :where {:pname pname})))

;; generate and save a playlist from 
;; the provided songs
(defn generate [user title songs]
  (if (not (nil? user))
    (add {
          :pname (str user (util/current-time)),
          :playlist (xhtml (map (fn [song]
                                  [:song
                                   [:title (:title song)]
                                   [:artist (:artist song)]
                                   [:album (:album song)]])
                                (vals songs))),
          :title title,
          :user user})
    {:added false,
     :message (str "Anon users cannont generate playlists at this time, sorry :-( ")}))

;; TODO: add generation for anon user



