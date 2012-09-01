(ns tuneramblr.test.models.gmusic
    (:use [tuneramblr.models.gmusic]
          [clojure.test]))

;; testing for google play functionality
(defn test-google-play-functionality []
  (let [authSession (loginToPlay "jdoe@gmail.com" "password")]
    (clojure.pprint/pprint 
      (songSearch "search string" 
                  authSession))
    (clojure.pprint/pprint 
      (songPlayUrl "songId" 
                   authSession))))