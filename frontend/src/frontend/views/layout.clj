(ns frontend.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.bootstrap.page :refer :all]))

(def title "Personal information aggregator")

(defn common [& body]
  (html5
    [:head
     [:title title]
     (include-bootstrap)
     (include-css "/css/screen.css")]
    [:body body]))