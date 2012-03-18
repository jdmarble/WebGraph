(defproject webGraph "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/tools.cli "0.2.1"]
                 [edu.uci.ics/crawler4j "3.3"]
                 [clj-http "0.3.2"]
                 [cheshire "2.2.2"]]
  :main webGraph.core
  :aot [webGraph.crawler])

