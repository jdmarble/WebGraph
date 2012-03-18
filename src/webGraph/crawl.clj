(ns webGraph.crawl
  (:use [webGraph.builder :only [add-seed!]]
        [webGraph.crawler :only [base-url]])
  (:import (webGraph Crawler)
           (edu.uci.ics.crawler4j.crawler
            CrawlConfig CrawlController)
           (edu.uci.ics.crawler4j.fetcher
            PageFetcher)
           (edu.uci.ics.crawler4j.robotstxt
            RobotstxtConfig RobotstxtServer)))

(defn crawl [seed max-depth max-pages politeness crawler-count]
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
    (compare-and-set! base-url nil seed)
    (add-seed! seed)
    (.addSeed controller seed)
    (.start controller Crawler crawler-count)))