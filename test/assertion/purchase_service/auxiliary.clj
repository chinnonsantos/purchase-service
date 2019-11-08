(ns purchase-service.auxiliary
  (:require [purchase-service.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clj-http.client :as http]))

(def port 9003)

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