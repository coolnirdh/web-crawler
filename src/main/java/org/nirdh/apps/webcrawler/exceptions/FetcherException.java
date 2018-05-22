package org.nirdh.apps.webcrawler.exceptions;

/**
 * Created by Nirdh on 22-05-2018.
 */
public class FetcherException extends Exception {
    public FetcherException(String url, Throwable cause) {
        super(String.format("Unable to fetch from URL: [%s]", url), cause);
    }
}
