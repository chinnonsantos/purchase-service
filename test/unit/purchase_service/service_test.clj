(ns purchase-service.service-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>
                                 against-background]]
            [purchase-service.db.saving-purchase :as db]
            [purchase-service.auxiliary :refer [test-income-st
                                                test-income-st-json]]
            [ring.mock.request :as mock]
            [purchase-service.service :refer [app]]
            [cheshire.core :as json]))

(facts "Hitting main route, check microservice health" :unit ;; filter label

       (against-background (json/generate-string {:message "Alive!"})
                           => "{\"message\":\"Alive!\"}") ;; mock Cheshire

       (let [response (app (mock/request :get "/"))] ;; mock Ring

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 200"
               (:status response) => 200)

         (fact "body response is a JSON, being key is :message and value is 'Alive!'"
               (:body response) => "{\"message\":\"Alive!\"}")))

(facts "Hitting balance route, by account id, check value" :unit

       (against-background (json/generate-string {:balance 0})
                           => "{\"balance\":0}")

       (let [response (app (mock/request :get "/balance/:account-id/"))]

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 200"
               (:status response) => 200)

         (fact "body response is a JSON, being key is :balance and value is 0"
               (:body response) => "{\"balance\":0}")))

(facts "Hitting purchases list route, by account id, check value" :unit

       (against-background (json/generate-string {:list []})
                           => "{\"list\":[]}")

       (let [response (app (mock/request :get "/purchase/from-account/:account-id/"))]

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 200"
               (:status response) => 200)

         (fact "body response is a JSON, being key is :list and value is []"
               (:body response) => "{\"list\":[]}")))

(facts "Hitting purchase info route, check value" :unit

       (against-background (json/generate-string {:purchase {}})
                           => "{\"purchase\":{}}")

       (let [response (app (mock/request :get "/purchase/:purchase-id/"))]

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 200"
               (:status response) => 200)

         (fact "body response is a JSON, being key is :purchase and value is {}"
               (:body response) => "{\"purchase\":{}}")))

(facts "Hitting purchase record route, with test income data, check value" :unit

       (against-background (db/record test-income-st-json)
                           => test-income-st)

       (let [response (app (-> (mock/request :post "/purchase/")
                          (mock/json-body test-income-st-json)))]

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 201"
               (:status response) => 201)

         (fact "body response is a JSON, with the same content that was submitted")
               (:body response) => test-income-st-json))

(facts "Hitting invalid route, check routes not found" :unit

       (against-background (json/generate-string {:message "Not Found"})
                           => "{\"message\":\"Not Found\"}")

       (let [response (app (mock/request :get "/invalid/"))]

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 404"
               (:status response) => 404)

         (fact "body response is a JSON, being key is :message and value is 'Not Found'"
               (:body response) => "{\"message\":\"Not Found\"}")))
