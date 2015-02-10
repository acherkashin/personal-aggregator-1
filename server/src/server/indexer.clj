(ns server.indexer
  (import org.apache.lucene.analysis.en.EnglishAnalyzer)
  (import org.apache.lucene.analysis.ru.RussianAnalyzer)
  (:require [clucy.core :as clucy]))

(def path-en "/tmp/news_en")
(def path-ru "/tmp/news_ru")

(def index-en (clucy/disk-index path-en))
(def index-ru (clucy/disk-index path-ru))

(def regex-en (re-pattern "[a-zA-Z]"))
(def regex-ru (re-pattern "[а-яА-Я]"))

(defn- detect-lang [s]
  (let [en-count (count (re-seq regex-en s))
        ru-count (count (re-seq regex-ru s))]
    (if (> en-count ru-count)
      "en"
      "ru")))

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

(defn- insert-en [doc]
  (clucy/add index-en doc))

(defn- insert-ru [doc]
  (clucy/add index-ru doc))

(defn- check-required-fields [doc]
  (if (= (:url doc) nil)
    (throw (Exception. "You must define field :url"))
    (if (= (:title doc) nil)
      (throw (Exception. "You must define field :title"))
      (if (= (:snippet doc) nil)
        (throw (Exception. "You must define field :snippet"))
        (if (= (:time doc) nil)
          (throw (Exception. "You must define field :time"))
          nil)))))

(defn- insert- [index doc]
  (check-required-fields doc)
  (clucy/add index
             (with-meta doc
               {:url {:stored true
                      :indexed false
                      :analyzed false}
                :time {:stored true
                       :indexed false
                       :analyzed false}})))

(defn delete-all []
  (delete-directory path-en)
  (delete-directory path-ru)
  (insert-en {:title "" :url ""})
  (insert-ru {:title "" :url ""}))

(defn insert [doc]
  (let [text (str (:title doc) " " (:snippet doc)) lang (detect-lang text)]
    (if (= lang "en")
      (insert- index-en doc)
      (if (= lang "ru")
        (insert- index-ru doc)
        (throw (Exception. (str "Language " lang " is not supported")))))))

(defn- search-en [query max]
  (clucy/search index-en query max))

(defn- search-ru [query max]
  (clucy/search index-ru query max))

(defn search [obj]
  (let [query (:keywords obj) max 10]
    (let [en-results (search-en query max) ru-results (search-ru query max)]
      (concat en-results ru-results))))
