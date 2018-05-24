package org.nirdh.apps.webcrawler.domain;

import lombok.Data;

/**
 * Represents a crawl request that can be filtered by application.
 */
@Data
public class CrawlRequest {
    private final String referrerUrl;
    private final String urlToCrawl;
}
