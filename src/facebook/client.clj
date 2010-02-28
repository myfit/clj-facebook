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

(defn apify-method [call-sym]
  (let [call      (name call-sym)
        call-sp   (.split call "-")
        class     (first call-sp)
        method1   (first (rest call-sp))
        method    (if (< 1 (count call-sp))
                      (rest (rest call-sp))
                      ())]
    (format
      "%s.%s%s"
      class
      method1
      (string/join ""
        (map string/capitalize method)))))

(defmacro named-map* [keys]
  `(zipmap (map keyword '~keys) (list ~@keys)))

(defmacro define-method [method args]
  `(defn ~method ~args
     (call-method ~(format "facebook.%s" (apify-method method))
                  ~(zipmap
                       (map
                         #(symbol (str ":" (name %)))
                         args)
                      args))))

(define-method admin-get-allocation [integration_point_name])
(define-method admin-get-app-properties [])
(define-method admin-get-metrics [])
(define-method admin-get-restriction-info [])
(define-method admin-set-app-properties [])
(define-method admin-ban-users [])
(define-method admin-unban-users [])
(define-method admin-get-banned-users [])

(def/defnk init-facebook! [api-key secret :secure true :session-key nil]
  (let [protoc (if (= true secure) protoc-secure protoc-default)]
    (def *fb-conn* {:endpoint (str protoc rest-endpoint)
                    :api_key api-key
                    :secret secret
                    :session_key session-key})))