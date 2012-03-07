(ns webGraph.crawler
  (:use [webGraph.builder :only [add-page!]])
  (:import (edu.uci.ics.crawler4j.crawler Page WebCrawler)
           (edu.uci.ics.crawler4j.parser HtmlParseData)
           (edu.uci.ics.crawler4j.url WebURL))
  (:gen-class :name webGraph.Crawler
              :extends edu.uci.ics.crawler4j.crawler.WebCrawler
              :prefix crawler-))

(def binary-extension
  #".*(\.(css|js|bmp|gif|jpe?g|png|tiff?
   |mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram
   |m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz
   |doc|docx|xls))$")

(defn crawler-shouldVisit [this ^WebURL url]
  (let [href (-> url .getURL .toLowerCase)]
    (and     
     (.startsWith href "http://www.cse.unr.edu")
     (not (re-matches binary-extension href)))))

(defn crawler-visit [this ^Page page]
  (let [^HtmlParseData parsed-html (.getParseData page)
        url (-> page .getWebURL .getURL)
        text (.getText parsed-html)
        links (->> parsed-html .getOutgoingUrls
                   (filter #(crawler-shouldVisit this %))
                   (map #(.getURL %))
                   doall)]
    (add-page! url text links)))