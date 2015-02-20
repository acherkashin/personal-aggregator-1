(ns server.handler
  (:require [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes ANY GET]]
            [compojure.handler :refer [site]]
            [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [cheshire.core :refer :all]
            [clj-time.core :as clj-time]
            [clj-time.format :as clj-format]
            [clj-time.coerce :as coerce]
            [server.indexer :as indexer]
            [server.ddl :as ddl]
            [server.docs :as docs])
  (:gen-class))

(def media-types ["application/json"])

(defn parse-json [ctx] (-> ctx :request :body slurp (decode true)))
(defn parse-query [ctx] (-> ctx :request :query-params (clojure.walk/keywordize-keys)))

(defresource version []
  :available-media-types media-types
  :handle-ok {:version 0.2})

(defresource insert-document []
             :allowed-methods [:post :options]
             :available-media-types media-types
             :post!     (fn [ctx] (let [doc (parse-json ctx)]
                                    (if (docs/exists (:url doc))
                                      {::status (indexer/insert
                                                 (assoc doc :time (clj-format/parse (:time doc))))}
                                      {::status "exists"})))
             :handle-created ::status)

(defresource search []
             :allowed-methods [:get :options]
             :available-media-types media-types
             :exists?     (fn [ctx] {::results (indexer/search (parse-query ctx))})
             :handle-ok ::results)

(defresource not-found []
             :allowed-methods [:get :post :put :delete :option]
             :available-media-types media-types
             :exists? (fn [_] false)
             :handle-not-found (fn [_] "Not found"))

(defroutes routes
  (GET "/" [] (version))
  (ANY "/insert-document" [] (insert-document))
  (ANY "/search" [] (search))
  (ANY "*" [] (not-found)))

(defn allow-cross-origin
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (assoc-in [:headers "Access-Control-Allow-Origin"]  "*")
          (assoc-in [:headers "Access-Control-Allow-Methods"] "GET,PUT,POST,DELETE,OPTIONS")
          (assoc-in [:headers "Access-Control-Allow-Headers"] "X-Requested-With,Content-Type,Cache-Control")))))

(def application (-> routes  
                     ;(wrap-trace :header :ui);it can cause problems with big data
                     wrap-params 
                     allow-cross-origin))

(defn- start [port]
  (jetty/run-jetty #'application {:port port :join? false}))

(defn -main [& args]
  (println "starting...")
  (if-let [option (first args)]
    (if (= option "create-db")
      (do
        (ddl/drop-schema)
        (ddl/create-schema)
        (ddl/create-tables)
        (indexer/delete-all)
        (println "A database was created")))
    (start 3000)))
