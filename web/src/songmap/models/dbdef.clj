(ns songmap.models.dbdef)

;; define our database connection
;; this namespace can be included in
;; whatever models i define (awesome!)
(def db
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :user "songmap_test"
   :password "songmap_test"
   :subname "//mysql.thejoeshow.net/songmap_test"})

