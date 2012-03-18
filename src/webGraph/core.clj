(ns webGraph.core  
  (:use [webGraph.crawl :only [crawl]]))

(defn -main [& args]
  (org.apache.log4j.PropertyConfigurator/configure "logging.properties")
  (crawl ["http://www.cse.unr.edu/"] 10 100 100 6))
