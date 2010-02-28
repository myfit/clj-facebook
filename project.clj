(defproject clj-facebook "1.0.0-SNAPSHOT" 
  :description "A Clojure client for Facebook Platform." 
  :url "http://github.com/myfit/clj-facebook"
  :source-path "src"
  :javac-fork "true"
  :dependencies [[org.clojure/clojure "1.1.0-master-SNAPSHOT"] 
                 [org.clojure/clojure-contrib "1.1.0-master-SNAPSHOT"]
                 [clojure-http-client "1.0.0-SNAPSHOT"]
                 [commons-codec "1.3"]
                 [clj-json "0.2.0-SNAPSHOT"]]
  :dev-dependencies [[lein-clojars "0.5.0-SNAPSHOT"]
                    [clj-unit "0.1.0-SNAPSHOT"]
                    [lein-javac "0.0.2-SNAPSHOT"]])