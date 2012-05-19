(ns tuneramblr.test.models.song
  (:use [tuneramblr.models.song]
        [clojure.test])
  (:import (java.util Date Calendar GregorianCalendar TimeZone)))

;; helper function to retrieve the
;; milliseconds representation of a
;; provided date. note: all times will 
;; be in America/New_York time zone.
(defn get-millis [year
                    month
                    day
                    hour
                    minute]
  (let [gregc (new GregorianCalendar
                   year
                   month
                   day
                   hour
                   minute)]
    (.setTimeZone 
      gregc
      (TimeZone/getTimeZone "America/New_York"))
    (.getTimeInMillis gregc)))
    
      

;; test the get-discrete-time
;; function
(deftest test-get-discrete-time []
  (let [mtime (get-millis
                  2012
                  GregorianCalendar/APRIL
                  2
                  8
                  30)
        atime (get-millis
                  2012
                  GregorianCalendar/APRIL
                  2
                  12
                  30)
        etime (get-millis
                2012
                GregorianCalendar/APRIL
                2
                17
                30)]
    (is (= "morning"
           (get-discrete-time mtime)))
    (is (= "afternoon"
           (get-discrete-time atime)))
    (is (= "evening"
           (get-discrete-time etime)))))