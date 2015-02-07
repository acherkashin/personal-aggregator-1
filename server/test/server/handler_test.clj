(ns server.handler-test
  (:require [clojure.test :refer :all]
            [cheshire.core :refer :all]
            [ring.mock.request :as mock]
            [server.handler :refer :all]))

(defn- get-json-body [response]
  (decode (:body response) true))

(deftest test-app
  (testing "main route"
    (let [response (application (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (get-json-body response) {:version 0.1}))))
  
  (testing "not-found route"
    (let [response (application (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
