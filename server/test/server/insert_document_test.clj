(ns server.insert-document-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as clj-time]
            [cheshire.core :refer :all]
            [ring.mock.request :as mock]
            [server.handler :refer :all]))

(defn- get-json-body [response]
  (decode (:body response) true))

(def doc {:title "Test"
          :url "http://github.com"
          :snippet "Test snippet"
          :time (str (clj-time/now))})

(deftest test-app
  (testing "insert new document"
    (let [response (application (mock/request :post "/insert-document" (encode doc)))]
      (is (= (:status response) 201)))))
