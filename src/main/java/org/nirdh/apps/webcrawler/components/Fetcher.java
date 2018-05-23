package org.nirdh.apps.webcrawler.components;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.nirdh.apps.webcrawler.domain.Page;
import org.nirdh.apps.webcrawler.exceptions.FetcherException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Leverages a pool of open connections with the host to visit a URL and retrieve the Page.
 */
@Component
public class Fetcher {

    private final CloseableHttpClient httpClient;

    @Autowired
    public Fetcher(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Page fetchPage(String url) throws FetcherException {
        try {
            CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
            HttpEntity entity = response.getEntity();
            String mimeType = ContentType.getOrDefault(entity).getMimeType();
            if (!mimeType.equals("text/html")) {
                throw new RuntimeException("Expected Content-Type text/html, received: " + mimeType);
            }
            int statusCode = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(entity);
            return new Page(url, statusCode, content);
        } catch (Throwable t) {
            throw new FetcherException(url, t);
        }
    }
}
