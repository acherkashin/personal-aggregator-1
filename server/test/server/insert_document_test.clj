(ns server.insert-document-test
  (:require [clojure.test :refer :all]
            [cheshire.core :refer :all]
            [ring.mock.request :as mock]
            [server.handler :refer :all]))

(defn- get-json-body [response]
  (decode (:body response) true))

(deftest test-app
  (testing "insert new document"
    (let [response (application (mock/request :post "/insert-document"
                                              (encode {:title "Test" :url "http://github.com"})))]
      (is (= (:status response) 201))))
  )
