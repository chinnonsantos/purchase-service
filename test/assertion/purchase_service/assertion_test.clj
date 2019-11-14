(ns purchase-service.assertion-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>
                                 against-background
                                 before
                                 after]]
            [purchase-service.db.saving-purchase :refer [reset-records!]]
            [purchase-service.auxiliary :refer [start-server!
                                                stop-server!
                                                response
                                                endpoint
                                                account-id
                                                purchase-id-st
                                                income-st
                                                income-st-json
                                                expense-st
                                                expense-st-json
                                                expense-nd
                                                content-like-json
                                                rm-purchase-id-from-json]]
            [cheshire.core :as json]
            [clj-http.client :as http]))

(facts "Starting server, hitting some endpoints, checking responses and stopping server" :assertion ;; filter label

       (against-background
        [(before :facts [(reset-records!) (start-server!)]) ;; `setup`
         (after :facts (stop-server!))] ;; `teardown`

        (fact "initial balance is 0"
              (json/parse-string (response (str "/balance/" account-id "/")) true)
              => {:balance 0})

        (fact "initial purchases list is []"
              (json/parse-string (response (str "/purchase/from-account/" account-id "/")) true)
              => {:list []})

        (fact "initial purchase info is {}"
              (json/parse-string (response (str "/purchase/" purchase-id-st "/")) true)
              => {:purchase {}})

        (fact "check response body after register a income transaction"

              (let [response (http/post (endpoint "/purchase/")
                                        (content-like-json income-st))]
                (rm-purchase-id-from-json (:body response)) => income-st-json))

        (fact "check response body after register a expense transaction"

              (let [response (http/post (endpoint "/purchase/")
                                        (content-like-json expense-st))]
                (rm-purchase-id-from-json (:body response)) => expense-st-json))

        (fact "balance is 520.50 when there is a only income transaction with value of 520.50"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (json/parse-string (response (str "/balance/" account-id "/")) true)
              => {:balance 520.50})

        (fact "balance is -124.90 when there is a only expense transaction with value of 124.90"

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-st))

              (json/parse-string (response (str "/balance/" account-id "/")) true)
              => {:balance -124.90})

        (fact "balance is -64.39 when we creating an income transaction with value of 520.50 and an expense transaction with value of 124.90 and other expense transaction with value of 459.99"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-nd))

              (json/parse-string (response (str "/balance/" account-id "/")) true)
              => {:balance -64.39})))

(facts "Hitting purchase register route, with invalid income data, checking response status" :assertion

       (against-background
        [(before :facts [(reset-records!) (start-server!)])
         (after :facts (stop-server!))]

        (let [expected-status 422] ;; 422 Unprocessable Entity
          (fact "reject a transaction without type"
                (let [response (http/post (endpoint "/purchase/")
                                          (content-like-json (dissoc income-st
                                                                     :type)))]
                  (:status response) => expected-status))

          (fact "reject a transaction with unknown type"
                (let [response (http/post (endpoint "/purchase/")
                                          (content-like-json (merge income-st
                                                                    {:type "any!"})))]
                  (:status response) => expected-status))

          (fact "reject a transaction without value"
                (let [response (http/post (endpoint "/purchase/")
                                          (content-like-json (dissoc income-st
                                                                     :value)))]
                  (:status response) => expected-status))

          (fact "reject a transaction with negative value"
                (let [response (http/post (endpoint "/purchase/")
                                          (content-like-json (merge income-st
                                                                    {:value -100})))]
                  (:status response) => expected-status))

          (fact "reject a transaction with a non-numeric value"
                (let [response (http/post (endpoint "/purchase/")
                                          (content-like-json (merge income-st
                                                                    {:value "a hundred"})))]
                  (:status response) => expected-status)))))

(facts "Hitting purchase register route, with invalid expense data, checking response status" :assertion

       (against-background
        [(before :facts [(reset-records!) (start-server!)])
         (after :facts (stop-server!))]

        (let [expected-status 422] ;; 422 Unprocessable Entity
          (fact "reject a transaction without type"
                (let [response (http/post (endpoint "/purchase/")
                                          (content-like-json (dissoc expense-st
                                                                     :type)))]
                  (:status response) => expected-status))

          (fact "reject a transaction with unknown type"
                (let [response (http/post (endpoint "/purchase/")
                                          (content-like-json (merge expense-st
                                                                    {:type "any!"})))]
                  (:status response) => expected-status))

          (fact "reject a transaction without value"
                (let [response (http/post (endpoint "/purchase/")
                                          (content-like-json (dissoc expense-st
                                                                     :value)))]
                  (:status response) => expected-status))

          (fact "reject a transaction with negative value"
                (let [response (http/post (endpoint "/purchase/")
                                          (content-like-json (merge expense-st
                                                                    {:value -100})))]
                  (:status response) => expected-status))

          (fact "reject a transaction with a non-numeric value"
                (let [response (http/post (endpoint "/purchase/")
                                          (content-like-json (merge expense-st
                                                                    {:value "a hundred"})))]
                  (:status response) => expected-status)))))