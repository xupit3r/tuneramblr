(ns songmap.dbdef
  (:use somnium.congomongo))

;; define our database connection
;; this namespace can be included in
;; whatever models i define (awesome!)
(def db-conn (make-connection "app2658583" 
                         :host "staff.mongohq.com"
                         :port 10028))
  
(defn init-db []
  (println ">>> initializing Mongo DB connection...")
  (set-connection! db-conn)
  (println ">>> connection initialized!")
  (println (str ">>> authenticated: " (authenticate db-conn "test" "test"))))

