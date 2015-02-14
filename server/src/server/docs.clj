(ns server.docs
  (:require [korma.db   :refer :all]
            [korma.core :refer :all]
            [clj-time.coerce :as coerce]
            [server.entities :as entities]
            [cheshire.core :refer :all]))

(defn exists [url]
  (let [count (:count (first
                    (select entities/docs
                            (aggregate (count :*) :count)
                            (where {:url url}))))]
    (> count 0)))

(defn select-doc [id]
  (first
    (select entities/docs
            (fields :id :url :title :snippet :time)
            (where {:id id}))))

(defn insert-doc [doc]
  (let [{:keys [url title snippet time]} doc]
    (:id (insert entities/docs
                 (values {:url url
                          :title title
                          :snippet snippet
                          :time (coerce/to-sql-date time)})))))

(defn update-doc [doc]
  (let [{:keys [id url title snippet time]} doc]
    (update entities/docs
            (set-fields {:url url
                         :title title
                         :snippet snippet
                         :time (coerce/to-sql-date time)})
            (where {:id id}))))

(defn delete-doc [id]
  (delete entities/docs
          (where {:id id})))

(defn delete-all []
  (delete entities/docs))
