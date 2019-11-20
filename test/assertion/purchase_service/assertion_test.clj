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
                                                purchase-id
                                                income-st
                                                income-st-json
                                                expense-st
                                                expense-st-json
                                                expense-nd
                                                content-like-json
                                                rm-id-date
                                                rm-id-date-from-json]]
            [cheshire.core :as json]
            [clj-http.client :as http])
  (:import [java.util UUID]))

(facts "Starting server, hitting some endpoints, checking responses and stopping server" :assertion ;; filter label

       (against-background
        [(before :facts [(reset-records!) (start-server!)]) ;; `setup`
         (after :facts (stop-server!))] ;; `teardown`

        (fact "initial balance is 0"
              (-> (str "/balance/" account-id "/")
                  (response)
                  (json/parse-string true)) => {:balance 0})

        (fact "initial purchases list by account ID is an empty list"
              (-> (str "/purchase/from-account/" account-id "/")
                  (response)
                  (json/parse-string true)) => '())

        (fact "initial purchase info is an empty object"
              (-> (str "/purchase/" purchase-id "/")
                  (response)
                  (json/parse-string  true)) => {})

        (fact "check response body when get transaction by ID (transaction info)"
              (let [response-post (http/post (endpoint "/purchase/")
                                             (content-like-json income-st))
                    income-registed (json/parse-string (:body response-post) true)
                    purchase-id-registed (:purchase-id income-registed)]

                (-> (str "/purchase/" purchase-id-registed "/")
                    (response)
                    (json/parse-string true)) => income-registed))

        (fact "check response body after register an income transaction"

              (let [response (http/post (endpoint "/purchase/")
                                        (content-like-json income-st))]

                (-> (:body response)
                    (rm-id-date-from-json)) => income-st-json))

        (fact "check response body after register an expense transaction"

              (let [response (http/post (endpoint "/purchase/")
                                        (content-like-json expense-st))]

                (-> (:body response)
                    (rm-id-date-from-json)) => expense-st-json))

        (fact "balance is 520.5 when there is an only income transaction with value of 520.5"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (-> (str "/balance/" account-id "/")
                  (response)
                  (json/parse-string true)) => {:balance 520.5})

        (fact "balance is -124.9 when there is an only expense transaction with value of 124.9"

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-st))

              (-> (str "/balance/" account-id "/")
                  (response)
                  (json/parse-string true)) => {:balance -124.9})

        (fact "balance is -64.39 when we creating an income transaction with value of 520.5 and an expense transaction with value of 124.9 and other expense transaction with value of 459.99"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-nd))

              (-> (str "/balance/" account-id "/")
                  (response)
                  (json/parse-string true)) => {:balance -64.39})

        (fact "purchase list count check when registering only one purchase, should list only one"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (let [purchase-list (-> (str "/purchase/from-account/" account-id "/")
                                      (response)
                                      (json/parse-string true))]

                (map rm-id-date purchase-list) => (list income-st)))

        (fact "purchase list count check when registering two purchase, should list only two"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-st))

              (let [purchase-list (-> (str "/purchase/from-account/" account-id "/")
                                      (response)
                                      (json/parse-string true))]

                (map rm-id-date purchase-list) => (list income-st
                                                        expense-st)))

        (fact "purchase list count check when registering three purchase, should list only three"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-nd))

              (let [purchase-list (-> (str "/purchase/from-account/" account-id "/")
                                      (response)
                                      (json/parse-string true))]

                (map rm-id-date purchase-list) => (list income-st
                                                        expense-st
                                                        expense-nd)))

        (fact "purchase list count check when registering two purchase from the same account ID and one from the different account ID, should list only two"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json (merge expense-nd {:account-id (UUID/randomUUID)})))

              (let [purchase-list (-> (str "/purchase/from-account/" account-id "/")
                                      (response)
                                      (json/parse-string true))]

                (map rm-id-date purchase-list) => (list income-st
                                                        expense-st)))

        (fact "purchase list count check when registering only one purchase with a account ID and list purchases from other account ID, should list none"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (let [purchase-list (-> (str "/purchase/from-account/" (UUID/randomUUID) "/")
                                      (response)
                                      (json/parse-string true))]

                (map rm-id-date purchase-list) => '()))))

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
