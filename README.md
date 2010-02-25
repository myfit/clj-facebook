# Clojure client for Facebook

The Facebook Platform API translated to idiomatic Clojure.

Example usage:

    (defn get-name-test [fb-uid]
      (make-facebook-connection "yourapikey" "yoursecret")
      (prn (friends-get fb-uid)))

    (defn -main [& args]
      (get-name 183121977))