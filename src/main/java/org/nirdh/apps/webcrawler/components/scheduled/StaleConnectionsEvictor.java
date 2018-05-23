package org.nirdh.apps.webcrawler.components.scheduled;

import org.apache.http.conn.HttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Evicts expired and idle connections from HttpClientConnectionManager
 * as required by documentation at https://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/connmgmt.html#d5e418
 */
@Component
@EnableScheduling
public class StaleConnectionsEvictor {

    private final HttpClientConnectionManager httpClientConnectionManager;

    @Value("${http.connections.idleTimeOutInSeconds}")
    private int idleTimeOutInSeconds;

    @Autowired
    public StaleConnectionsEvictor(HttpClientConnectionManager httpClientConnectionManager) {
        this.httpClientConnectionManager = httpClientConnectionManager;
    }

    @Scheduled(fixedDelayString = "${http.connections.evictionDelayInMillis}")
    public void evict() {
        httpClientConnectionManager.closeExpiredConnections();
        httpClientConnectionManager.closeIdleConnections(idleTimeOutInSeconds, TimeUnit.SECONDS);
    }
}
