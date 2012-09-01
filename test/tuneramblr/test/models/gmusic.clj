(ns tuneramblr.test.models.gmusic
    (:use [tuneramblr.models.gmusic]
          [clojure.test]))

;; test some stuff that has to do with the
;; auth token retrieval
(deftest test-tokens []
  (let [playCookies (loginToPlay "jdoe@gmail.com" "password")]
    (clojure.pprint/pprint playCookies)))

