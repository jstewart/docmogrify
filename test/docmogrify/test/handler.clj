(ns docmogrify.test.handler
  (:use clojure.test
        ring.mock.request
        environ.core
        docmogrify.handler))

(deftest test-app
  (testing "a document conversion"
    (let [response (app (-> (request :post "/convert")
                            (content-type "application/json")
                            (header "Authorization" "Token token=test-token")
                            (body (slurp "test/docmogrify/fixtures/prince.json"))))]
      (is (= (:status response) 200))
      (is (= (get-in response [:headers "Content-Type"])
             "application/pdf"))
      (is (= (get-in response [:headers "Content-Disposition"])
             "attachment; filename=test.pdf"))))

  (testing "not-found route"
    (let [response (app
                    (header
                     (request :get "/invalid")
                     "Authorization" "Token token=test-token"))]
      (is (= (:status response) 404)))))
