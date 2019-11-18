(ns purchase-service.components.transactions)

; TODO: implement the checking of 'account-id' value, if value is UUID?
; TODO: implement checking for the 'origin' structure if the map has 'code' and 'name'?
(defn valid? [transaction]
  (and (contains? transaction :account-id)
       (contains? transaction :value)
       (number? (:value transaction))
       (pos? (:value transaction))
       (contains? transaction :type)
       (or (= "expense" (:type transaction))
           (= "income" (:type transaction)))
       (contains? transaction :origin)))
