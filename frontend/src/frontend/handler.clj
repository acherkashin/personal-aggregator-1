(ns frontend.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [frontend.routes.home :refer [home-routes]])
  (:gen-class))

(defn init []
  (println "frontend is starting"))

(defn destroy []
  (println "frontend is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)))

(defn start [port]
  (jetty/run-jetty #'app {:port port :join? false}))

(defn -main [& args]
  (println "starting web server...")
  (start 80))
