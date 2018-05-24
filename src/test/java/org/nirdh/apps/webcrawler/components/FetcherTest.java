package org.nirdh.apps.webcrawler.components;

import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nirdh.apps.webcrawler.domain.CachedResponse;
import org.nirdh.apps.webcrawler.exceptions.FetcherException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Nirdh on 22-05-2018.
 */
public class FetcherTest {

    private Fetcher fetcher;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        fetcher = new Fetcher(HttpClients.createDefault());
    }

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