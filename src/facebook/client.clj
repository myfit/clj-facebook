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
                      (rest (rest call-sp)) ())]
    (format
      "%s.%s%s"
      class
      method1
      (string/join ""
        (map string/capitalize method)))))

(defmacro define-method [method args]
  `(defn ~method ~args
     (call-method ~(format "facebook.%s" (apify-method method))
                  ~(zipmap
                       (map
                         (fn [a]
                           (keyword (name a)))
                         args)
                      args))))

(define-method admin-get-allocation [integration_point_name])
(define-method admin-get-app-properties [properties])
(define-method admin-get-metrics [start_time end_time period metrics])
(define-method admin-get-restriction-info [])
(define-method admin-set-app-properties [properties])
(define-method admin-set-restriction-info [restriction_str])
(define-method admin-ban-users [uids])
(define-method admin-unban-users [uids])
(define-method admin-get-banned-users [])
(define-method application-get-public-info [application_id])
(define-method auth-create-token [])
(define-method auth-expire-session [])
(define-method auth-get-session [])
(define-method auth-promote-session [])
(define-method auth-revoke-authorization [])
(define-method auth-revoke-extended-permission [])
(define-method batch-run [])
(define-method comments-add [])
(define-method comments-get [xid])
(define-method comments-remove [])
(define-method connect-get-unconnected-friends-count [])
(define-method connect-register-users [accounts])
(define-method connect-unregister-users [email_hashes])
(define-method data-get-cookies [])
(define-method data-set-cookie [])
(define-method events-cancel [eid cancel_message])
(define-method events-create [event_info])
(define-method events-edit [eid event_info])
(define-method events-get [uid eids start_time end_time rsvp_status])
(define-method events-get-members [eid])
(define-method events-invite [eid uids personal_message])
(define-method events-rsvp [eid rsvp_status])
(define-method fbml-delete-custom-tags [names])
(define-method fbml-get-custom-tags [app_id])
(define-method fbml-register-custom-tags [tags])
(define-method fbml-refresh-img-src [url])
(define-method fbml-refresh-ref-url [url])
(define-method fbml-set-ref-handle [handle fbml])
(define-method fql-multiquery [queries])
(define-method fql-query [query])
(define-method friends-are-friends [uids1 uids2])
(define-method friends-get [uid])
(define-method friends-get-app-users [])
(define-method friends-get-lists [])
(define-method friends-get-mutual-friends [])
(define-method groups-get [])
(define-method groups-get-members [])
(define-method intl-get-translations [])
(define-method intl-upload-native-strings [])
(define-method links-get [])
(define-method links-get-stats [urls])
(define-method links-post [url comment uid image])
(define-method links-preview [url])
(define-method livemessage-send [])
(define-method message-get-threads-in-folder [])
(define-method notes-create [uid title content])
(define-method notes-delete [note_id uid])
(define-method notes-edit [note_id title content])
(define-method notes-get [])
(define-method notifications-get [])
(define-method notifications-get-list [])
(define-method notifications-mark-read [])
(define-method pages-get-info [fields page_ids uid])
(define-method pages-is-admin [page_id uid])
(define-method pages-is-app-added [page_id])
(define-method pages-is-fan [page_id uid])
(define-method photos-add-tag [pid tag_uid tag_text x y])
(define-method photos-create-album [name location description visible uid privacy])
(define-method photos-get [subj_id aid pids])
(define-method photos-get-albums [uid aids])
(define-method photos-get-tags [pids])
(define-method photos-upload [uid aid caption])
(define-method sms-can-send [uid])
(define-method sms-send [uid message session_id req_session])
(define-method status-get [uid limit])
(define-method status-set [status uid])
(define-method stream-add-comment [uid post_id comment])
(define-method stream-add-like [uid post_id])
(define-method stream-get [viewer_id source_ids start_time end_time limit filter_key metadata])
(define-method stream-get-comments [post_id])
(define-method stream-get-filters [uid])
(define-method stream-publish [message attachment action_links target_id uid privacy])
(define-method stream-remove [post_id uid])
(define-method stream-remove-comment [comment_id uid])
(define-method stream-remove-like [uid post_id])
(define-method users-get-info [uids fields])
(define-method users-get-logged-in-user [])
(define-method users-get-standard-info [uids fields])
(define-method users-is-app-user [uid])
(define-method users-is-verified [])
(define-method users-set-status [status clear status_includes_verb uid])
(define-method video-get-upload-limits [])
(define-method video-upload [])

(def/defnk init-facebook! [api-key secret :secure true :session-key nil]
  (let [protoc (if (= true secure) protoc-secure protoc-default)]
    (def *fb-conn* {:endpoint (str protoc rest-endpoint)
                    :api_key api-key
                    :secret secret
                    :session_key session-key})))