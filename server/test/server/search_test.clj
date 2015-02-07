(ns server.search-test
  (:require [clojure.test :refer :all]
            [cheshire.core :refer :all]
            [ring.mock.request :as mock]
            [server.handler :refer :all]
            [server.indexer :as indexer]))

(defn- get-json-body [response]
  (decode (:body response) true))

(deftest test-app
  (indexer/insert {:title "The first document" :url "http://github.com"})
  (indexer/insert {:title "The second document" :url "http://twitter.com"})
  
  (testing "search by phrase"
    (let [response (application (mock/request :post "/search" (encode {:keywords "first"})))]
      (is (= (:status response) 201))
      (is (> (count (get-json-body response)) 0))))

  (testing "empty result"
    (let [response (application (mock/request :post "/search" (encode {:keywords "unknown"})))]
      (is (= (:status response) 201))
      (is (= (count (get-json-body response)) 0))))
  )
