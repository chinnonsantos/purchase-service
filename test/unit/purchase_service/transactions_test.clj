(ns purchase-service.transactions-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>]]
            [purchase-service.components.transactions :refer [valid?]]
            [purchase-service.auxiliary :refer [income-st
                                                expense-st]]))

(facts "Income data integrity check" :unit

       (fact "a transaction without account id is not valid"
             (valid? (dissoc income-st :account-id)) => false)

       (fact "a transaction without type is not valid"
             (valid? (dissoc income-st :type)) => false)

       (fact "a transaction with unknown type is not valid"
             (valid? (merge income-st {:type "any!"})) => false)

       (fact "a transaction without value is not valid"
             (valid? (dissoc income-st :value)) => false)

       (fact "a transaction with negative value is not valid"
             (valid? (merge income-st {:value -100})) => false)

       (fact "a transaction with a non-numeric value is not valid"
             (valid? (merge income-st {:value "a hundred"})) => false)

       (fact "a transaction without origin map is not valid"
             (valid? (dissoc income-st :origin)) => false)

       (fact "a transaction with a account id, a origin map and a positive number and a known type is valid"
             (valid? income-st) => true))

(facts "Expense data integrity check" :unit

       (fact "a transaction without account id is not valid"
             (valid? (dissoc expense-st :account-id)) => false)

       (fact "a transaction without type is not valid"
             (valid? (dissoc expense-st :type)) => false)

       (fact "a transaction with unknown type is not valid"
             (valid? (merge expense-st {:type "any!"})) => false)

       (fact "a transaction without value is not valid"
             (valid? (dissoc expense-st :value)) => false)

       (fact "a transaction with negative value is not valid"
             (valid? (merge expense-st {:value -100})) => false)

       (fact "a transaction with a non-numeric value is not valid"
             (valid? (merge expense-st {:value "a hundred"})) => false)

       (fact "a transaction without origin map is not valid"
             (valid? (dissoc expense-st :origin)) => false)

       (fact "a transaction with a account id, a origin map and a positive number and a known type is valid"
             (valid? expense-st) => true))