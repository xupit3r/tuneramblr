(ns tuneramblr.test.models.weather  
  (:use [tuneramblr.models.weather]
        [clojure.test]))

(defn test-weather? []
  (let [lat 40.37858996679397
        lng -80.04364013671875]
    (let [cc (-> 
                  (weather? lat lng)
                  (current-conditions?))]
      (println (str "Qualitative: " 
                    (qualitative? cc)))
      (println (str "Temperature: " 
                    (temperature? cc)))
    ; TODO: write an actual test
    (is (= true true)))))
  

