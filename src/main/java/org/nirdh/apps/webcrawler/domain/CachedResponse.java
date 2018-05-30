package org.nirdh.apps.webcrawler.domain;

import lombok.Data;
import org.apache.commons.lang3.Validate;

/**
 * Represents simplified version of HttpResponse for offline usage.
 * Most implementations of HttpResponse require working with streams of data or holding resources of some kind.
 * This class enables one to cache required information from HttpResponse and let go of such resources.
 */
@Data
public class CachedResponse {
    private static final int MIN_STATUS_CODE = 100;
    private static final int MAX_STATUS_CODE = 599;

    private final String url;
    private final int statusCode;
    private final String content;

    public CachedResponse(String url, int statusCode, String content) {
        Validate.notNull(url, "url must not be null");
        Validate.inclusiveBetween(MIN_STATUS_CODE, MAX_STATUS_CODE, statusCode, String.format("statusCode must be between %d and %d but was %d", MIN_STATUS_CODE, MAX_STATUS_CODE, statusCode));
        Validate.notNull(content, "content must not be null");

        this.url = url;
        this.statusCode = statusCode;
        this.content = content;
    }
}
