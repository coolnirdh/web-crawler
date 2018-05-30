package org.nirdh.apps.webcrawler.components;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nirdh.apps.webcrawler.HtmlString;
import org.nirdh.apps.webcrawler.domain.CachedResponse;
import org.nirdh.apps.webcrawler.domain.Link;
import org.nirdh.apps.webcrawler.domain.Page;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Nirdh on 24-05-2018.
 */
public class ParserTest {

    private Parser parser;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        parser = new Parser();
    }

    @Test
    public void cannotParseIfCachedResponseIsNull() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("cachedResponse"));
        parser.parse(null);
    }

    @Test
    public void urlInPageIsSameAsThatInCachedResponse() throws Exception {
        String url = "https://www.google.com";
        Page page = parser.parse(new CachedResponse(url, 200, new HtmlString().build()));
        assertThat(page.getUrl(), is(url));
    }

    @Test
    public void titleInPageIsExtractedFromContentOfCachedResponse() throws Exception {
        String pageTitle = "pageTitle";
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withTitle(pageTitle).build()));
        assertThat(page.getTitle(), is(pageTitle));
    }

    @Test
    public void titleInPageIsEmptyIfContentOfCachedResponseDoesNotContainTitle() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withTitle(null).build()));
        assertThat(page.getTitle(), isEmptyString());
    }

    @Test
    public void relativeLinksAreAddedAsAbsoluteLinksToPage() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withLink("test").build()));
        assertThat(page.getAllLinks(), contains(new Link("https://www.google.com/test", "Click me")));
    }

    @Test
    public void emptyLinksAreNotAddedToPage() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withLink(null).build()));
        assertThat(page.getAllLinks(), is(empty()));
    }

    @Test
    public void encodedLinksAreAddedToPage() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withLink("&#109;&#97;&#x69;&#108;&#116;&#111;&#x3a;&#x68;&#x65;&#108;&#112;&#64;&#x6d;&#111;&#x6e;&#122;&#111;&#46;&#x63;&#111;&#x6d;").build()));
        assertThat(page.getAllLinks(), contains(new Link("mailto:help@monzo.com", "Click me")));
    }

    @Test
    public void linksToDifferentDomainAreAddedToPage() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withLink("http://www.github.com/test").build()));
        assertThat(page.getAllLinks(), contains(new Link("http://www.github.com/test", "Click me")));
    }

    @Test
    public void linksToSubDomainsAreAddedToPage() throws Exception {
        Page page = parser.parse(new CachedResponse("https://google.com", 200, new HtmlString().withLink("https://www.google.com/test").build()));
        assertThat(page.getAllLinks(), contains(new Link("https://www.google.com/test", "Click me")));
    }

    @Test
    public void linksToParentDomainsAreAddedToPage() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withLink("https://google.com/test").build()));
        assertThat(page.getAllLinks(), contains(new Link("https://google.com/test", "Click me")));
    }

    @Test
    public void linksToSiblingDomainsAreAddedToPage() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withLink("https://mail.google.com/test").build()));
        assertThat(page.getAllLinks(), contains(new Link("https://mail.google.com/test", "Click me")));
    }

    @Test
    public void linksToSameDomainAreAddedToPage() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withLink("https://www.google.com/test").build()));
        assertThat(page.getAllLinks(), contains(new Link("https://www.google.com/test", "Click me")));
    }

    @Test
    public void linksToSameDomainButDifferentProtocolAreAddedToPage() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withLink("http://www.google.com/test").build()));
        assertThat(page.getAllLinks(), contains(new Link("http://www.google.com/test", "Click me")));
    }

    @Test
    public void linksToSameDomainButDifferentPortAreAddedToPage() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withLink("https://www.google.com:8080/test").build()));
        assertThat(page.getAllLinks(), contains(new Link("https://www.google.com:8080/test", "Click me")));
    }

    @Test
    public void duplicateLinksAreAddedToPageAsManyTimesAsTheyExist() throws Exception {
        Page page = parser.parse(new CachedResponse("https://www.google.com", 200, new HtmlString().withLink("https://www.google.com/test").withLink("https://www.google.com/test").build()));
        assertThat(page.getAllLinks(), contains(new Link("https://www.google.com/test", "Click me"),
                new Link("https://www.google.com/test", "Click me")));
    }
}
