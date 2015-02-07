(ns server.indexer
  (:require [clucy.core :as clucy]))

(def path "/tmp/news")

(def index (clucy/disk-index path))

(defn- safe-delete [file-path]
  (if (.exists (clojure.java.io/file file-path))
    (try
      (clojure.java.io/delete-file file-path)
      (catch Exception e (str "exception: " (.getMessage e))))
    false))

(defn- delete-directory [directory-path]
  (let [directory-contents (file-seq (clojure.java.io/file directory-path))
        files-to-delete (filter #(.isFile %) directory-contents)]
    (doseq [file files-to-delete]
      (safe-delete (.getPath file)))
    (safe-delete directory-path)))

(defn delete-all []
  (delete-directory path))

(defn insert [doc]
  (clucy/add index doc))

(defn search [query]
  (clucy/search index (:keywords query) 10))
