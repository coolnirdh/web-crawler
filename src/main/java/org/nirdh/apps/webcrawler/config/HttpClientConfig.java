package org.nirdh.apps.webcrawler.config;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates injectable beans for HTTP Client connection management.
 */
@Configuration
public class HttpClientConfig {

    @Value("${http.connections.maxTotal}")
    private int httpConnectionsMaxTotal;

    @Value("${http.connections.defaultMaxPerRoute}")
    private int httpConnectionsDefaultMaxPerRoute;

    @Bean(destroyMethod = "shutdown")
    public HttpClientConnectionManager httpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(httpConnectionsMaxTotal);
        connectionManager.setDefaultMaxPerRoute(httpConnectionsDefaultMaxPerRoute);
        return connectionManager;
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient httpClient(@Autowired HttpClientConnectionManager httpClientConnectionManager) {
        return HttpClients.custom()
                .setConnectionManager(httpClientConnectionManager)
                .build();
    }
}
