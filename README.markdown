# WebGraph

This tool crawls a website and generates a graph of pages and
links. Additionally, a node is added for each word and edges are added
from each word to the pages they are a part of.


## Installation

Only Leiningen is required for building.
[Install Leiningen](https://github.com/technomancy/leiningen), then
execute the following from the webGraph directory:

    webGraph$ lein deps
    webGraph$ lein compile

To actually do anything useful, you'll need access to a running
[Neo4J](http://neo4j.org/) server.  You can install this to your local
machine and start an instance:

    neo4j-community-1.6$ ./bin/neo4j start


## Usage

Use the `--help` command line option to see the possible arguments:

    webGraph$ lein run --help
    Usage:

    Switches               Default    Desc                                    
    --------               -------    ----                                    
    -h, --no-help, --help  false      Show this help message                  
    --host                 localhost  Neo4J server host                       
    --port                 7474       Neo4J server port                       
    -s, --seed                        Fully qualified URL for the crawl       
    -p, --max-pages        10         Maximum number of pages to crawl        
    -d, --max-depth        5          Maximum crawl depth                     
    -n, --politeness       1000       Time to wait between page requests (ms) 
    -j, --threads          1          Number of crawler threads 

To crawl a domain specify the fully qualified domain name as the seed:

    webGraph$ lein run --seed http://www.milk.com/

The crawler will follow links outside of the given domain.

## Future Work

* There needs to be a way to query the database outside of Neo4J's web
  interface. Either a command-line query tool or a custom web interface.
* The web crawler library that is used
  ([crawler4j](http://code.google.com/p/crawler4j/)) does a poor job
  at extracting plain-text from the HTML. Some words get squished
  together. This can be fixed by taking the raw HTML from the crawler
  and running it through something like [jsoup's](http://jsoup.org/)
  `Jsoup.parse(html).text()`.
* Currently, each distinct word from a crawled page requires either
  two or three calls to the server. This is very slow, but it can be
  sped up by using the
  [batch API](http://docs.neo4j.org/chunked/milestone/rest-api-batch-ops.html).
* To run PageRank on the graph, the server must be taken off-line so a
  custom Gremlin distribution can access it directly. Maybe there's a
  better way?


## License

Copyright (C) 2012 James Marble and John Russell

Distributed under the Eclipse Public License, the same as Clojure.
