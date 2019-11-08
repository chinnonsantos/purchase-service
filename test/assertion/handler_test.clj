(ns purchase-service.handler-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>]]
            [aux.init :refer [start-server!
                              stop-server!]]
          ; [purchase-service.handler :refer [app]]
          ; [ring.adapter.jetty :refer [run-jetty]]
            [clj-http.client :as http]))

(facts "Starting server, hitting some endpoints, checking responses and stopping server"

       (fact "Initial balance is 0"
             (start-server! 9000)

             (:body (http/get "http://localhost:9000/balance")) => "0"
             (stop-server!)))