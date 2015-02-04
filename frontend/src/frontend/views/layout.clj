(ns frontend.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(def title "Personal information aggregator")

(defn common [& body]
  (html5
    [:head
     [:title title]
     (include-css "./css/bootstrap.css")
     (include-css "./css/screen.css")
     (include-js "./js/bootstrap.js")]
    [:body
     [:div {:class "container"}
      [:h1 title]
      [:br]
      body]]))
