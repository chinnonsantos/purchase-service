(ns purchase-service.auxiliary
  (:require [purchase-service.service :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clj-http.client :as http]
            [cheshire.core :as json])
  (:import [java.util UUID]))

(def port 9002)

(def server (atom nil))

(defn start-server! []
  (swap! server
         (fn [_] (run-jetty app {:port port
                                 :join? false}))))

(defn stop-server! []
  (.stop @server))

(defn endpoint [route]
  (str "http://localhost:"
       port route))

(def request-http
  (comp http/get endpoint))

(defn response [route]
  (:body (request-http route)))

(defn content-like-json [transaction]
  {:content-type :json
   :body (json/generate-string transaction)
   :throw-exceptions false})

(def account-id-st (UUID/randomUUID))

(def purchase-id-st (UUID/randomUUID)) ; first id

(def purchase-id-nd (UUID/randomUUID)) ; second id

(def purchase-id-rd (UUID/randomUUID)) ; third id

(def income-st
  {:account-id account-id-st
   :type "income"
   :value 520.50
   :date "2019-11-11T23:15:22Z"
   :origin {:code 0
            :name "bill payment"}
   :tag ["bill"
         "prepayment"]})

(def income-st-json
  (str "{\"account-id\":\"" account-id-st "\",\"type\":\"income\","
       "\"value\":520.5,\"date\":\"2019-11-11T23:15:22Z\",\"origin\":"
       "{\"code\":0,\"name\":\"bill payment\"},\"tag\":"
       "[\"bill\",\"prepayment\"]}"))

(def expense-st
  {:account-id account-id-st
   :type "expense"
   :value 124.90
   :date "2019-11-03T21:36:27Z"
   :origin {:code 2
            :name "shopping online"}
   :tag ["footwear"]})

(def expense-nd
  {:account-id account-id-st
   :type "expense"
   :value 459.99
   :date "2019-11-05T14:45:01Z"
   :origin {:code 1
            :name "shopping"}
   :tag ["furniture"
         "kitchen"]})