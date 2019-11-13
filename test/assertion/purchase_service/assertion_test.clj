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
                                                account-id-st
                                                purchase-id-st
                                                income-st
                                                expense-st
                                                expense-nd
                                                content-like-json]]
            [cheshire.core :as json]
            [clj-http.client :as http]))

(facts "Starting server, hitting some endpoints,
checking responses and stopping server" :assertion ;; filter label

       (against-background
        [(before :facts [(reset-records!) (start-server!)]) ;; `setup`
         (after :facts (stop-server!))] ;; `teardown`

        (fact "initial balance is 0"
              (json/parse-string (response (str "/balance/" account-id-st "/")) true)
              => {:balance 0})

        (fact "initial purchases list is []"
              (json/parse-string (response (str "/purchase/from-account/" account-id-st "/")) true)
              => {:list []})

        (fact "initial purchase info is {}"
              (json/parse-string (response (str "/purchase/" purchase-id-st "/")) true)
              => {:purchase {}})

        (fact "balance is 520.50 when there is a only income transaction, in the value of 520.50"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (json/parse-string (response (str "/balance/" account-id-st "/")) true)
              => {:balance 520.50})

        (fact "balance is -64.39 when we creating an income transaction with value of 520.50 and
an expense transaction with value of 124.90 and other expense transaction with value of 459.99"

              (http/post (endpoint "/purchase/")
                         (content-like-json income-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-st))

              (http/post (endpoint "/purchase/")
                         (content-like-json expense-nd))

              (json/parse-string (response (str "/balance/" account-id-st "/")) true)
              => {:balance -64.39})))