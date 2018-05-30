package org.nirdh.apps.webcrawler.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Created by Nirdh on 29-05-2018.
 */
public class LinkTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void urlMustNotBeNull() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("Url must not be null"));
        new Link(null, null);
    }

    @Test
    public void textIsCapturedInExceptionForNullUrl() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("[text]"));
        new Link(null, "text");
    }

    @Test
    public void urlMustBeAbsolute() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("Malformed Url [/relative]"));
        new Link("/relative", null);
    }

    @Test
    public void textIsCapturedInExceptionForMalformedUrl() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("[text]"));
        new Link("/relative", "text");
    }

    @Test
    public void trailingQuestionMarksAreIgnoredInUrl() throws Exception {
        String urlWithoutTrailingQuestionMarks = "https://www.google.com:80/login";
        Link link = new Link(urlWithoutTrailingQuestionMarks + "???", null);
        assertThat(link.getUrl(), is(urlWithoutTrailingQuestionMarks));
    }

    @Test
    public void trailingSlashesAreIgnoredInUrl() throws Exception {
        String urlWithoutTrailingSlashes = "https://www.google.com:80/login";
        Link link = new Link(urlWithoutTrailingSlashes + "///", null);
        assertThat(link.getUrl(), is(urlWithoutTrailingSlashes));
    }

    @Test
    public void trailingHashesAreIgnoredInUrl() throws Exception {
        String urlWithoutTrailingHashes = "https://www.google.com:80/login";
        Link link = new Link(urlWithoutTrailingHashes + "###", null);
        assertThat(link.getUrl(), is(urlWithoutTrailingHashes));
    }

    @Test
    public void trailingQuestionMarksSlashesAndHashesAreIgnoredInUrl() throws Exception {
        String urlWithTrailingQuestionMarksSlashesAndHashes = "https://www.google.com:80/login";
        Link link = new Link(urlWithTrailingQuestionMarksSlashesAndHashes + "?/##/??#//#?", null);
        assertThat(link.getUrl(), is(urlWithTrailingQuestionMarksSlashesAndHashes));
    }

    @Test
    public void questionMarksSlashesAndHashesThatAreNotTrailingArePreserved() throws Exception {
        String urlWithNonTrailingQuestionMarksSlashesAndHashes = "https://www.google.com:80/login?id=#321";
        Link link = new Link(urlWithNonTrailingQuestionMarksSlashesAndHashes + "?/##/??#//#?", null);
        assertThat(link.getUrl(), is(urlWithNonTrailingQuestionMarksSlashesAndHashes));
    }

    @Test
    public void urlWithoutFragmentIsUrlIfItDoesNotContainHash() throws Exception {
        String urlWithoutHash = "https://www.google.com:80/document";
        Link link = new Link(urlWithoutHash, null);
        assertThat(link.getUrlWithoutFragment(), is(urlWithoutHash));
    }

    @Test
    public void urlWithoutFragmentIsPartOfUrlBeforeHash() throws Exception {
        String urlBeforeHash = "https://www.google.com:80/document";
        Link link = new Link(urlBeforeHash + "#jumpToHeader", null);
        assertThat(link.getUrlWithoutFragment(), is(urlBeforeHash));
    }

    @Test
    public void aLinkBelongsToADomainWhichExactlyMatchesItsHostName() throws Exception {
        Link link = new Link("https://www.google.com:80/login", null);
        assertThat(link.belongsTo("www.google.com"), is(true));
    }

    @Test
    public void aLinkBelongsToADomainWhichIsParentDomainOfItsHostName() throws Exception {
        Link link = new Link("https://www.google.com:80/login", null);
        assertThat(link.belongsTo("google.com"), is(true));
    }

    @Test
    public void aMailToLinkDoesNotBelongToAnyDomain() throws Exception {
        Link link = new Link("mailto:me@google.com", null);
        assertThat(link.belongsTo("google.com"), is(false));
    }

    @Test
    public void aLinkDoesNotBelongToADomainWhichIsSubDomainOfItsHostName() throws Exception {
        Link link = new Link("https://google.com:80/login", null);
        assertThat(link.belongsTo("www.google.com"), is(false));
    }

    @Test
    public void aLinkDoesNotBelongToADomainWhichIsSiblingDomainOfItsHostName() throws Exception {
        Link link = new Link("https://www.google.com:80/login", null);
        assertThat(link.belongsTo("mail.google.com"), is(false));
    }

    @Test
    public void aLinkDoesNotBelongToADomainWhichNotRelatedToItsHostName() throws Exception {
        Link link = new Link("https://www.google.com:80/login", null);
        assertThat(link.belongsTo("www.apache.org"), is(false));
    }
}
