(ns purchase-service.assertion-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>
                                 against-background
                                 before
                                 after]]
            [purchase-service.auxiliary :refer [start-server!
                                                stop-server!
                                                response]]
            [cheshire.core :as json]))

(facts "Starting server, hitting some endpoints,
checking responses and stopping server" :assertion ;; filter label

       (against-background [(before :facts (start-server!)) ;; `setup`
                            (after :facts (stop-server!))] ;; `teardown`

                           (fact "Initial balance is 0"
                                 (json/parse-string (response "/balance/") true)
                                 => {:balance 0})

                           (fact "Initial purchases list is []"
                                 (json/parse-string (response "/purchase/") true)
                                 => {:list []})

                           (fact "Initial purchase info is []"
                                 (json/parse-string (response "/purchase/:purchase-id/") true)
                                 => {:purchase []})))