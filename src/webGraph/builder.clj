(ns webGraph.builder
  (:require [clj-http.client :as http]
            [cheshire.core :as json])
  (:import [java.net URLEncoder]))

;; Low-level Neo4j interface
;; =========================

(defn- db-service
  "Create a service URL by combining the root database with the
  service name."
  [rest]
  (str "http://localhost:7474/db/data/" rest) )

(defn- db-req
  "Perform a REST/JSON request given a method (e.g. :post), URL, and
  optional body."
  ([method address]
     (db-req method address nil nil))
  ([method address body]
     (db-req method address body nil))
  ([method address body query]
     (->
      (http/request {:method method
                     :url address
                     :body (json/generate-string body)
                     :content-type :json
                     :accept :json
                     :query-params query})
      :body
      (json/parse-string true))))

(defn- create-node! [url]
  (db-req :post (db-service "node") {:url url}))

(defn- set-props! [node props]
  (let [clean-text (-> (:text props)
                       (.replaceAll "\\W+" " "))
        short-text (.substring clean-text 0
                               (min 1000 (count clean-text)))]
    (db-req :put (:properties node) (assoc props :text short-text))  
    (db-req :post (db-service "index/node/fulltext")
            {:key "text"
             :value short-text
             :uri (:self node)})))

(defn- create-rel! [from type to]
  (db-req :post (:create_relationship from)
          {:to (:self to), :type type}))

(defn- get-root []
  (db-req :get (db-service "node/0")))

(defn- get-page [url]
  (try 
    (->> (URLEncoder/encode url)       ; Escape the url characters
         (str "index/node/pages/url/") ; so it can be appended to this query
         db-service                    ; of the index service.
         (db-req :get)                 ; Send as GET method
         first)                        ; Get first and only result
    (catch Exception e                 ; Return nil if not found (404)
      nil)))

(defn- index-page! [node]
  (db-req :post (db-service "index/node/pages")
          {:key "url"
           :value (get-in node [:data :url])
           :uri (:self node)}))

(defn- find-or-create [url]
  (let [result (get-page url)]
    (if (nil? result)
      (doto (create-node! url)
        index-page!)
      result)))

;; Public Neo4j interface
;; ======================

(defn add-seed! [url]
  "Specify that the given url was used to seed the web crawler.
   A 'seed' edge from the root node to that page will be created."
  (let [node (find-or-create url)]
    (create-rel! (get-root) :seed node)))

(defn add-page! [url text links]
  "Add a page to the graph with a list of urls as out edges."
  (let [page (find-or-create url)]
    (set-props! page {:url url, :text text})
    (doseq [link links
            :let [other-page (find-or-create link)]]
      (create-rel! page :links-to other-page))))
