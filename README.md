# Clojure client for Facebook

The Facebook Platform API translated to idiomatic Clojure.

    (ns myns.myclass
      (:require (facebook [client :as facebook])))

    (defn mymethod []
      (facebook/init-facebook! "yourapikey" "yoursecret")
      (prn (facebook/friends-get 193021930)))