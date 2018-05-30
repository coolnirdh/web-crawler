package org.nirdh.apps.webcrawler.domain;

import lombok.Data;

/**
 * Represents a crawl request that CrawlFrontier spawns.
 */
@Data
public class CrawlRequest {
    private final String urlToCrawl;
    private final String referrerUrl;
}
