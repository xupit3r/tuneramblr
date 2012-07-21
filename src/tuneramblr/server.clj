(ns tuneramblr.server
  (:require [noir.server :as server]
            [tuneramblr.dbdef :as db]
            [monger.core :as mg]
            [monger.ring.session-store :as msession]))

(server/load-views "src/tuneramblr/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))
        mguri (get (System/getenv) 
                   "MONGODB_URI" 
                   "mongodb://127.0.0.1/monger-test4")]
    ; initialize the database
    (db/init-db)
    ;connect monger to the db (session management at this time)
    (mg/connect-via-uri! mguri)
    (server/start port {:mode mode
                        :ns 'tuneramblr
                        :session-store (msession/monger-store "sessions")})))

