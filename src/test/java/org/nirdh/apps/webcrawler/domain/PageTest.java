package org.nirdh.apps.webcrawler.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by Nirdh on 29-05-2018.
 */
public class PageTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void urlMustNotBeNull() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("Url must not be null"));
        new Page(null, null);
    }

    @Test
    public void titleIsCapturedInExceptionForNullUrl() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("[title]"));
        new Page(null, "title");
    }

    @Test
    public void linkBeingAddedMustNotBeNull() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("Link must not be null"));
        new Page("https://www.google.com", "title").add(null);
    }

    @Test
    public void linkIsInternalIfItsHostNameIsSameAsThatOfPage() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        Link link = new Link("https://www.google.com/test", null);
        page.add(link);
        assertThat(page.getInternalLinks(), contains(link));
    }

    @Test
    public void linkIsInternalIfItsHostNameIsSameAsThatOfPageEvenIfProtocolIsDifferent() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        Link link = new Link("http://www.google.com/test", null);
        page.add(link);
        assertThat(page.getInternalLinks(), contains(link));
    }

    @Test
    public void linkIsInternalIfItsHostNameIsSameAsThatOfPageEvenIfPortIsDifferent() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        Link link = new Link("https://www.google.com:80/test", null);
        page.add(link);
        assertThat(page.getInternalLinks(), contains(link));
    }

    @Test
    public void linkIsInternalIfItsHostNameIsSubDomainOfPage() throws Exception {
        Page page = new Page("https://google.com", "title");
        Link link = new Link("https://www.google.com/test", null);
        page.add(link);
        assertThat(page.getInternalLinks(), contains(link));
    }

    @Test
    public void linkIsExternalIfItIsAMailToLink() throws Exception {
        Page page = new Page("https://google.com", "title");
        Link link = new Link("mailto:me@google.com", null);
        page.add(link);
        assertThat(page.getExternalLinks(), contains(link));
    }

    @Test
    public void linkIsExternalIfItsHostNameIsParentDomainOfPage() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        Link link = new Link("https://google.com/test", null);
        page.add(link);
        assertThat(page.getExternalLinks(), contains(link));
    }

    @Test
    public void linkIsExternalIfItsHostNameIsSiblingDomainOfPage() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        Link link = new Link("https://mail.google.com/test", null);
        page.add(link);
        assertThat(page.getExternalLinks(), contains(link));
    }

    @Test
    public void linkIsExternalIfItsHostNameIsNotRelatedToDomainOfPage() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        Link link = new Link("https://www.github.com/test", null);
        page.add(link);
        assertThat(page.getExternalLinks(), contains(link));
    }

    @Test
    public void retrievingAllLinksIsSameAsRetrievingInternalAndExternalLinks() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        Link internalLink = new Link("https://www.google.com/test", null);
        Link externalLink = new Link("https://www.github.com/test", null);
        page.add(internalLink);
        page.add(externalLink);
        assertThat(page.getAllLinks(), contains(internalLink, externalLink));
    }

    @Test
    public void internalLinksCannotBeModifiedAfterRetrieval() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        List<Link> internalLinks = page.getInternalLinks();

        thrown.expect(UnsupportedOperationException.class);
        internalLinks.add(new Link("https://www.google.com/test", null));
    }

    @Test
    public void externalLinksCannotBeModifiedAfterRetrieval() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        List<Link> externalLinks = page.getExternalLinks();

        thrown.expect(UnsupportedOperationException.class);
        externalLinks.add(new Link("https://www.github.com/test", null));
    }

    @Test
    public void linksCannotBeModifiedAfterRetrieval() throws Exception {
        Page page = new Page("https://www.google.com", "title");
        List<Link> allLinks = page.getAllLinks();

        thrown.expect(UnsupportedOperationException.class);
        allLinks.add(new Link("https://www.google.com/test", null));
    }
}
