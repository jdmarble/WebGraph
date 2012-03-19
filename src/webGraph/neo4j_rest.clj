(ns webGraph.neo4j-rest
  "Neo4j REST interface"
  (:require [clj-http.client :as http]
            [cheshire.core :as json])
  (:import [java.net URLEncoder]))

(def neo4j-host (atom "localhost"))
(def neo4j-port (atom 7474))

(defn- db-service
  "Create a service URL by combining the root database with the
  service name."
  [service]
  (str "http://" @neo4j-host ":" @neo4j-port "/db/data/" service))

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

(defn create-node!
  "Create a new node with the given properties. Return a value that
  represents the node."
  [property-map]
  (db-req :post (db-service "node") property-map))

(defn set-props!
  "Replace the property map of a node with a new map."
  [node properties]
  (db-req :put (:properties node) properties))

(defn create-rel!
  "Add an edge between two nodes with the given type."
  [from type to]
  (db-req :post (:create_relationship from)
          {:to (:self to), :type type}))

(defn get-root []
  "Return a value representing the root node."
  (db-req :get (db-service "node/0")))

(defn- index-service
  ([index]
     (db-service (str "index/node/"
                      (name index) "/")))
  ([index key]
     (db-service (str "index/node/"
                      (name index) "/"
                      (name key) "/")))
  ([index key value]
     (db-service (str "index/node/"
                      (name index) "/"
                      (name key) "/"
                      (URLEncoder/encode value)))))

(defn index-node!
  "Place a node into an index given the value of a particular key."
  [node index key]
  {:pre [(get-in node [:data key])]}
  (db-req :post (index-service index)
          {:key (name key)
           :value (get-in node [:data key])
           :uri (:self node)}))

(defn lookup-node [index key value]
  (try 
    (first (db-req :get (index-service index key value)))
    (catch Exception e ; Return nil if not found (404)
      nil)))

(defn find-or-create!
  "If a node already exists in the index with that value for the key then return it.
  Otherwise, create a new node with the key and value, index it, and return it."
  [index key val]
  (let [result (lookup-node index key val)]
    (if (nil? result)
      (doto (create-node! {key val})
        (index-node! index key))
      result)))
