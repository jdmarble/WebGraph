(ns webGraph.builder
  (:require [borneo.core :as neo]))

(defn- find-or-create [url]
  (let [index (.forNodes (neo/index) "pages")
        hits (.get index "url" url)
        result (.getSingle hits)]
    (.close hits)
    (if (nil? result)
      (let [node (neo/create-node! {:url url})]
        (.add index node "url" url)
        node)
      result)))

(defn add-seed! [url]
  (neo/with-tx
    (let [node (find-or-create url)]
      (neo/create-rel! (neo/root) :seed node))))

(defn add-page! [url text links]
  (println url (count links))
  (neo/with-tx
    (let [page (find-or-create url)]
      (neo/set-props! page {:text text})
      (doseq [link links
              :let [other-page (find-or-create link)]]
        (neo/create-rel! page :links-to other-page)))))