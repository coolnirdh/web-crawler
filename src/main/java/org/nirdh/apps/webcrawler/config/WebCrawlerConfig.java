package org.nirdh.apps.webcrawler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Creates injectable beans for configuration of the crawler behavior, like concurrency.
 */
@Configuration
public class WebCrawlerConfig {

    @Value("${concurrency.corePoolSize}")
    private int corePoolSize;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        return threadPoolTaskExecutor;
    }
}
