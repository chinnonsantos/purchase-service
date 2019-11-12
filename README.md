# PURCHASE MICROSERVICES DEMO

## Build status

[![Build Status](https://travis-ci.org/chinnonsantos/purchase-service.svg?branch=master)](https://travis-ci.org/chinnonsantos/purchase-service)

This project deals with a purchase microservice created from the leiningen '[compojure][]' template.

## Prerequisites

You will need [Leiningen][] 2.9.1 or above installed.

**Libraries:**

- [midje][] 1.9.9 (:dev)
- [ring/ring-core][] 1.7.1(:dev)
- [ring/ring-jetty-adapter][] 1.7.1 (:dev)
- [clj-http][] 3.10.0
- [cheshire][] 5.9.0
- [ring-json][] 0.5.0

**Plugins:**

- [lein-midje][] 3.2.1 (:dev)
- [lein-cloverage][] 1.1.2 (:dev)

[compojure]: https://github.com/weavejester/compojure
[leiningen]: https://github.com/technomancy/leiningen
[midje]: https://clojars.org/midje
[ring/ring-core]: https://clojars.org/ring/ring-core
[ring/ring-jetty-adapter]: https://clojars.org/ring/ring-jetty-adapter
[clj-http]: https://clojars.org/clj-http
[cheshire]: https://clojars.org/cheshire
[ring-json]: https://clojars.org/ring/ring-json
[lein-midje]: https://clojars.org/lein-midje
[lein-cloverage]: https://clojars.org/lein-cloverage

## Running server

To start a web server for the application, run:

    lein ring server-headless 9002

## Running tests (TDD)

To test the project, run:

    lein midje

To test the project in development stage, run:

    lein midje :autotest

> Automatically reloading the test with each change

To test only **Unit tests** of the project, run:

    lein midje :filter unit

To test only **Assertion tests** of the project, run:

    lein midje :filter assertion

To check test coverage, run:

    lein cloverage --runner :midje

## License

Copyright © 2019 | Chinnon Santos | Apache License 2.0
