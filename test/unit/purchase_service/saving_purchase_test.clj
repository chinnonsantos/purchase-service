(ns purchase-service.saving-purchase-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>
                                 against-background
                                 before]]
            [purchase-service.db.saving-purchase :refer [register!
                                                         transactions!
                                                         transactions-from-account!
                                                         transaction-by-id!
                                                         reset-records!
                                                         balance!]]
            [purchase-service.auxiliary :refer [income-st
                                                expense-st
                                                expense-nd
                                                purchase-id
                                                account-id
                                                other-account-id]]))

(facts "Store a transaction in an atom" :unit

       (against-background
        [(before :facts (reset-records!))]

        (fact "initial count of transactions is 0"
              (count (transactions!)) => 0)

        (fact "this transaction is the first record, and count of transactions is 1"
              (let [date (java.util.Date. 1573139257804)]
                (register! income-st purchase-id date)
                => (merge income-st {:purchase-id purchase-id
                                     :date date}))

              (count (transactions!)) => 1)))

(facts "Calculate balance given a collection of transactions" :unit

       (against-background
        [(before :facts (reset-records!))]

        (fact "balance is 2602.5 as a result of the sum of just incomes from five transactions (520.5) with the same account ID and the same value"
              (register! income-st) ; :value 520.50 x 5 = 2602.50
              (register! income-st)
              (register! income-st)
              (register! income-st)
              (register! income-st)

              (balance! account-id) => 2602.5)

        (fact "balance is -624.5 as a result of the sum of just expenses from five transactions (-124.9) with the same account ID and the same value"
              (register! expense-st) ; :value -124.90 x 5 = -624.50
              (register! expense-st)
              (register! expense-st)
              (register! expense-st)
              (register! expense-st)

              (balance! account-id) => -624.5)

        (fact "balance is 456.11 as a result of the sum of incomes minus expenses from four transactions (-124.9, 520.5, -459.99 and 520.5) with the same account ID"
              (register! expense-st) ; :value -124.90 = -124.90
              (register! income-st) ; :value 520.50 = 395.6
              (register! expense-nd) ; :value -459.99 = -64.39
              (register! income-st) ; :value 520.50 = 456.11

              (balance! account-id) => 456.11)

        (fact "balance is 395.6 as a result of the sum of incomes minus expenses from two transactions (-124.9 and 520.5) with the same account ID and another transaction (-459.99) with another account ID"
              (register! expense-st) ; :value -124.90 = -124.90
              (register! income-st) ; :value 520.50 = 395.60
              (register! (merge expense-nd {:account-id other-account-id})) ; not calculate, other account ID!

              (balance! account-id) => 395.6)

        (fact "balance is 0 as a result of the sum of incomes minus expenses from three transactions (-124.9, 520.5 and -459.99), but no transactions with the account ID fetched"
              (register! expense-st) ; :value -124.90 = -124.90
              (register! income-st) ; :value 520.50 = 395.6
              (register! expense-nd) ; :value -459.99 = -64.39

              (balance! other-account-id) => 0)))

(facts "Counting transactions with multiple account IDs" :unit

       (against-background
        [(before :facts (reset-records!))]

        (fact "count is 3 when there are 3 transactions with the same fetched account ID"
              (register! income-st)
              (register! expense-st)
              (register! expense-nd)

              (count (transactions-from-account! account-id)) => 3)

        (fact "count is 2 when there are 3 transactions but just 2 with the same fetched account ID"
              (register! income-st)
              (register! expense-st)
              (register! (merge expense-nd {:account-id other-account-id}))

              (count (transactions-from-account! account-id)) => 2)

        (fact "count is 1 when there are 3 transactions but just 1 with the same fetched account ID"
              (register! income-st)
              (register! expense-st)
              (register! (merge expense-nd {:account-id other-account-id}))

              (count (transactions-from-account! other-account-id)) => 1)

        (fact "count is 0 when there are 3 transactions but NONE with the same account ID fetched"
              (register! income-st)
              (register! expense-st)
              (register! expense-nd)

              (count (transactions-from-account! other-account-id)) => 0)))

(facts "Get transaction by ID" :unit

       (against-background
        [(before :facts (reset-records!))]

        (fact "get only the transaction fetched by ID between the three transaction records"
              (let [date (java.util.Date. 1573139257804)]
                (register! income-st purchase-id date)
                (register! expense-st)
                (register! expense-nd)

                (transaction-by-id! purchase-id)
                => (merge income-st {:purchase-id purchase-id
                                     :date date})))

        (fact "get last transaction fetched by ID between both transaction records with the same ID"
              (let [date (java.util.Date. 1573139257804)]
                (register! income-st purchase-id date)
                (register! expense-st purchase-id date)

                (transaction-by-id! purchase-id)
                => (merge expense-st {:purchase-id purchase-id
                                      :date date})))

        (fact "get no transaction fetched by ID between two transaction records with different IDs"
              (register! income-st)
              (register! expense-st)

              (transaction-by-id! purchase-id) => {})))
