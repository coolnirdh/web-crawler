package org.nirdh.apps.webcrawler.components;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nirdh.apps.webcrawler.exceptions.FetcherException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;

/**
 * Created by Nirdh on 22-05-2018.
 */
public class FetcherTest {

    private Fetcher fetcher;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        fetcher = new Fetcher();
    }

    @Test
    public void doesNotFetchFromNullUrl() throws Exception {
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString("[null]"));
        fetcher.fetchPageContentFrom(null);
    }

    @Test
    public void doesNotFetchFromEmptyUrl() throws Exception {
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString("[]"));
        fetcher.fetchPageContentFrom("");
    }

    @Test
    public void doesNotFetchFromMalformedUrl() throws Exception {
        String urlSupplied = "blah";
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString(urlSupplied));
        fetcher.fetchPageContentFrom(urlSupplied);
    }

    @Test
    public void doesNotFetchFromUrlWithInvalidDNS() throws Exception {
        String urlSupplied = "http://subdomain.google.com:8080/path";
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString(urlSupplied));
        fetcher.fetchPageContentFrom(urlSupplied);
    }

    @Test
    public void doesNotFetchFromNonExistentPage() throws Exception {
        String urlSupplied = "https://google.com/nonexistentpage";
        thrown.expect(FetcherException.class);
        thrown.expectMessage(containsString(urlSupplied));
        fetcher.fetchPageContentFrom("https://google.com/nonexistentpage");
    }

    @Test
    public void fetchesFromValidUrl() throws Exception {
        assertThat(fetcher.fetchPageContentFrom("https://google.com/"), endsWith("</html>"));
    }
}
