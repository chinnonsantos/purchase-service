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
    (->> (merge transaction {:purchase-id (str purchase-id)
                             :date date})
         (swap! records conj)
         (last))))

(defn transactions-from-account!
  "List transactions by account ID"
  [account-id & [filters]]
  (println-str "filters sent: " filters) ;; TODO: Implement listing with filters (eg.: by tag)
  (filter #(= account-id (:account-id %)) (transactions!)))

(defn transaction-by-id!
  "Get a transaction by purchase ID"
  [purchase-id]
  (->> (filter #(= purchase-id (:purchase-id %)) (transactions!))
       (last)
       (conj {})))

(defn- expense?
  "Check if a transaction type is an expense"
  [transaction]
  (= (:type transaction) "expense"))

(defn- calculate!
  "Calculate the sum of transactions between income and expenses"
  [balance transaction]
  (let [value (:value transaction)]
    (if (expense? transaction)
      (- balance value)
      (+ balance value))))

(defn balance!
  "Calculate balance by account ID"
  [account-id]
  (let [transactions (transactions-from-account! account-id)]
    (reduce calculate! 0 transactions)))
