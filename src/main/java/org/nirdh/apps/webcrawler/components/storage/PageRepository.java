package org.nirdh.apps.webcrawler.components.storage;

import org.nirdh.apps.webcrawler.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimalistic version of a fast data store that persists information extracted after parsing a Page.
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

    public void store(String url) {
        pagesByUrl.put(url, new Page(url, null));
    }

    public Page findByUrl(String url) {
        return pagesByUrl.get(url);
    }

    public boolean contains(String url) {
        return pagesByUrl.containsKey(url);
    }
}
