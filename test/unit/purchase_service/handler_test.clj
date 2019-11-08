(ns purchase-service.handler-test
  (:require [midje.sweet :refer [facts
                                 fact
                                 =>]]
            ; [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [customer-service.handler :refer [app]]))

; (deftest test-app
;   (testing "main route"
;     (let [response (app (mock/request :get "/"))]
;       (is (= (:status response) 200))
;       (is (= (:body response) "Alive!"))))

;   (testing "not-found route"
;     (let [response (app (mock/request :get "/invalid"))]
;       (is (= (:status response) 404))
;       (is (= (:body response) "Not Found")))))

(facts "Hitting main route, check microservice health"

       (fact "status response is 200"
             (let [response (app (mock/request :get "/"))]
               (:status response) => 200))

       (fact "body response is 'Alive!'"
             (let [response (app (mock/request :get "/"))]
               (:body response) => "Alive!")))

(facts "Hitting balance route, check value"

       (fact "status response is 200"
             (let [response (app (mock/request :get "/balance"))]
               (:status response) => 200))

       (fact "body response is 0"
             (let [response (app (mock/request :get "/balance"))]
               (:body response) => "0")))

(facts "Hitting invalid route, check routes not found"

       (fact "status response is 404"
             (let [response (app (mock/request :get "/invalid"))]
               (:status response) => 404))

       (fact "body response is 'Not Found'"
             (let [response (app (mock/request :get "/invalid"))]
               (:body response) => "Not Found")))
