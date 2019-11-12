(ns purchase-service.saving-purchase-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>
                                 against-background
                                 before]]
            [purchase-service.db.saving-purchase :refer [register!
                                                         transactions!
                                                         reset-records!
                                                         balance!]]
            [purchase-service.auxiliary :refer [income-st
                                                expense-st
                                                expense-nd
                                                purchase-id-st]]))

(facts "Store a transaction in an atom" :unit

       (against-background
        [(before :facts (reset-records!))]

        (fact "initial count of transactions is 0"
              (count (transactions!)) => 0)

        (fact "this transaction is the first record, and count of transactions is 1"
              (first (register! income-st purchase-id-st))
              => (merge income-st {:purchase-id purchase-id-st})
              (count (transactions!)) => 1)))

(facts "calculate balance given a collection of transactions" :unit

       (against-background
        [(before :facts (reset-records!))]

        (fact "balance is positive when have just incomes"
              (register! income-st) ; :value 520.50 x 5 = 2602.5
              (register! income-st)
              (register! income-st)
              (register! income-st)
              (register! income-st)
              (balance!) => 2602.5)

        (fact "balance is negative when have just expenses"
              (register! expense-st) ; :value -124.90 x 5 = -624.5
              (register! expense-st)
              (register! expense-st)
              (register! expense-st)
              (register! expense-st)
              (balance!) => -624.5)

        (fact "balance is sum of incomes any less expenses"
              (register! expense-st) ; :value -124.90 = -124.90
              (register! income-st) ; :value 520.50 = 395.6
              (register! expense-nd) ; :value 459.99 = −64.39
              (register! income-st) ; :value 520.50 = 456.11
              (balance!) => 456.11)))