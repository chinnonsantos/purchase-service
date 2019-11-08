(ns purchase-service.handler
  (:require [compojure.core :refer [defroutes
                                    GET]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults
                                              site-defaults]]))

(defroutes app-routes
  (GET "/" [] "Alive!")
  ; (GET "/balance" [] "0")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
