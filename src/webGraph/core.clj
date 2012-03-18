(ns webGraph.core  
  (:use [webGraph.crawl :only [crawl]]
        [webGraph.builder :only [init-database]]))

(defn -main [& args]
  (org.apache.log4j.PropertyConfigurator/configure "logging.properties")
  (init-database)
  (crawl ["http://www.cse.unr.edu/"] 10 100 100 6))
