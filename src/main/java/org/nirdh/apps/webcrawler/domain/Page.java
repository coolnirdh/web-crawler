package org.nirdh.apps.webcrawler.domain;

import lombok.Data;

/**
 * Represents a page on the internet.
 */
@Data
public class Page {
    private final String url;
    private final int statusCode;
    private final String content;
}
