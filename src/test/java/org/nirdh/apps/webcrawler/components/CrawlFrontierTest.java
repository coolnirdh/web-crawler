package org.nirdh.apps.webcrawler.components;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nirdh.apps.webcrawler.components.storage.PageRepository;
import org.nirdh.apps.webcrawler.domain.CrawlRequest;
import org.nirdh.apps.webcrawler.domain.Link;
import org.nirdh.apps.webcrawler.domain.Page;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.*;

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
        Page page = new Page("https://www.google.com", "title");
        crawlFrontier.spawnRequests(page);
        verify(pageRepository).store(page);
    }

    @Test
    public void storesUrlsToBeCrawledToRepository() throws Exception {
        String url = "https://www.google.com/test";
        Page page = new Page("https://www.google.com", "title");
        page.add(new Link(url, null));

        crawlFrontier.spawnRequests(page);
        verify(pageRepository).store(url);
    }

    @Test
    public void doesNotStoreUrlsThatNeedNotBeCrawled() throws Exception {
        String url = "mailto:me@google.com";
        Page page = new Page("https://www.google.com", "title");
        page.add(new Link(url, null));
        crawlFrontier.spawnRequests(page);
        verify(pageRepository, never()).store(url);
    }

    @Test
    public void doesNotSpawnCrawlRequestsIfPageHasNoLinks() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void doesNotSpawnCrawlRequestForUrlsStartingWithMailTo() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        page.add(new Link("mailto:me@google.com", null));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void doesNotSpawnCrawlRequestForLinksToParentDomainOfPage() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        page.add(new Link("https://google.com/test", null));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void doesNotSpawnCrawlRequestForLinksToSiblingDomainOfPage() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        page.add(new Link("https://mail.google.com/test", null));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void doesNotSpawnCrawlRequestForLinksOutsidePageDomain() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        page.add(new Link("https://github.com/test", null));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void doesNotSpawnCrawlRequestForUrlsAlreadyCrawledOrScheduledForCrawl() throws Exception {
        String urlAlreadyInRepository = "https://www.google.com:8080/test";
        doReturn(true).when(pageRepository).contains(urlAlreadyInRepository);
        Page page = new Page("https://www.google.com", "title");
        page.add(new Link(urlAlreadyInRepository, null));
        assertThat(crawlFrontier.spawnRequests(page), is(empty()));
    }

    @Test
    public void spawnsCrawlRequestForLinksToSubDomainIfNotCrawledOrScheduledForCrawl() throws Exception {
        String pageUrl = "https://google.com";
        String urlToSubDomainResource = "https://www.google.com/test";
        Page page = new Page(pageUrl, "title");
        page.add(new Link(urlToSubDomainResource, null));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(urlToSubDomainResource, pageUrl)));
    }

    @Test
    public void spawnsCrawlRequestForLinksToDifferentProtocolIfNotCrawledOrScheduledForCrawl() throws Exception {
        String pageUrl = "https://www.google.com";
        String urlToResourceInSameDomainButDifferentProtocol = "http://www.google.com/test";
        Page page = new Page(pageUrl, "title");
        page.add(new Link(urlToResourceInSameDomainButDifferentProtocol, null));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(urlToResourceInSameDomainButDifferentProtocol, pageUrl)));
    }

    @Test
    public void spawnsCrawlRequestForLinksToDifferentPortIfNotCrawledOrScheduledForCrawl() throws Exception {
        String pageUrl = "https://www.google.com";
        String urlToResourceInSameDomainButDifferentPort = "https://www.google.com:8080/test";
        Page page = new Page(pageUrl, "title");
        page.add(new Link(urlToResourceInSameDomainButDifferentPort, null));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(urlToResourceInSameDomainButDifferentPort, pageUrl)));
    }

    @Test
    public void spawnsCrawlRequestForBaseUrlOfLinksWithFragmentsIfNotCrawledOrScheduledForCrawl() throws Exception {
        String pageUrl = "https://www.google.com";
        String urlPartBeforeFragment = "https://www.google.com/test";
        Page page = new Page(pageUrl, "title");
        page.add(new Link(urlPartBeforeFragment + "#fragment", null));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(urlPartBeforeFragment, pageUrl)));
    }

    @Test
    public void spawnsSingleCrawlRequestForMultipleLinksWithSameBaseUrlButDifferentFragments() throws Exception {
        String pageUrl = "https://www.google.com";
        String urlPartBeforeFragment = "https://www.google.com/test";
        Page page = new Page(pageUrl, "title");
        page.add(new Link(urlPartBeforeFragment + "#fragment1", "first"));
        page.add(new Link(urlPartBeforeFragment + "#fragment2", "second"));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(urlPartBeforeFragment, pageUrl)));
    }

    @Test
    public void spawnsSingleCrawlRequestForMultipleLinksWithSameUrl() throws Exception {
        String pageUrl = "https://www.google.com";
        String urlToResource = "https://www.google.com/test";
        Page page = new Page(pageUrl, "title");
        page.add(new Link(urlToResource, "first"));
        page.add(new Link(urlToResource, "second"));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(urlToResource, pageUrl)));
    }

    @Test
    public void spawnsMultipleCrawlRequestsForLinksPointingToDifferentInternalUrls() throws Exception {
        String pageUrl = "https://www.google.com";
        String urlToResource1 = "https://www.google.com/test1";
        String urlToResource2 = "https://www.google.com/test2";
        Page page = new Page(pageUrl, "title");
        page.add(new Link(urlToResource1, "first"));
        page.add(new Link(urlToResource2, "second"));
        assertThat(crawlFrontier.spawnRequests(page), contains(new CrawlRequest(urlToResource1, pageUrl),
                new CrawlRequest(urlToResource2, pageUrl)));
    }
}
