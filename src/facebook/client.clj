(ns facebook.client
  (:require (clojure.contrib [def :as def] [str-utils2 :as string])
            (clojure.http [resourcefully :as resourcefully]
                          [client :as client])
            (clj-json [core :as json]))
  (:import (org.apache.commons.codec.digest DigestUtils))
  (:gen-class))

(def protoc-default "http")
(def protoc-secure "https")
(def rest-endpoint "://api.facebook.com/restserver.php")

(declare *fb-conn*)

(defn md5 [string]
  (DigestUtils/md5Hex string))

(defn named [x]
  (if (instance? clojure.lang.Named x)
    (name x)
    (.toString x)))

(defn- calculate-signature [params secret]
  (md5 (.concat
         (reduce #(format "%s%s=%s" %1 (named (first %2)) (second %2))
           ""
         (sort-by key params))
       secret)))

(defn finalize-params [conn method params]
  (let [p  (merge params
                  {:format "JSON"
                   :api_key (:api_key conn)
                   :method method
                   :call_id (str (System/currentTimeMillis))
                   :v "1.0"})
        p   (if (= (:session_key conn) nil) 
                p
                (assoc p :session_key (:session_key conn)))]
    (assoc p :sig (calculate-signature p (:secret conn)))))

(defn call-method [method params]
  (let [finalized-params  (finalize-params *fb-conn* method params)
        response          (resourcefully/with-cookies {}
                            (resourcefully/post (:endpoint *fb-conn*)
                                                {} finalized-params))]
    (json/parse-string (first (:body-seq response)))))

(defn apify-method [call]
  (let [call-sp   (.split call "-")
        class     (first call-sp)
        method    (rest call-sp)]
    (str
      class "."
      (apply string/capitalize method))))

(defmacro define-method [method map args]
  `(defn ~method []
     false))
(comment
     (call-method 
       ~(format "facebook.%s" map)
       {}))

(def fb-methods
  [['admin-get-allocation "admin.getAllocation" []]
   ['admin-get-app-properties "admin.getAppProperties" []]
   ['admin-get-metrics "admin.getMetrics" []]
   ['admin-get-restriction-info "admin.getRestrictionInfo" []]
   ['admin-set-app-properties "admin.setAppProperties" []]
   ['admin-ban-users "admin.banUsers" []]
   ['admin-unban-users "admin.unbanUsers" []]
   ['admin-get-banned-users "admin.getBannedUsers" []]])

(defn generate-methods []
  (doseq [method fb-methods]
    (define-method (first method) (second method) (second (rest method)))))

(def/defnk make-facebook-connection [api-key secret :secure true :session-key nil]
  (let [protoc (if (= true secure) protoc-secure protoc-default)]
    (def *fb-conn* {:endpoint (str protoc rest-endpoint)
                    :api_key api-key
                    :secret secret
                    :session_key session-key})
    (generate-methods)))