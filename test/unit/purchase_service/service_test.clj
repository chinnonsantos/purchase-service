(ns purchase-service.service-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>
                                 against-background]]
            [purchase-service.db.saving-purchase :as db]
            [purchase-service.components.transactions :as trans]
            [ring.mock.request :as mock]
            [purchase-service.service :refer [app]]
            [cheshire.core :as json]))

(facts "Hitting main route, checking microservice health" :unit ;; filter label

       (against-background (json/generate-string {:message "Alive!"})
                           => "{\"message\":\"Alive!\"}") ;; mock Cheshire

       (let [response (app (mock/request :get "/"))] ;; mock Ring

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 200"
               (:status response) => 200)

         (fact "body response is a JSON, message should be 'Alive!'"
               (:body response) => "{\"message\":\"Alive!\"}")))

(facts "Hitting balance route, by account id, checking response" :unit

       (against-background [(json/generate-string {:balance 0})
                            => "{\"balance\":0}"
                            (db/balance!) => 0])

       (let [response (app (mock/request :get "/balance/:account-id/"))]

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 200"
               (:status response) => 200)

         (fact "body response is a JSON, initial value of balance should be 0"
               (:body response) => "{\"balance\":0}")))

(facts "Hitting purchases list route, by account id, checking response" :unit

       (against-background (json/generate-string '()) => "[]")

       (let [response (app (mock/request :get "/purchase/from-account/:account-id/"))]

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 200"
               (:status response) => 200)

         (fact "body response is a JSON, initial value should be empty list"
               (:body response) => "[]")))

(facts "Hitting purchase info route, checking response" :unit

       (against-background (json/generate-string {}) => "{}")

       (let [response (app (mock/request :get "/purchase/:purchase-id/"))]

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 200"
               (:status response) => 200)

         (fact "body response is a JSON, initial value should be {}"
               (:body response) => "{}")))

(facts "Hitting purchase register route, checking response" :unit

       (against-background [(trans/valid? {:value 100}) => true ;; Mock of `trans/valid?`
                            (db/register! {:value 100}) => {:value 100} ;; Mock of `db/register!`
                            ])

       (let [response (app (-> (mock/request :post "/purchase/") ;; Mock of `/purchase/` route
                               (mock/json-body {:value 100}) ;; Creating JSON for body POST
                               ))]

         (fact "the header content-type is 'application/json'"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")

         (fact "status response is 201"
               (:status response) => 201)

         (fact "body response is a JSON, with the same content that was submitted"
               (:body response) => "{\"value\":100}")))

(facts "Hitting invalid route, checking routes not found" :unit

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
