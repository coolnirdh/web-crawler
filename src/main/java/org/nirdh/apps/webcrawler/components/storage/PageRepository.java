package org.nirdh.apps.webcrawler.components.storage;

import org.nirdh.apps.webcrawler.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimalistic version of a fast data store.
 */
@Repository
public class PageRepository {

    private final Map<String, Page> pagesByUrl;

    public PageRepository() {
        pagesByUrl = new ConcurrentHashMap<>();
    }

    public void store(Page page) {
        pagesByUrl.put(page.getUrl(), page);
    }

    public Page findByUrl(String url) {
        return pagesByUrl.get(url);
    }

    public void markAsScheduledForCrawl(String url) {
        pagesByUrl.put(url, new Page(url, null, null));
    }

    public boolean isCrawledOrScheduledForCrawl(String url) {
        return pagesByUrl.containsKey(url);
    }
}
