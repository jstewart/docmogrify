(ns docmogrify.handler
  (:use compojure.core
        environ.core
        ring.adapter.jetty
        ring.middleware.json
        ring.middleware.token-authentication
        ring.util.response)
  (:require [clojure.java.io    :as io]
            [compojure.handler  :as handler]
            [compojure.route    :as route]
            [docmogrify.prince  :as prince]))

(defn authenticated? [t]
  (= t (env :auth-token)))

(defn serve-pdf
  "convert a media spec (m) and serve it as PDF"
  [m]
  (let [[res output] (prince/run-cmd m)]
    (if res
      ;; This has to be coreced into an InputStream in order to clean up.
      ;; We delete the file, but it doesn't get ulinked since we have a handle via
      ;; the input stream. After render, ring will release the InputStream, causing the
      ;; handle to expire and the file to be unlinked.
      (let [is (io/input-stream output)]
        (.delete output)
        {:status  200
         :headers {"Content-Type" "application/pdf"
                   "Content-Disposition" (str "attachment; filename=" (:output-file m))}
         :body is})
      {:status 400 :body output})))

(defroutes app-routes
  (POST "/convert" {params :body}
        (serve-pdf params))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (wrap-json-body {:keywords? true})
      (wrap-token-authentication authenticated?)))

(defn start-server []
  (run-jetty #'app {:port (env :port 8080) :join? false}))

(defn -main [& args]
  (start-server))
