(defproject purchase-service "1.0.0" ;; Semantic Versioning 2.0.0
  :description "Purchase microservices demo"
  :url "https://github.com/chinnonsantos/purchase-service"
  :min-lein-version "2.9.1"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"] ;; Project template
                 [ring/ring-defaults "0.3.2"] ;; HTTP server
                 [clj-http "3.10.0"] ;; HTTP client
                 [cheshire "5.9.0"] ;; JSON encoding
                 ]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler purchase-service.service/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]
                        [midje "1.9.9"] ;; TDD
                        [ring/ring-core "1.7.1"]
                        [ring/ring-jetty-adapter "1.7.1"] ;; Ring server abstraction
                        ]
         :plugins [[lein-midje "3.2.1"]
                   [lein-cloverage "1.1.2"] ;; Test coverage
                   ]}}
  :test-paths ["test/unit"
               "test/assertion"])
