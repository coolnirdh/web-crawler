package org.nirdh.apps.webcrawler.components;

import org.apache.commons.lang3.Validate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.nirdh.apps.webcrawler.domain.CachedResponse;
import org.nirdh.apps.webcrawler.domain.Page;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extracts meaningful information from CachedResponse in a single parse of the content.
 */
@Component
public class Parser {

    public Page parse(CachedResponse cachedResponse) {
        Validate.notNull(cachedResponse, "cachedResponse must not be null");
        Document document = Jsoup.parse(cachedResponse.getContent(), cachedResponse.getUrl());
        String host = URI.create(cachedResponse.getUrl()).getHost();
        List<String> outgoingLinks = document.select("a[href]").stream()
                .map(anchorTag -> anchorTag.attr("abs:href").replaceFirst("\\?|\\/$", ""))
                .filter(link -> URI.create(link).getHost().endsWith(host))
                .distinct()
                .collect(Collectors.toList());
        return new Page(cachedResponse.getUrl(), document.title(), outgoingLinks);
    }
}
