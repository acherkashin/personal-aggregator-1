(ns server.search-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as clj-time]
            [clj-time.format :as format]
            [cheshire.core :refer :all]
            [ring.mock.request :as mock]
            [server.handler :refer :all]
            [server.indexer :as indexer]))

(defn- get-json-body [response]
  (decode (:body response) true))

(def doc1 {:title "The first document"
           :url "http://github.com/1"
           :snippet "This is a site for programmers."
           :time (clj-time/now)})
(def doc2 {:title "The second document"
           :url "http://twitter.com"
           :snippet "It is used for access to a lot of actual information.\n Try this site."
           :time (clj-time/now)})
(def doc3 {:title "Третий документ"
           :url "http://concert.ru"
           :snippet "Все интересные события"
           :time (clj-time/now)})

(defn correct-result [doc]
  (let [doc (dissoc doc :id) time (:time doc)]
    (assoc doc :time (format/parse time))))

(deftest test-app
  (indexer/delete-all)
  (indexer/insert doc1)
  (indexer/insert doc2)
  (indexer/insert doc3)

  (testing "exists"
    (is (indexer/exists (:url doc1)))
    (is (not (indexer/exists "http://unknown.url"))))

   (testing "search by English phrase"
    (let [response (application (mock/request :get "/search" {:keywords "first"}))]
      (is (= (:status response) 200))
      (let [results (get-json-body response)]
        (is (= (count results) 1))
        (is (= (correct-result (first results)) doc1)))))

  (testing "search by Russian phrase"
    (let [response (application (mock/request :get "/search" {:keywords "Третий"}))]
      (is (= (:status response) 200))
      (let [results (get-json-body response)]
        (is (= (count results) 1))
        (is (= (correct-result (first results)) doc3)))))

  (testing "empty result"
    (let [response (application (mock/request :get "/search" {:keywords "unknown"}))]
      (is (= (:status response) 200))
      (is (= (count (get-json-body response)) 0))))
  )
