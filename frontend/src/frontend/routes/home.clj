(ns frontend.routes.home
  (:require [compojure.core :refer :all]
            [frontend.views.layout :as layout]
            [cheshire.core :refer :all]
            [clj-http.client :as client]
            [hiccup.form :refer :all]))

(def url "http://localhost:3000/search")

(defn- format-time-string [s]
  s)

(defn home []
  (layout/common
    (hiccup.form/form-to [:post "/"]
      [:div {:style "text-align: center"}
       (text-field {:placeholder "What would you like to find?" :size 100} "query" "")
       [:br]
       [:br]
       (submit-button {:class "btn btn-info"} "Search")])))

(defn results-view [& [query results]]
  (layout/common
    [:h3 (str "You've asked for: " query)]
    [:h3 "Search results:"]
    (if (= (count results) 0)
      [:p "Sorry, we haven't find anything"]
      [:ol
       (for [result results]
         [:li 
          [:a {:href (:url result)} (:title result)]
          [:p (format-time-string (:time result))]
          [:p (:snippet result)]])])))

(defn- search [query]
  (let [obj {:keywords query}]
    (let [results (client/get url {:query-params obj})]
      (decode (:body results) true))))

(defroutes home-routes
           (GET "/" [] (home))
           (POST "/" [query] (results-view query (search query))))
