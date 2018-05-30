package org.nirdh.apps.webcrawler.components;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.nirdh.apps.webcrawler.domain.CachedResponse;
import org.nirdh.apps.webcrawler.domain.Link;
import org.nirdh.apps.webcrawler.domain.Page;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Extracts meaningful information from CachedResponse in a single parse of the content.
 */
@Component
public class Parser {

    private static final Log logger = LogFactory.getLog(SpringApplication.class);

    public Page parse(CachedResponse cachedResponse) {
        Validate.notNull(cachedResponse, "cachedResponse must not be null");
        Document document = Jsoup.parse(cachedResponse.getContent(), cachedResponse.getUrl());
        Page page = new Page(cachedResponse.getUrl(), document.title());
        document.select("a[href]").stream()
                .map(this::getLink)
                .filter(Objects::nonNull)
                .forEach(page::add);
        return page;
    }

    private Link getLink(Element anchorTag) {
        String url = anchorTag.attr("abs:href");
        try {
            return new Link(url, anchorTag.text());
        } catch (Exception e) {
            logger.debug(String.format("Could not create Link for url: [%s]", url), e);
            return null;
        }
    }
}
