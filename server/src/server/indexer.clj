(ns server.indexer
  (:require [clucy.core :as clucy]))

(def index (clucy/memory-index))

(defn insert [doc]
  (clucy/add index doc))

(defn search [query]
  (clucy/search index (:keywords query) 10))
