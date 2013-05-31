(defproject tuneramblr "0.1.0"
            :description "your music, your world"
            :dependencies [[org.clojure/clojure "1.5.0"]
                           [noir "1.3.0-beta1"]
                           [com.novemberain/monger "1.1.1"]
                           [prxml "1.3.0"]
                           [clj-http "0.5.0"]
                           [http.async.client "0.4.5"]
                           [org.clojure/data.json "0.1.2"]
                           [org.imgscalr/imgscalr-lib "4.2"]]
            :main tuneramblr.server
            :min-lein-version "2.0.0")
