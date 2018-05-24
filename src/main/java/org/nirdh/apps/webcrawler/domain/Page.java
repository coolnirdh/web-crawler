package org.nirdh.apps.webcrawler.domain;

import lombok.Data;

import java.util.List;

/**
 * Represents the information extracted from parsing the contents of a web document.
 */
@Data
public class Page {
    private final String url;
    private final String title;
    private final List<String> outgoingLinks;
}
