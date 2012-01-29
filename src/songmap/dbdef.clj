(ns songmap.dbdef
  (:use somnium.congomongo))
 
;; initialize mongo
(defn init-db []
  (println ">>> initializing Mongo DB connection...")
  (set-connection! db-conn)
  (println ">>> connection initialized!")
  (println (str ">>> authenticated: " (authenticate db-conn "test" "test"))))

