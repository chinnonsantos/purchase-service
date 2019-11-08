(ns purchase-service.handler-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>]]
            [ring.mock.request :as mock]
            [purchase-service.handler :refer [app]]))

(facts "Hitting main route, check microservice health" :unit ;; filter label

       (fact "status response is 200"
             (let [response (app (mock/request :get "/"))]
               (:status response) => 200))

       (fact "body response is 'Alive!'"
             (let [response (app (mock/request :get "/"))]
               (:body response) => "Alive!")))

(facts "Hitting balance route, check value" :unit ;; filter label

       (fact "status response is 200"
             (let [response (app (mock/request :get "/balance"))]
               (:status response) => 200))

       (fact "body response is 0"
             (let [response (app (mock/request :get "/balance"))]
               (:body response) => "0")))

(facts "Hitting invalid route, check routes not found" :unit ;; filter label

       (fact "status response is 404"
             (let [response (app (mock/request :get "/invalid"))]
               (:status response) => 404))

       (fact "body response is 'Not Found'"
             (let [response (app (mock/request :get "/invalid"))]
               (:body response) => "Not Found")))
