(defproject frontend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/cljs"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                         [cljsjs/react "0.12.2-5"]
                         [reagent "0.5.0-alpha3"]
                         [reagent-forms "0.4.3"]
                         [reagent-utils "0.1.2"]
                         [secretary "1.2.1"]
                         [cljs-ajax "0.3.10"]
                         [com.andrewmcveigh/cljs-time "0.3.2"]
                         [org.clojure/clojurescript "0.0-2850" :scope "provided"]]

  :plugins [[lein-cljsbuild "1.0.4"]
                [lein-environ "1.0.0"]
                [lein-asset-minifier "0.2.2"]]

  :min-lein-version "2.5.0"

  :clean-targets ^{:protect false} ["resources/public/js"]

  :minify-assets {:assets {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs" "env/dev/cljs"]
                             :compiler {:output-to     "resources/public/js/app.js"
                                        :output-dir    "resources/public/js/out"
                                        ;;:externs       ["react/externs/react.js"]
                                        :asset-path   "js/out"
                                        :optimizations :advanced;:none
                                        :pretty-print  true
                                        :main "frontend.dev" ;:source-map true
                                        }}}}

  :profiles {:dev {
                   :dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.3.2"]
                                  [leiningen "2.5.1"]
                                  [figwheel "0.2.5-SNAPSHOT"]
                                  [weasel "0.6.0-SNAPSHOT"]
                                  [com.cemerick/piggieback "0.1.6-SNAPSHOT"]
                                  [pjstadig/humane-test-output "0.6.0"]]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.2.3-SNAPSHOT"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev? true}}})
