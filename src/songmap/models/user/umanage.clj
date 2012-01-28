(ns songmap.models.user.umanage
  (:use somnium.congomongo))

;;;; user management methods


(defn create-user [email username password]
    (if (insert! :users {:email email
                         :username username 
                         :password password})
    username))
  

