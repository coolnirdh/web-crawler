package org.nirdh.apps.webcrawler.components.storage;

import org.junit.Before;
import org.junit.Test;
import org.nirdh.apps.webcrawler.domain.Page;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
        Page page = new Page(url, "Google");
        pageRepository.store(page);
        assertThat(pageRepository.findByUrl(url), is(page));
    }

    @Test
    public void retrievingStoredUrlsGivesPageWithSameUrl() throws Exception {
        String url = "https://www.google.com";
        pageRepository.store(url);
        Page page = new Page(url, null);
        assertThat(pageRepository.findByUrl(url), is(page));
    }

    @Test
    public void retrievingByUrlThatWasNeverStoredReturnsNull() throws Exception {
        assertThat(pageRepository.findByUrl("https://www.google.com"), is(nullValue()));
    }

    @Test
    public void newlyStoredPageOverritesValueOfOlderPageWithSameUrl() throws Exception {
        String url = "https://www.google.com";
        Page olderPage = new Page(url, "Old");
        pageRepository.store(olderPage);
        assertThat(pageRepository.findByUrl("https://www.google.com"), is(olderPage));

        Page newerPage = new Page(url, "New");
        pageRepository.store(newerPage);
        assertThat(pageRepository.findByUrl("https://www.google.com"), is(newerPage));
    }

    @Test
    public void newlyStoredUrlOverritesValueOfOlderPageWithSameUrl() throws Exception {
        String url = "https://www.google.com";
        Page olderPage = new Page(url, "Old");
        pageRepository.store(olderPage);
        assertThat(pageRepository.findByUrl(url), is(olderPage));

        pageRepository.store(url);
        assertThat(pageRepository.findByUrl(url), is(new Page(url, null)));
    }

    @Test
    public void doesNotContainUrlThatIsNotStored() throws Exception {
        String url = "https://www.google.com";
        assertThat(pageRepository.contains(url), is(false));
    }

    @Test
    public void containsUrlOfStoredPage() throws Exception {
        String url = "https://www.google.com";
        Page page = new Page(url, "Google");
        pageRepository.store(page);
        assertThat(pageRepository.contains(url), is(true));
    }

    @Test
    public void containsUrlOfStoredLink() throws Exception {
        String url = "https://www.google.com";
        pageRepository.store(url);
        assertThat(pageRepository.contains(url), is(true));
    }
}
