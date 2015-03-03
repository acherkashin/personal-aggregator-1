(ns server.parsers-test
  (:require [clojure.test :refer :all]
            [server.parsers :as parsers]))

(deftest remove-html-tags
  (testing "removing html tags"
    (let [html "Some <b>bold and <i>italic</i></b> text"
          text (parsers/remove-html-tags html)]
      (is (= text "Some bold and italic text")))))
