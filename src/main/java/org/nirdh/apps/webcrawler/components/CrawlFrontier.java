package org.nirdh.apps.webcrawler.components;

import org.nirdh.apps.webcrawler.components.storage.PageRepository;
import org.nirdh.apps.webcrawler.domain.CrawlRequest;
import org.nirdh.apps.webcrawler.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
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
        String domain = URI.create(page.getUrl()).getHost();
        return page.getOutgoingLinks().stream()
                .filter(link -> isNonEmpty(link)
                        && !link.startsWith("mailto:")
                        && URI.create(link).getHost().endsWith(domain)
                        && !pageRepository.isCrawledOrScheduledForCrawl(link))
                .map(link -> link.replaceFirst("#.*$", ""))
                .distinct()
                .peek(pageRepository::markAsScheduledForCrawl)
                .map(link -> new CrawlRequest(page.getUrl(), link))
                .collect(Collectors.toList());
    }

    private boolean isNonEmpty(String link) {
        return link != null && link.trim().length() > 0;
    }
}
