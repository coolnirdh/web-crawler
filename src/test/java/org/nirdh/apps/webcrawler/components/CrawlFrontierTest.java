package org.nirdh.apps.webcrawler.components;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nirdh.apps.webcrawler.components.storage.PageRepository;
import org.nirdh.apps.webcrawler.domain.CrawlRequest;
import org.nirdh.apps.webcrawler.domain.Page;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by Nirdh on 24-05-2018.
 */
public class CrawlFrontierTest {

    @Mock
    private PageRepository pageRepository;

    private CrawlFrontier crawlFrontier;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        crawlFrontier = new CrawlFrontier(pageRepository);
    }

    @Test
    public void storesPageToRepository() throws Exception {
        Page page = new Page("https://www.google.com", "title", Collections.emptyList());
        crawlFrontier.spawnRequests(page);
        verify(pageRepository).store(page);
    }

    @Test
    public void doesNotSpawnCrawlRequestForNullLinks() throws Exception {
        Page page = new Page("https://www.google.com", "title", Collections.singletonList(null));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void doesNotSpawnCrawlRequestForEmptyLinks() throws Exception {
        Page page = new Page("https://www.google.com", "title", Collections.singletonList("   "));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void doesNotSpawnCrawlRequestForLinksOutsidePageDomain() throws Exception {
        Page page = new Page("https://www.google.com", "title", Collections.singletonList("https://github.com/test"));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void doesNotSpawnCrawlRequestForLinksToParentDomainOfPage() throws Exception {
        Page page = new Page("https://www.google.com", "title", Collections.singletonList("https://google.com/test"));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void doesNotSpawnCrawlRequestForLinksAlreadyCrawled() throws Exception {
        doReturn(new Page("https://www.google.com:8080/test", "title", Collections.singletonList("")))
                .when(pageRepository).findByUrl("https://www.google.com:8080/test");
        Page page = new Page("https://www.google.com", "title", Collections.singletonList("https://www.google.com:8080/test"));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void spawnsCrawlRequestForLinksToSubDomainIfNotVisited() throws Exception {
        String url = "https://google.com";
        String link = "https://www.google.com/test";
        Page page = new Page(url, "title", Collections.singletonList(link));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(url, link)));
    }

    @Test
    public void spawnsCrawlRequestForLinksToDifferentProtocolIfNotVisited() throws Exception {
        String url = "https://www.google.com";
        String link = "http://www.google.com/test";
        Page page = new Page(url, "title", Collections.singletonList(link));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(url, link)));
    }

    @Test
    public void spawnsCrawlRequestForLinksToDifferentPortIfNotVisited() throws Exception {
        String url = "https://www.google.com";
        String link = "https://www.google.com:8080/test";
        Page page = new Page(url, "title", Collections.singletonList(link));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(url, link)));
    }
}
