(ns webGraph.crawl
  (:use [webGraph.builder :only [add-seed!]])
  (:import (webGraph Crawler)
           (edu.uci.ics.crawler4j.crawler
            CrawlConfig CrawlController)
           (edu.uci.ics.crawler4j.fetcher
            PageFetcher)
           (edu.uci.ics.crawler4j.robotstxt
            RobotstxtConfig RobotstxtServer)))

(defn crawl
  ([seeds]
     (crawl seeds 5 5 1000 1))
  ([seeds max-depth max-pages politeness crawler-count]
     (let [config (doto (CrawlConfig.)
                    (.setCrawlStorageFolder "temp")
                    (.setMaxDepthOfCrawling max-depth)
                    (.setMaxPagesToFetch max-pages)
                    (.setPolitenessDelay politeness)
                    (.setResumableCrawling false))
           fetcher (PageFetcher. config)
           robots-txt-config (RobotstxtConfig.)
           robots-txt-server (RobotstxtServer. robots-txt-config fetcher)
           controller (CrawlController. config fetcher robots-txt-server)]
       (doseq [seed seeds]
         (add-seed! seed)
         (.addSeed controller seed))
       (.start controller Crawler crawler-count))))