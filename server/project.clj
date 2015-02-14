(defproject server "0.1.0-SNAPSHOT"
  :description "The first sketch"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clucy "0.4.0"]
                 [ring "1.3.2"]
                 [ring-cors "0.1.5"]
                 [ring/ring-codec "1.0.0"]
                 [compojure "1.3.1"]
                 [liberator "0.12.2"]
                 [cheshire "5.4.0"]
                 [clj-time "0.9.0"]
                 [korma "0.4.0"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]]
  :plugins [[lein-ring "0.8.13"]]

  :ring {:handler server.handler/application}

  :main ^:skip-aot server.handler
  :uberjar-name "server.jar"
  :profiles {:uberjar {:aot :all} 
             :dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]}}
  )
