(ns tuneramblr.test.models.gmusic
    (:use [tuneramblr.models.gmusic]
          [clojure.test]))

;; test some stuff that has to do with the
;; auth token retrieval
(deftest test-tokens []
  (let [response (makeAuthRequest 
                   "jdoe@gmail.com"
                   "password")]
    (let [tokens (getAuthTokens response)]
      (println (str "Returned Keys: " (keys tokens)))
      (is (:LSID tokens))
      (is (:Auth tokens))
      (is (:SID tokens)))))

(deftest test-oauth-url []
  ; TODO: actually write this test
  (is (= true true)))

