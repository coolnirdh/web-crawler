package org.nirdh.apps.webcrawler.components;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.nirdh.apps.webcrawler.domain.Page;
import org.nirdh.apps.webcrawler.exceptions.FetcherException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Nirdh on 22-05-2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FetcherTest {

    @Autowired
    private Fetcher fetcher;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void doesNotFetchPageFromNullUrl() throws Exception {
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString("[null]"));
        fetcher.fetchPage(null);
    }

    @Test
    public void doesNotFetchPageFromEmptyUrl() throws Exception {
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString("[]"));
        fetcher.fetchPage("");
    }

    @Test
    public void doesNotFetchPageFromMalformedUrl() throws Exception {
        String urlSupplied = "blah";
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString(urlSupplied));
        fetcher.fetchPage(urlSupplied);
    }

    @Test
    public void doesNotFetchPageFromUrlWithInvalidDNS() throws Exception {
        String urlSupplied = "http://subdomain.google.com:8080/path";
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString(urlSupplied));
        fetcher.fetchPage(urlSupplied);
    }

    @Test
    public void doesNotFetchPageFromUrlPointingToMedia() throws Exception {
        String urlSupplied = "https://www.google.co.in/images/branding/product/ico/googleg_lodp.ico";
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString(urlSupplied));
        fetcher.fetchPage(urlSupplied);
    }

    @Test
    public void statusCodeIs404InPageFetchedFromNonExistentUrl() throws Exception {
        Page page = fetcher.fetchPage("https://google.com/nonexistentpage");
        assertThat(page.getStatusCode(), comparesEqualTo(404));
    }

    @Test
    public void statusCodeIs200InPageFetchedFromValidUrl() throws Exception {
        Page page = fetcher.fetchPage("https://google.com/");
        assertThat(page.getStatusCode(), comparesEqualTo(200));
    }

    @Test
    public void urlInPageIsSameAsUrlSuppliedToFetcher() throws Exception {
        String suppliedUrl = "https://google.com/";
        Page page = fetcher.fetchPage(suppliedUrl);
        assertThat(page.getUrl(), is(suppliedUrl));
    }

    @Test
    public void contentInPageRepresentsHtmlFetchedFromUrl() throws Exception {
        Page page = fetcher.fetchPage("https://google.com/nonexistentpage");
        assertThat(page.getContent(), containsString("<title>"));
    }
}