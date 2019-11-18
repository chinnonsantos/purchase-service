(ns purchase-service.service
  (:require [compojure.core :refer [defroutes
                                    GET
                                    POST]]
            [compojure.route :as route]
            [purchase-service.components.service :refer [home-page
                                                         account->balance
                                                         get-purchase
                                                         account->purchases
                                                         create-purchase
                                                         not-found]]
            [ring.middleware.defaults :refer [wrap-defaults
                                              api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]))

(defroutes app-routes
  (GET "/" [] (home-page))
  (GET "/balance/:account-id/" request (account->balance request))
  (GET "/purchase/:purchase-id/" request (get-purchase request))
  (GET "/purchase/from-account/:account-id/" request (account->purchases request))
  (POST "/purchase/" request (create-purchase request))
  (route/not-found (not-found)))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})))
