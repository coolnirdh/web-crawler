package org.nirdh.apps.webcrawler.exceptions;

/**
 * Makes sure that we always capture the faulting URL.
 */
public class FetcherException extends Exception {
    public FetcherException(String url, Throwable cause) {
        super(String.format("Unable to fetch from URL: [%s]", url), cause);
    }
}
