(ns frontend.routes.home
  (:require [compojure.core :refer :all]
            [frontend.views.layout :as layout]
            [hiccup.form :refer :all]))

(def snippet "Here'll be short snippets for RSS data. Here'll be short snippets for RSS data. Here'll be short snippets for RSS data. Here'll be short snippets for RSS data. Here'll be short snippets for RSS data.Here'll be short snippets for RSS data.Here'll be short snippets for RSS data.Here'll be short snippets for RSS data.")

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
    [:ol
     (for [result results]
       [:li 
        [:a {:href (:url result)} (:title result)]
        [:p (:snippet result)]])]))

(defn- search [query]
  [{:url "http://github.com" :title "Github" :snippet snippet} {:url "http://twitter.com" :title "Twitter" :snippet snippet}])

(defroutes home-routes
           (GET "/" [] (home))
           (POST "/" [query] (results-view query (search query))))