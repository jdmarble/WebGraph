(ns webGraph.core  
  (:use [clojure.tools.cli :only [cli]]
        [webGraph.crawl :only [crawl]]
        [webGraph.neo4j-rest :only [neo4j-host neo4j-port]]))

(defn -main [& args]
  (org.apache.log4j.PropertyConfigurator/configure "logging.properties")
  (let [[options args banner]
        (cli args
             ["-h" "--help" "Show this help message"
              :flag true]
             ["--host" "Neo4J server host"
              :default @neo4j-host]
             ["--port" "Neo4J server port"
              :default @neo4j-port :parse-fn #(Integer. %)]
             ["-s" "--seed" "Fully qualified URL for the crawl"]
             ["-p" "--max-pages" "Maximum number of pages to crawl"
              :default 10 :parse-fn #(Integer. %)]
             ["-d" "--max-depth" "Maximum crawl depth"
              :default 5 :parse-fn #(Integer. %)]
             ["-n" "--politeness" "Time to wait between page requests (ms)"
              :default 1000 :parse-fn #(Integer. %)]
             ["-j" "--threads" "Number of crawler threads"
              :default 1 :parse-fn #(Integer. %)])
        {:keys [help host port
                seed max-depth max-pages politeness threads]} options]
    (swap! neo4j-host (constantly host))
    (swap! neo4j-port (constantly port))
    (if help
      (println banner)
      (when seed
        (crawl seed max-depth max-pages politeness threads)))))
