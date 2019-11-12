(ns purchase-service.auxiliary
  (:require [purchase-service.service :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clj-http.client :as http])
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

(def test-account-id (UUID/randomUUID))

(def test-purchase-id (UUID/randomUUID))

(def test-income-st
  {:purchase-id test-purchase-id
   :account-id test-account-id
   :type "income"
   :value 520.50
   :date "2019-11-11T23:15:22Z"
   :origin {:code 0
            :name "bill payment"}
   :tag ["bill"
         "prepayment"]})

(def test-income-st-json
(str "{\"purchase-id\":\"" test-purchase-id "\",\"account-id\":\""
     test-account-id "\",\"type\":\"income\",\"value\":520.5,"
     "\"date\":\"2019-11-11T23:15:22Z\",\"origin\":{\"code\":0,"
     "\"name\":\"bill payment\"},\"tag\":[\"bill\",\"prepayment\"]}"))

(def test-expense-st
  {:purchase-id (UUID/randomUUID)
   :account-id test-account-id
   :type "expense"
   :value 124.90
   :date "2019-11-03T21:36:27Z"
   :origin {:code 2
            :name "shopping online"}
   :tag ["footwear"]})

(def test-expense-nd
  {:purchase-id (UUID/randomUUID)
   :account-id test-account-id
   :type "expense"
   :value 459.99
   :date "2019-11-05T14:45:01Z"
   :origin {:code 1
            :name "shopping"}
   :tag ["furniture"
         "kitchen"]})