(ns purchase-service.service
  (:require [compojure.core :refer [defroutes
                                    GET
                                    POST]]
            [compojure.route :as route]
            [purchase-service.db.saving-purchase :as db]
            [purchase-service.components.transactions :as trans]
            [ring.middleware.defaults :refer [wrap-defaults
                                              api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [cheshire.core :as json]))

(defn header-json [data-map & [status]]
  {:status (or status 200)
   :headers {"Content-Type"
             "application/json; charset=utf-8"}
   :body (json/generate-string data-map)})

(defroutes app-routes
  (GET "/" []
    (header-json {:message "Alive!"}))
  (GET "/balance/:account-id/" []
    (header-json {:balance (db/balance!)}))
  (GET "/purchase/from-account/:account-id/" []
    (header-json {:list []}))
  (GET "/purchase/:purchase-id/" []
    (header-json {:purchase {}}))
  (POST "/purchase/" request
    (if (trans/valid? (:body request))
      (-> (db/register! (:body request))
          (header-json 201))
      (header-json {:mensagem "Unprocessable Entity"} 422)))
  (route/not-found (header-json {:message "Not Found"})))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})))
