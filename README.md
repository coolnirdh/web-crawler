# Web Crawler

## Requirements for MVP:
1. Simple: Must only fulfil core requirements, will evolve in iterations
2. Single-domain: Must not follow external links
3. Fast: Must be able to crawl sites with blogs
4. Prints sitemap showing links between pages

## Product Roadmap:
1. Stateful: Must preserve state beyond crawl lifecycle to be able to resume partial crawls
2. Usable: Must allow users across the world to submit requests for crawling a domain or specific page via some URL
3. Asynchronous: Must not wait until end of crawling to print sitemap, user must be able to see crawling progress (number of links discovered and crawled) and request partial sitemap
4. Robust: Should not have single point of failures
5. Scalable and Distributed: Must be able to crawl sites like Wikipedia or Twitter
6. Polite: Introduces itself to sites as a crawler, respects disallow and crawl-delay policies using robots.txt
7. Content Indexing: Must be able to extract and store relevant content by scraping pages
8. Automatic Reindexing: Must regularly reindex known pages based on calculated page importance, and changeFrequency / priority specifications found in sitemap.xml
9. Search: Must allow users to access indexed information and suggest related pages.

## Architecture:

Attaining speed and building for scalability in a streaming problem like web crawling calls for micro-services. But for the scope of MVP Spring Integration will be used instead, for simplicity of deployment and demos.

### Fetcher:
Caches response from a given URL. It is most likely to be the slowest component in MVP version. Needs good amount of parallelism and optimizations, one such optimization is maintaining a pool of reusable and persistent connections, and the fact that Fetcher retrieves a response, caches relevant parts of it and lets go of the underlying resources helps us scale better.

### Parser:
Uses the CachedResponse to extract meaningful Page information using a single parse over the HTML string. As of MVP version, the only information extracted is outgoing links and page titles.

### Crawl Frontier:
Maintains state about Pages crawled and decides which outgoing links must be requested for crawling next. It discards links to external websites, and ignores pages already crawled.

### Page Repository:
The idea behind persisting parsed Page information is that we could later introduce indexing and search functionality based on the data accumulated. Page Repository is currently only implemented using ConcurrentHashMap for the sake of MVP. Ideally, a No-SQL store like ElasticSearch works best.

### Stale Connections Evictor:
We are using some low-level optimizations for fetching Responses across multiple pages in parallel. However, the HttpClient API itself has certain limitations and require us to regularly evict stale connections. Hence we use a scheduler with configurable delay to manage connection evictions behind the scene.
