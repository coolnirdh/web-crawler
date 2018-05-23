package org.nirdh.apps.webcrawler.components;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.nirdh.apps.webcrawler.exceptions.FetcherException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Returns a String representation of HTML available at input URL.
 */
@Component
public class Fetcher {

    private final CloseableHttpClient httpClient;

    @Autowired
    public Fetcher(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String fetchPageContentFrom(String url) throws FetcherException {
        try {
            CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("Expected status code 200, received: " + statusCode);
            }
            return EntityUtils.toString(response.getEntity());
        } catch (Throwable t) {
            throw new FetcherException(url, t);
        }
    }
}
