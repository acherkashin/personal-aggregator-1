(ns server.indexer
  (import org.apache.lucene.analysis.en.EnglishAnalyzer)
  (import org.apache.lucene.analysis.ru.RussianAnalyzer)
  (:require [clucy.core :as clucy]
            [ring.util.codec :as codec]
            [clj-time.core :as clj-time]
            [clj-time.coerce :as coerce]
            [server.docs :as docs]))

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
               {:id {:stored true
                     :indexed true
                     :analyzed false}
                :url {:stored true
                      :indexed true
                      :analyzed false}
                :time {:stored true
                       :indexed false
                       :analyzed false}})))

(defn delete-all []
  (docs/delete-all)
  (delete-directory path-en)
  (delete-directory path-ru)
  (insert- index-en {:title "" :url "" :snippet "" :time ""})
  (insert- index-ru {:title "" :url "" :snippet "" :time ""}))

(defn insert [doc]
  (let [id (docs/insert-doc doc)
        text (str (:title doc) " " (:snippet doc))
        lang (detect-lang text)
        time (str (:time doc))]
    (if (= lang "en")
      (insert- index-en (assoc doc :id id))
      (if (= lang "ru")
        (insert- index-ru (assoc doc :id id))
        (throw (Exception. (str "Language " lang " is not supported")))))))

(defn- search-en [query max]
  (clucy/search index-en query max))

(defn- search-ru [query max]
  (clucy/search index-ru query max))

(defn search [obj]
  (let [query (:keywords obj) max 10]
    (let [en-results (search-en query max) ru-results (search-ru query max)]
      (concat en-results ru-results))))

(defn exists [url]
  (docs/exists url))
