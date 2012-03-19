(ns webGraph.builder
  (:require [webGraph.neo4j-rest :as neo]))


(defn find-or-create-page!
  [url]
  (neo/find-or-create! :pages :url url))

(defn add-seed! [url]
  "Specify that the given url was used to seed the web crawler.
   A 'seed' edge from the root node to that page will be created."
  (let [node (find-or-create-page! url)]
    (neo/create-rel! (neo/get-root) :seed node)))

(defn add-page! [url text links]
  "Add a page to the graph with a list of urls as out edges."
  (let [page (find-or-create-page! url)]
    (neo/set-props! page {:url url, :text text})
    (doseq [link links
            :let [other-page (find-or-create-page! link)]]
      (neo/create-rel! page :links-to other-page))))
