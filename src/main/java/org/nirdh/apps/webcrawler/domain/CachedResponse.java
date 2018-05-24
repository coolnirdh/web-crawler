package org.nirdh.apps.webcrawler.domain;

import lombok.Data;

/**
 * Represents simplified version of HttpResponse for offline usage.
 * Most implementations of HttpResponse require working with streams of data or holding resources of some kind.
 * This class enables one to cache required information from HttpResponse and let go of such resources.
 */
@Data
public class CachedResponse {
    private final String url;
    private final int statusCode;
    private final String content;
}
