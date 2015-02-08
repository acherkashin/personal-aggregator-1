(ns server.search-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as clj-time]
            [cheshire.core :refer :all]
            [ring.mock.request :as mock]
            [server.handler :refer :all]
            [server.indexer :as indexer]))

(defn- get-json-body [response]
  (decode (:body response) true))

(def doc1 {:title "The first document"
           :url "http://github.com"
           :snippet "This is a site for programmers."
           :time (str (clj-time/now))})
(def doc2 {:title "The second document"
           :url "http://twitter.com"
           :snippet "It is used for access to a lot of actual information.\n Try this site."
           :time (str (clj-time/now))})

(deftest test-app
  (indexer/delete-all)
  (indexer/insert doc1)
  (indexer/insert doc2)

  (testing "search by phrase"
    (let [response (application (mock/request :get "/search" {:keywords "first"}))]
      (is (= (:status response) 200))
      (let [results (get-json-body response)]
        (is (= (count results) 1))
        (is (= (first results) doc1)))))

  (testing "empty result"
    (let [response (application (mock/request :get "/search" {:keywords "unknown"}))]
      (is (= (:status response) 200))
      (is (= (count (get-json-body response)) 0))))
  )
