(ns tuneramblr.dbdef
  (:use somnium.congomongo
        tuneramblr.trprops))

;; db properties keys
(def MONGO_DBNAME_PROP :tr.mongo.dbname)
(def MONGO_HOST_PROP :tr.mongo.host)
(def MONGO_PORT_PROP :tr.mongo.port)
(def MONGO_USERNAME_PROP :tr.mongo.username)
(def MONGO_PASSWORD_PROP :tr.mongo.password)

;; define our database connection
(def db-conn (make-connection (read-str-prop MONGO_DBNAME_PROP)
                              :host (read-str-prop MONGO_HOST_PROP)
                              :port (read-int-prop MONGO_PORT_PROP)))

;; initialize mongo
(defn init-db []
  (println ">>> initializing Mongo DB connection...")
  (set-connection! db-conn)
  (println ">>> connection initialized!")
  (println (str ">>> authenticated: " 
                (authenticate db-conn 
                              (read-str-prop MONGO_USERNAME_PROP)
                              (read-str-prop MONGO_PASSWORD_PROP)))))

