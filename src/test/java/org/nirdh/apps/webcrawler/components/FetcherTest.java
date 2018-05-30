package org.nirdh.apps.webcrawler.components;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.nirdh.apps.webcrawler.domain.CachedResponse;
import org.nirdh.apps.webcrawler.exceptions.FetcherException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Nirdh on 22-05-2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FetcherTest {

    @Autowired
    private Fetcher fetcher;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void doesNotFetchFromNullUrl() throws Exception {
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString("[null]"));
        fetcher.fetchFrom(null);
    }

    @Test
    public void doesNotFetchFromEmptyUrl() throws Exception {
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString("[]"));
        fetcher.fetchFrom("");
    }

    @Test
    public void doesNotFetchFromMalformedUrl() throws Exception {
        String urlSupplied = "blah";
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString(urlSupplied));
        fetcher.fetchFrom(urlSupplied);
    }

    @Test
    public void doesNotFetchFromUrlWithInvalidDNS() throws Exception {
        String urlSupplied = "http://subdomain.google.com:8080/path";
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString(urlSupplied));
        fetcher.fetchFrom(urlSupplied);
    }

    @Test
    public void doesNotFetchFromUrlPointingToMedia() throws Exception {
        String urlSupplied = "https://www.google.co.in/images/branding/product/ico/googleg_lodp.ico";
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString(urlSupplied));
        fetcher.fetchFrom(urlSupplied);
    }

    @Test
    public void statusCodeIs404InCachedResponseFetchedFromNonExistentUrl() throws Exception {
        CachedResponse cachedResponse = fetcher.fetchFrom("https://google.com/nonexistentpage");
        assertThat(cachedResponse.getStatusCode(), comparesEqualTo(404));
    }

    @Test
    public void statusCodeIs200InCachedResponseFetchedFromValidUrl() throws Exception {
        CachedResponse cachedResponse = fetcher.fetchFrom("https://google.com/");
        assertThat(cachedResponse.getStatusCode(), comparesEqualTo(200));
    }

    @Test
    public void fetchesFromUrlWithUntrustedCertificate() throws Exception {
        String urlSupplied = "https://community.monzo.com";
        CachedResponse cachedResponse = fetcher.fetchFrom(urlSupplied);
        assertThat(cachedResponse.getStatusCode(), comparesEqualTo(200));
    }

    @Test
    public void fetchesFromHttpUrl() throws Exception {
        String urlSupplied = "http://google.com";
        CachedResponse cachedResponse = fetcher.fetchFrom(urlSupplied);
        assertThat(cachedResponse.getStatusCode(), comparesEqualTo(200));
    }

    @Test
    public void urlInCachedResponseIsSameAsUrlSuppliedToFetcher() throws Exception {
        String suppliedUrl = "https://google.com/";
        CachedResponse cachedResponse = fetcher.fetchFrom(suppliedUrl);
        assertThat(cachedResponse.getUrl(), is(suppliedUrl));
    }

    @Test
    public void contentInCachedResponseRepresentsHtmlFetchedFromUrl() throws Exception {
        CachedResponse cachedResponse = fetcher.fetchFrom("https://google.com/nonexistentpage");
        assertThat(cachedResponse.getContent(), containsString("<title>"));
    }
}