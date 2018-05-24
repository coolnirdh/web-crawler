package org.nirdh.apps.webcrawler.components.storage;

import org.junit.Before;
import org.junit.Test;
import org.nirdh.apps.webcrawler.domain.Page;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by Nirdh on 24-05-2018.
 */
public class PageRepositoryTest {

    private PageRepository pageRepository;

    @Before
    public void setUp() throws Exception {
        pageRepository = new PageRepository();
    }

    @Test
    public void storedPageCanBeRetrievedByUrl() throws Exception {
        String url = "https://www.google.com";
        Page page = new Page(url, "", Collections.emptyList());
        pageRepository.store(page);
        assertThat(pageRepository.findByUrl(url), is(page));
    }

    @Test
    public void retrievingByUrlThatIsntStoredReturnsNull() throws Exception {
        assertThat(pageRepository.findByUrl("https://www.google.com"), is(nullValue()));
    }

    @Test
    public void newlyStoredPageOverritesValueOfOlderPageWithSameUrl() throws Exception {
        String url = "https://www.google.com";
        Page olderPage = new Page(url, "Old", Collections.emptyList());
        pageRepository.store(olderPage);
        assertThat(pageRepository.findByUrl("https://www.google.com"), is(olderPage));

        Page newerPage = new Page(url, "New", Collections.emptyList());
        pageRepository.store(newerPage);
        assertThat(pageRepository.findByUrl("https://www.google.com"), is(newerPage));
    }

    @Test
    public void markingPageAsScheduledForCrawlRemovesPreviousPageStored() throws Exception {
        String url = "https://www.google.com";
        Page olderPage = new Page(url, "Old", Collections.emptyList());
        pageRepository.store(olderPage);
        assertThat(pageRepository.findByUrl(url), is(olderPage));

        pageRepository.markAsScheduledForCrawl(url);
        assertThat(pageRepository.findByUrl(url), not(olderPage));
        assertThat(pageRepository.isCrawledOrScheduledForCrawl(url), is(true));
    }

    @Test
    public void aUrlNotKnownToPageRepositoryIsNotCrawledOrScheduledForCrawl() throws Exception {
        String url = "https://www.google.com";
        assertThat(pageRepository.isCrawledOrScheduledForCrawl(url), is(false));
    }
}
