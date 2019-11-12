(ns purchase-service.assertion-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>
                                 against-background
                                 before
                                 after]]
            [purchase-service.auxiliary :refer [start-server!
                                                stop-server!
                                                response
                                                endpoint
                                                test-account-id
                                                test-purchase-id
                                                test-income-st]]
            [cheshire.core :as json]
            [clj-http.client :as http]))

(facts "Starting server, hitting some endpoints,
checking responses and stopping server" :assertion ;; filter label

       (against-background
        [(before :facts (start-server!)) ;; `setup`
         (after :facts (stop-server!))] ;; `teardown`

        (fact "Initial balance is 0"
              (json/parse-string (response (str "/balance/" test-account-id "/")) true)
              => {:balance 0})

        (fact "Initial purchases list is []"
              (json/parse-string (response (str "/purchase/from-account/" test-account-id "/")) true)
              => {:list []})

        (fact "Initial purchase info is {}"
              (json/parse-string (response (str "/purchase/" test-purchase-id "/")) true)
              => {:purchase {}})

        (fact "Balance is 520.50 when a only purchase is income type with the same value"

              (http/post (endpoint "/purchase/")
                         {:content-type :json
                          :body (json/generate-string test-income-st)})

              (json/parse-string (response (str "/balance/" test-account-id "/")) true)
              => {:balance 520.50})))