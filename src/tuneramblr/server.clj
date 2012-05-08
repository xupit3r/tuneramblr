(ns songmap.server
  (:require [noir.server :as server]
            [songmap.dbdef :as db]))

(server/load-views "src/songmap/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    ; initialize the database
    (db/init-db)
    (server/start port {:mode mode
                        :ns 'songmap})))

