(ns tuneramblr.test.models.weather  
  (:use [tuneramblr.models.weather]
        [clojure.test]))

(deftest test-weather? []
  (let [lat 40.37858996679397
        lng -80.04364013671875]
    (println (weather? lat lng))
    ; TODO: write an actual test
    (is (= true true))))
  

