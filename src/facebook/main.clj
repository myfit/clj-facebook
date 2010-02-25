(ns facebook.main
  (:use facebook.client)
  (:gen-class))

(defn- main [& args]
  (let [conn (make-facebook-connection "asdas" "dasaas")]
    (with-facebook conn
      (application-get-public-info))))