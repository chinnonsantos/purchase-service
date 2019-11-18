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

(def other-account-id (UUID/randomUUID))

(def purchase-id (UUID/randomUUID))

(def income-st
  {:account-id (str account-id) ; required
   :type "income" ; required ("income" or "expense")
   :value 520.5 ; required (posive number)
   :origin {:code 0
            :name "bill payment"} ; required
   :tag ["bill"
         "prepayment"]} ; optional
  )

(def income-st-json
  (json/generate-string income-st
                        {:escape-non-ascii true}))

(def expense-st
  {:account-id (str account-id)
   :type "expense"
   :value 124.9
   :origin {:code 2
            :name "shopping online"}
   :tag ["footwear"]})

(def expense-st-json
  (json/generate-string expense-st
                        {:escape-non-ascii true}))

(def expense-nd
  {:account-id (str account-id)
   :type "expense"
   :value 459.99
   :origin {:code 1
            :name "shopping"}
   :tag ["furniture"
         "kitchen"]})

(def purchase-list
  [income-st expense-st expense-nd])

(defn rm-id-date
  "Remove the purchase-id and date key from set"
  [set]
  (dissoc set :purchase-id :date))

(defn rm-id-date-from-json
  "Remove the purchase-id and date key from JSON string"
  [json]
  (-> (json/parse-string json true)
      (rm-id-date)
      (json/generate-string {:escape-non-ascii true})))
