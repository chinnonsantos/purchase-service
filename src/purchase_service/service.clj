(ns purchase-service.service
  (:require [compojure.core :refer [defroutes
                                    GET]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults
                                              site-defaults]]
            [cheshire.core :as json]))

(defn header-json [data-map]
  {:headers {"Content-Type"
             "application/json; charset=utf-8"}
   :body (json/generate-string data-map)})

(defroutes app-routes
  (GET "/" []
    (header-json {:message "Alive!"}))
  (GET "/balance/:account-id/" []
    (header-json {:balance 0}))
  (GET "/purchase/from-account/:account-id/" []
    (header-json {:list []}))
  (GET "/purchase/:purchase-id/" []
    (header-json {:purchase []}))
  (route/not-found (header-json {:message "Not Found"})))

(def app
  (wrap-defaults app-routes site-defaults))
