(ns tuneramblr.server
  (:require [noir.server :as server]
            [tuneramblr.dbdef :as db]
            [monger.ring.session-store :refer [monger-store]]))

(server/load-views "src/tuneramblr/views/")


(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
        ;connect monger to the db (session management at this time)
    ; initialize the database stuff
    (db/init-db)
    (server/start port {:mode mode
                        :ns 'tuneramblr
                        :session-store (monger-store "sessions")})))

