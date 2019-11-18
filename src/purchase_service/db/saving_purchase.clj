(ns purchase-service.db.saving-purchase)

(def records
  (atom []))

(defn transactions!
  "List all transactions"
  []
  @records)

(defn reset-records!
  "Remove all transactions"
  []
  (reset! records []))

(defn register!
  "Save the transaction to an Atom and return the last record.
   Optionally an ID and Date can be entered to override the
   Runtime generated ID and Date (useful for automated testing)."
  [transaction & [uuid-default date-default]]
  (let [purchase-id (or uuid-default (java.util.UUID/randomUUID))
        date (or date-default (java.util.Date.))]
    (->> (merge transaction {:purchase-id purchase-id
                             :date date})
         (swap! records conj)
         (last))))

(defn transactions-from-account! [account-id & [filters]]
  (filter #(= account-id (:account-id %)) (transactions!)))

(defn- expense? [transaction]
  (= (:type transaction) "expense"))

(defn- calculate! [balance transaction]
  (let [value (:value transaction)]
    (if (expense? transaction)
      (- balance value)
      (+ balance value))))

(defn balance! [account-id]
  (let [transactions (transactions-from-account! account-id)]
    (reduce calculate! 0 transactions)))
