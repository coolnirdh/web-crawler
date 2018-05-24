package org.nirdh.apps.webcrawler.components;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nirdh.apps.webcrawler.components.storage.PageRepository;
import org.nirdh.apps.webcrawler.domain.CrawlRequest;
import org.nirdh.apps.webcrawler.domain.Page;

import java.util.Arrays;
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
    public void marksLinksAsScheduledForCrawl() throws Exception {
        String link = "https://www.google.com/test";
        crawlFrontier.spawnRequests(new Page("https://www.google.com", "title", Collections.singletonList(link)));
        verify(pageRepository).markAsScheduledForCrawl(link);
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
    public void doesNotSpawnCrawlRequestForLinksStartingWithMailTo() throws Exception {
        Page page = new Page("https://www.google.com", "title", Collections.singletonList("mailto:me@google.com"));
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
    public void doesNotSpawnCrawlRequestForLinksAlreadyCrawledOrScheduledForCrawl() throws Exception {
        doReturn(true).when(pageRepository).isCrawledOrScheduledForCrawl("https://www.google.com:8080/test");
        Page page = new Page("https://www.google.com", "title", Collections.singletonList("https://www.google.com:8080/test"));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void spawnsCrawlRequestForLinksToSubDomainIfNotCrawledOrScheduledForCrawl() throws Exception {
        String url = "https://google.com";
        String link = "https://www.google.com/test";
        Page page = new Page(url, "title", Collections.singletonList(link));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(url, link)));
    }

    @Test
    public void spawnsCrawlRequestForLinksToDifferentProtocolIfNotCrawledOrScheduledForCrawl() throws Exception {
        String url = "https://www.google.com";
        String link = "http://www.google.com/test";
        Page page = new Page(url, "title", Collections.singletonList(link));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(url, link)));
    }

    @Test
    public void spawnsCrawlRequestForLinksToDifferentPortIfNotCrawledOrScheduledForCrawl() throws Exception {
        String url = "https://www.google.com";
        String link = "https://www.google.com:8080/test";
        Page page = new Page(url, "title", Collections.singletonList(link));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(url, link)));
    }

    @Test
    public void crawlRequestsIgnorePartAfterFragment() throws Exception {
        String url = "https://www.google.com";
        String link = "https://www.google.com:8080/test#fragment";
        Page page = new Page(url, "title", Collections.singletonList(link));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(url, "https://www.google.com:8080/test")));
    }

    @Test
    public void spawnsOnlyOneCrawlRequestEvenIfPagePointsToSamePageWithDifferentFragments() throws Exception {
        String url = "https://www.google.com";
        String link1 = "https://www.google.com:8080/test#fragment1";
        String link2 = "https://www.google.com:8080/test#fragment2";
        Page page = new Page(url, "title", Arrays.asList(link1, link2));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(url, "https://www.google.com:8080/test")));
    }
}
