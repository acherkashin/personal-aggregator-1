(ns server.entities
  (:require [korma.db   :refer :all]
            [korma.core :refer :all]
            [cheshire.core :refer :all]))

(defn- get-settings []
  (decode (slurp "settings.txt") true))

(def db-name "docs")

(def settings (get-settings))
(def user (:user settings))
(def password (:password settings))

(defdb db
       (postgres {:db db-name
                  :user user
                  :password password}))

(defentity docs
           (pk :id)
           (table :docs)
           (database db))
