(ns frontend.core
    (:require [reagent.core :as reagent :refer [atom]]
              [ajax.core :refer [GET POST]]
              [clojure.walk :refer [keywordize-keys]]
              [cljs-time.core :as cljs-time]
              [cljs-time.format :as cljs-format]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [cljsjs.react :as react])
    (:import goog.History))

;; -------------------------
;; Data

(def address "http://188.226.178.169:3000/search")

(def search-results (atom []))

(def text-for-search (atom "доллар"))

(defn search-handler [response]
  (let [results (map #(clojure.walk/keywordize-keys %) response)]
    (reset! search-results results)))

(defn search-error [{:keys [status status-text]}]
  (.log js/console (str "Error occured: " status " " status-text))
  (reset! search-results []))

(defn search []
  (reset! search-results [])
  (GET address {:params {:keywords @text-for-search}
                :format :json
                :handler search-handler
                :error-handler search-error}))

(defn- is-same-date [d1 d2]
  (and (= (cljs-time/year d1) (cljs-time/year d2))
      (= (cljs-time/month d1) (cljs-time/month d2))
       (= (cljs-time/day d1) (cljs-time/day d2))))

(defn- format-int [i]
  (if (< i 10)
    (str "0" i)
    (str i)))

(defn format-time [s]
  (let [time (cljs-format/parse s)
        today (cljs-time/now)]
    (if (is-same-date time today)
      (str "today " (format-int (cljs-time/hour time)) ":" (format-int (cljs-time/minute time)))
      (str (format-int (cljs-time/day time)) "." (format-int (cljs-time/month time)) "." (format-int (cljs-time/year time))))))

;; -------------------------
;; Views

(defn result-view [result]
  ^{:key result}[:li (:title result)
   [:p (-> result :time format-time)]
   [:p (:snippet result)]
   [:a {:class "btn btn-primary" :href (:url result) :target "_blank"} "Read more..."]])

(defn result-views [results]
  [:ol
   (map result-view results)])

(defn home-page []
  [:div
    [:div {:style {:text-align "center"}}
     [:h2 "News"]
     [:input {:type "text"
              :placeholder "What would you like to find?"
              :size 100
              :value @text-for-search
              :on-change #(reset! text-for-search (-> % .-target .-value))}]
     [:br]
      [:br]
      [:button {:class "btn btn-success" :on-click search} "Search"] [:a {:class "btn btn-info" :href "#/about"} "About the site"]]
    [:div {:style {:text-align "left"}}
     (result-views @search-results)]])

(defn about-page []
  [:div
    [:div {:style {:text-align "center"}}
     [:h2 "About the site"]]
    [:div {:style {:text-align "left"}}
     [:p "The site was designed as a hobby project with several goals:"]
     [:ol
      [:li "Try Clojure and ClojureScript"]
      [:li "Try Emacs and LightTable for development and choose one of them"]
      [:li "Develop collaborative project with my students"]]
     [:div [:a {:class "btn btn-info" :href "#/"} "Go to the search page"]]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn init! []
  (hook-browser-navigation!)
  (reagent/render-component [current-page] (.getElementById js/document "app")))
