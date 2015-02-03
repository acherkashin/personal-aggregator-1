(ns frontend.routes.home
  (:require [compojure.core :refer :all]
            [frontend.views.layout :as layout]
            [hiccup.form :refer :all]))

(defn home []
  (layout/common
    [:h1 layout/title]
    (hiccup.form/form-to [:post "/"]
                         [:label "What do you want to find?"]
                         (text-field {:placeholder "Enter your query here"} "query" "")
                         (submit-button "Search"))))

(defn results-view [& [query snippets]]
  (layout/common
    [:h1 layout/title]
    [:h3 (str "You've asked for: " query)]
    [:h3 "Search results:"]
    [:ul
     (for [snippet snippets]
       [:li [:a {:href "http://github.com"} snippet]])]))

(defn- search [query]
  ["1" "2" "3" query])

(defroutes home-routes
           (GET "/" [] (home))
           (POST "/" [query] (results-view query (search query))))