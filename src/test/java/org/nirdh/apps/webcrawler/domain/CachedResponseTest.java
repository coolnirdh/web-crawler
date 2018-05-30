package org.nirdh.apps.webcrawler.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.containsString;

/**
 * Created by Nirdh on 29-05-2018.
 */
public class CachedResponseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void urlMustNotBeNull() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("url must not be null"));
        new CachedResponse(null, 200, "");
    }

    @Test
    public void statusCodeMustNotBeLesserThan100() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("statusCode must be between "));
        new CachedResponse("", 99, "");
    }

    @Test
    public void statusCodeMayBe100() throws Exception {
        new CachedResponse("", 100, "");
    }

    @Test
    public void statusCodeMustNotBeGreaterThan599() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("statusCode must be between "));
        new CachedResponse("", 600, "");
    }

    @Test
    public void statusCodeMayBe599() throws Exception {
        new CachedResponse("", 599, "");
    }

    @Test
    public void valueOfInvalidStatusCodeIsCaptured() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("but was -1"));
        new CachedResponse("", -1, "");
    }

    @Test
    public void contentMustNotBeNull() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("content must not be null"));
        new CachedResponse("", 200, null);
    }
}
