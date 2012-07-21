(ns tuneramblr.dbdef
  (:require [somnium.congomongo :as cm]
            [tuneramblr.trprops :as props]
            [monger.core :as mg]))

;; db properties keys
(def MONGO_DBNAME_PROP :tr.mongo.dbname)
(def MONGO_HOST_PROP :tr.mongo.host)
(def MONGO_PORT_PROP :tr.mongo.port)
(def MONGO_USERNAME_PROP :tr.mongo.username)
(def MONGO_PASSWORD_PROP :tr.mongo.password)

;; FIXME: for the time being, I am going to just 
;; construct the full mongo DB uri for monger.
;; I plan to eventually drop congomongo and 
;; move everything over to monger, at which time 
;; I will fix this.
(def MONGO_DB_URI (str
                    "mongodb://"
                    (props/read-str-prop MONGO_USERNAME_PROP) ":"
                    (props/read-str-prop MONGO_PASSWORD_PROP) "@"
                    (props/read-str-prop MONGO_HOST_PROP) ":" 
                    (props/read-int-prop MONGO_PORT_PROP) "/"
                    (props/read-str-prop MONGO_DBNAME_PROP)))


;; define our database connection
(def db-conn (cm/make-connection (props/read-str-prop MONGO_DBNAME_PROP)
                              :host (props/read-str-prop MONGO_HOST_PROP)
                              :port (props/read-int-prop MONGO_PORT_PROP)))

;; initialize database stuff
(defn init-db []
  (println "Initializing CongoMongo...")
  (cm/set-connection! db-conn)
  (cm/authenticate db-conn 
                   (props/read-str-prop MONGO_USERNAME_PROP)
                   (props/read-str-prop MONGO_PASSWORD_PROP))
  (println "Initializing Monger...")
  (mg/connect-via-uri! MONGO_DB_URI))

