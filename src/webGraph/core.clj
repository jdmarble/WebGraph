(ns webGraph.core  
  (:use [webGraph.crawl :only [crawl]])
  (:require [borneo.core :as neo]))

(defn -main [& args]
  (neo/with-db! "page-graph"
    (crawl ["http://www.cse.unr.edu/"] -1 -1 1000 6)))