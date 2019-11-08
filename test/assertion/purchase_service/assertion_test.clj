(ns purchase-service.assertion-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>
                                 against-background
                                 before
                                 after]]
            [purchase-service.auxiliary :refer [start-server!
                                                stop-server!
                                                response]]))

(facts "Starting server, hitting some endpoints,
checking responses and stopping server" :assertion ;; filter label

       (against-background [(before :facts (start-server!)) ;; `setup`
                            (after :facts (stop-server!))] ;; `teardown`

                           (fact "Initial balance is 0"
                                 (response "/balance") => "0")))