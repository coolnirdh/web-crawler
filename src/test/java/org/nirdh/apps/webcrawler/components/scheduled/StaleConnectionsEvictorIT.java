package org.nirdh.apps.webcrawler.components.scheduled;

import org.apache.http.conn.HttpClientConnectionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nirdh.apps.webcrawler.config.MockConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Nirdh on 24-05-2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Import(MockConfig.class)
@ActiveProfiles("mock")
public class StaleConnectionsEvictorIT {

    @Autowired
    private HttpClientConnectionManager httpClientConnectionManager;

    @Value("${http.connections.evictionDelayInMillis}")
    private int evictionDelay;

    @Value("${http.connections.idleTimeOutInSeconds}")
    private int idleTimeOutInSeconds;

    @Test
    public void expiredAndIdleConnectionsAreClosedAfterEvictionDelay() throws Exception {
        TimeUnit.MILLISECONDS.sleep(evictionDelay);
        verify(httpClientConnectionManager, atLeastOnce()).closeExpiredConnections();
        verify(httpClientConnectionManager, atLeastOnce()).closeIdleConnections(idleTimeOutInSeconds, TimeUnit.SECONDS);
    }
}
