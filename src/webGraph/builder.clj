(ns webGraph.builder
  (:use [clojure.string :only [lower-case]])
  (:require [webGraph.neo4j-rest :as neo]))


(defn- find-or-create-page!
  [url]
  (neo/find-or-create! :pages :url url))

(defn- link-pages!
  "Create an edge to another page for every out-going link."
  [page-node links]
  (doseq [link links
          :let [other-page-node (find-or-create-page! link)]]
    (neo/create-rel! page-node :links-to other-page-node)))

(defn- find-or-create-word!
  [word]
  (neo/find-or-create! :words :word word))

(defn- index-words!
  "Create an edge from all of the words in this page."
  [page-node text]
  (let [words (clojure.string/split (lower-case text) #"\W+")]
    (doseq [word (distinct words)
            :let [word-node (find-or-create-word! word)]]
      (neo/create-rel! word-node :is-in page-node))))


(defn add-seed!
  "Specify that the given url was used to seed the web crawler.
   A 'seed' edge from the root node to that page will be created."
  [url]
  (let [node (find-or-create-page! url)]
    (neo/create-rel! (neo/get-root) :seed node)))

(defn add-page!
  "Add a page to the graph with a list of urls as out edges.
   The given text will be added as a property and will be indexed by
   adding links from the word nodes to this page node."
  [url text links]
  (let [page-node (find-or-create-page! url)]
    (neo/set-props! page-node {:url url, :text text})
    (index-words! page-node text)
    (link-pages! page-node links)))
