(ns songmap.models.songs)

(defn get-songs [lat lng]
  [{:lat lat
    :lng lng
    :name "SONG 1"
    :content "this is some content for the marker"}])

(defn add-song [lat lng songname]
  "SONG ADDED")

