(defproject docmogrify "0.1.0"
  :description "Convert documents from one type to another"
  :url "https://github.com/jstewart/docmogrify"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [environ "0.4.0"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [ring/ring-json "0.2.0"]
                 [ring-token-authentication "0.1.0"]]
  :main docmogrify.handler
  :aot [docmogrify.handler]
  :plugins [[lein-environ "0.4.0"] [lein-ring "0.8.10"]]
  :ring {:handler docmogrify.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]
         :env {:auth-token   "letmein"}}
   :test {:env {:auth-token "test-token"}}})
