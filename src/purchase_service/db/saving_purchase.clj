(ns purchase-service.db.saving-purchase)

(def records
  (atom []))

(defn transactions! []
  @records)

(defn reset-records! []
  (reset! records []))

(defn register! [transaction & [purchase-uuid]]
  (let [purchase-id (or purchase-uuid (java.util.UUID/randomUUID))]
    (swap! records conj (merge transaction {:purchase-id purchase-id}))))

(defn- expense? [transaction]
  (= (:type transaction) "expense"))

(defn- calculate! [balance transaction]
  (let [value (:value transaction)]
    (if (expense? transaction)
      (- balance value)
      (+ balance value))))

(defn balance! []
  (reduce calculate! 0 @records))