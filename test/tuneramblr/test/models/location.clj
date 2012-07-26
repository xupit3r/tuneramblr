(ns tuneramblr.test.models.location  
  (:use [tuneramblr.models.location]
        [clojure.test]))

(deftest test-weather? []
  (let [lat 40.37858996679397
        lng -80.04364013671875]
    (let [ad (address? lat lng)]
      (println "Address data: " ad)
      (println "Formatted address: " (formatted-address? ad))
    ; TODO: write an actual test
    (is (= true true)))))

