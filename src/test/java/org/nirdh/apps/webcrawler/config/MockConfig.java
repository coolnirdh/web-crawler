package org.nirdh.apps.webcrawler.config;

import org.apache.http.conn.HttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

/**
 * Created by Nirdh on 24-05-2018.
 */
@Configuration
@Profile("mock")
public class MockConfig {

    @Bean
    public HttpClientConnectionManager httpClientConnectionManager() {
        return mock(HttpClientConnectionManager.class);
    }
}
