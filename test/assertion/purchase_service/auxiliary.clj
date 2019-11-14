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

(def account-id (UUID/randomUUID))

(def purchase-id-st (UUID/randomUUID)) ; first id

(def purchase-id-nd (UUID/randomUUID)) ; second id

(def income-st
  {:account-id account-id ; required
   :type "income" ; required ("income" or "expense")
   :value 520.50 ; required (posive number)
   :date "2019-11-11T23:15:22Z" ; optional
   :origin {:code 0
            :name "bill payment"} ; required
   :tag ["bill"
         "prepayment"]} ; optional
  )

(def income-st-w-id
  (merge income-st {:purchase-id purchase-id-st}))

(def income-st-json
  (json/generate-string income-st
                        {:escape-non-ascii true}))

(def income-st-json-w-id
  (json/generate-string income-st-w-id
                        {:escape-non-ascii true}))

(def expense-st
  {:account-id account-id
   :type "expense"
   :value 124.90
   :date "2019-11-03T21:36:27Z"
   :origin {:code 2
            :name "shopping online"}
   :tag ["footwear"]})

(def expense-st-w-id
  (merge expense-st {:purchase-id purchase-id-st}))

(def expense-st-json
  (json/generate-string expense-st
                        {:escape-non-ascii true}))

(def expense-st-json-w-id
  (json/generate-string expense-st-w-id
                        {:escape-non-ascii true}))

(def expense-nd
  {:account-id account-id
   :type "expense"
   :value 459.99
   :date "2019-11-05T14:45:01Z"
   :origin {:code 1
            :name "shopping"}
   :tag ["furniture"
         "kitchen"]})

(defn rm-purchase-id-from-json
  "Remove the purchase-id key from JSON string"
  [json]
  (-> (json/parse-string json true)
      (last)
      (dissoc :purchase-id)
      (json/generate-string {:escape-non-ascii true})))