(ns server.ddl
  (:require [korma.db   :refer :all]
            [korma.core :refer :all]))

(def docs
  "CREATE TABLE docs (id serial PRIMARY KEY, url text NOT NULL,
    title text NOT NULL, snippet text NOT NULL, time timestamp NOT NULL);")

(def docs-unique
  "CREATE UNIQUE INDEX docs_unq ON docs (url);")

(defn drop-schema []
  (exec-raw "drop schema public cascade;"))

(defn create-schema []
  (exec-raw "create schema public;"))

(defn create-tables []
  (do
    (exec-raw docs)
    (exec-raw docs-unique)))
