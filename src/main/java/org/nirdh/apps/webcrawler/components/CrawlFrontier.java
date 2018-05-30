package org.nirdh.apps.webcrawler.components;

import org.nirdh.apps.webcrawler.components.storage.PageRepository;
import org.nirdh.apps.webcrawler.domain.CrawlRequest;
import org.nirdh.apps.webcrawler.domain.Link;
import org.nirdh.apps.webcrawler.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores Page information in PageRepository and spawns CrawlRequests for
 * links that belong to the same domain and have never been visited.
 */
@Component
public class CrawlFrontier {

    private final PageRepository pageRepository;

    @Autowired
    public CrawlFrontier(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public List<CrawlRequest> spawnRequests(Page page) {
        pageRepository.store(page);
        return page.getInternalLinks().stream()
                .map(Link::getUrlWithoutFragment)
                .distinct()
                .filter(url -> !pageRepository.contains(url))
                .peek(pageRepository::store)
                .map(url -> new CrawlRequest(url, page.getUrl()))
                .collect(Collectors.toList());
    }
}
