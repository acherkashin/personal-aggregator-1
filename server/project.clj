(defproject server "0.1.0-SNAPSHOT"
  :description "The first sketch"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clucy "0.4.0"]
                 [ring "1.3.2"]
                 [ring-cors "0.1.5"]
                 [compojure "1.3.1"]
                 [liberator "0.12.2"]
                 [cheshire "5.4.0"]
                 [clj-time "0.9.0"]
                 [com.novemberain/monger "2.0.0"]]
  :plugins [[lein-ring "0.8.13"]]

  :ring {:handler server.handler/application}

  :main ^:skip-aot server.handler
  :uberjar-name "server.jar"
  :profiles {:uberjar {:aot :all} 
             :dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]}}
  )
