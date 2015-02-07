(ns server.search-test
  (:require [clojure.test :refer :all]
            [cheshire.core :refer :all]
            [ring.mock.request :as mock]
            [server.handler :refer :all]
            [server.indexer :as indexer]))

(defn- get-json-body [response]
  (decode (:body response) true))

(def doc1 {:title "The first document" :url "http://github.com"})
(def doc2 {:title "The second document" :url "http://twitter.com"})

(deftest test-app
  (indexer/delete-all)
  (indexer/insert doc1)
  (indexer/insert doc2)

  (testing "search by phrase"
    (let [response (application (mock/request :post "/search" (encode {:keywords "first"})))]
      (is (= (:status response) 201))
      (let [results (get-json-body response)]
        (is (= (count results) 1))
        (is (= (first results) doc1)))))

  (testing "empty result"
    (let [response (application (mock/request :post "/search" (encode {:keywords "unknown"})))]
      (is (= (:status response) 201))
      (is (= (count (get-json-body response)) 0))))
  )
