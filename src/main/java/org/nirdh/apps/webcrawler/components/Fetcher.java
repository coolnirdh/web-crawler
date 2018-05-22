package org.nirdh.apps.webcrawler.components;

import org.jsoup.Jsoup;
import org.nirdh.apps.webcrawler.exceptions.FetcherException;

/**
 * Returns a String representation of HTML available at input URL.
 */
public class Fetcher {
    public String fetchPageContentFrom(String url) throws FetcherException {
        try {
            return Jsoup.connect(url).get().html();
        } catch (Throwable t) {
            throw new FetcherException(url, t);
        }
    }
}
