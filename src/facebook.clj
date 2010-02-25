(ns facebook
  (:require (clojure.contrib [def :as def])
            (clojure.http [resourcefully :as resourcefully]
                          [client :as client]))
  (:import (org.apache.commons.codec.digest DigestUtils))
  (:gen-class))

(def protoc-default "http")
(def protoc-secure "https")
(def rest-endpoint "://api.facebook.com/restserver.php")

(declare *fb-conn*)

(def/defnk make-facebook-connection [api-key secret :secure true]
  (let [protoc (if (= true secure) protoc-secure protoc-default)]
    (def *fb-conn* {:endpoint (str protoc rest-endpoint)
                    :api_key api-key
                    :secret secret})))

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
                   :call_id "12121"
                   :v "1.0"})]
    (assoc p :sig (calculate-signature p (:secret conn)))))

(defn call-method [method params]
  (let [finalized-params  (finalize-params *fb-conn* method params)]
    (prn finalized-params)
    (resourcefully/with-cookies {}
      (resourcefully/post (:endpoint *fb-conn*)
                          {} finalized-params))))

(defn admin-get-allocation []
  false)

(defn admin-get-app-properties []
  false)

(defn admin-get-metrics []
  false)

(defn admin-get-restriction-info []
  false)

(defn admin-set-app-properties []
  false)

(defn admin-ban-users []
  false)

(defn admin-unban-users []
  false)

(defn admin-get-banned-users []
  false)

(defn application-get-public-info []
  false)

(defn auth-create-token []
  false)

(defn auth-expire-session []
  false)

(defn auth-get-session []
  false)

(defn auth-promote-session []
  false)

(defn auth-revoke-authorization []
  false)

(defn auth-revoke-extended-permission []
  false)

(defn batch-run []
  false)

(defn comments-add []
  false)

(defn comments-get []
  false)

(defn comments-remove []
  false)

(defn connect-get-unconnected-friends-count []
  false)

(defn connect-register-users []
  false)

(defn connect-unregister-users []
  false)

(defn data-get-cookies []
  false)

(defn data-set-cookie []
  false)

(defn events-cancel []
  false)

(defn events-create []
  false)

(defn events-edit []
  false)

(defn events-get []
  false)

(defn events-get-members []
  false)

(defn events-invite []
  false)

(defn events-rsvp []
  false)

(defn fbml-delete-custom-tags []
  false)

(defn fbml-get-custom-tags []
  false)

(defn fbml-register-custom-tags []
  false)

(defn fbml-refresh-img-src []
  false)

(defn fbml-refresh-ref-url []
  false)

(defn fbml-setRefHandle []
  false)

(defn fql-multiquery []
  false)

(defn fql-query [query]
  (call-method "facebook.fql.query" {:query query}))

(defn friends-are-friends [uids1 uids2]
  (call-method "facebook.friends.areFriends"
    {:uids1 uids1 :uids2 uids2}))

(defn friends-get [uid]
  (call-method "facebook.friends.get" {:uid uid}))

(defn friends-get-app-users []
  false)

(defn friends-get-lists []
  false)

(defn friends-get-mutual-friends []
  false)

(defn groups-get []
  false)

(defn groups-get-members []
  false)

(defn intl-get-translations []
  false)

(defn intl-upload-native-strings []
  false)

(defn links-get []
  false)

(defn links-get-stats []
  false)

(defn links-post []
  false)

(defn links-preview []
  false)

(defn livemessage-send []
  false)

(defn message-get-threads-in-folder []
  false)

(defn notes-create []
  false)

(defn notes-delete []
  false)

(defn notes-edit []
  false)

(defn notes-get []
  false)

(defn notifications-get []
  false)

(defn notifications-get-list []
  false)

(defn notifications-mark-read []
  false)

(defn pages-get-info []
  false)

(defn pages-is-admin []
  false)

(defn pages-is-app-added []
  false)

(defn photos-add-tag []
  false)

(defn photos-create-album []
  false)

(defn photos-get []
  false)

(defn photos-get-albums []
  false)

(defn photos-get-tags []
  false)

(defn photos-upload []
  false)

(defn sms-can-send []
  false)

(defn sms-send []
  false)

(defn status-get []
  false)

(defn status-set []
  false)

(defn stream-add-comment []
  false)

(defn stream-add-like []
  false)

(defn stream-get []
  false)

(defn stream-get-comments []
  false)

(defn stream-get-filters []
  false)

(defn stream-publish []
  false)

(defn stream-remove []
  false)

(defn stream-remove-comment []
  false)

(defn stream-remove-like []
  false)

(defn users-get-info []
  false)

(defn users-get-logged-in-user []
  false)

(defn users-get-standard-info []
  false)

(defn users-is-app-user []
  false)

(defn users-is-verified []
  false)

(defn users-set-status []
  false)

(defn video-get-upload-limits []
  false)

(defn video-upload []
  false)