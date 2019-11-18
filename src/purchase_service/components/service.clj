(ns purchase-service.components.service
  (:require [cheshire.core :as json]
            [purchase-service.db.saving-purchase :as db]
            [purchase-service.components.transactions :refer [valid?]]))

(defn header-json
  "Create a JSON response header"
  [data-map & [status]]
  {:status (or status 200)
   :headers {"Content-Type"
             "application/json; charset=utf-8"}
   :body (json/generate-string data-map)})

(defn get-account-id [request]
  (:account-id (:route-params request)))

(defn get-purchase-id [request]
  (:purchase-id (:route-params request)))

(defn get-filters [request]
  (dissoc (:params request) :account-id))

(defn home-page
  "Main route"
  []
  (header-json {:message "Alive!"}))

(defn account->balance
  "Account balance route"
  [request]
  (-> {:balance (db/balance! (get-account-id request))}
      (header-json)))

(defn get-purchase
  "Purchase info route (purchase details)"
  [request]
  (-> (get-purchase-id request)
      (db/transaction-by-id!)
      (header-json)))

(defn account->purchases
  "Account purchases list route"
  [request]
  (-> (get-account-id request)
      (db/transactions-from-account! (get-filters request))
      (header-json)))

(defn create-purchase
  "Purchase transaction route, create a new purchase"
  [request]
  (let [body (:body request)]
    (if (valid? body)
      (-> (db/register! body)
          (header-json 201))
      (header-json {:mensagem "Unprocessable Entity"} 422))))

(defn not-found
  "Response for any route that does not exist"
  []
  (header-json {:message "Not Found"}))
