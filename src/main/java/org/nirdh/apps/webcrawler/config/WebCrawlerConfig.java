package org.nirdh.apps.webcrawler.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nirdh.apps.webcrawler.components.storage.LocalPageRepository;
import org.nirdh.apps.webcrawler.components.storage.PageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Creates injectable beans for configuration of the crawler behavior, like concurrency.
 */
@Configuration
public class WebCrawlerConfig {
    private static final Log logger = LogFactory.getLog(WebCrawlerConfig.class);

    @Value("${concurrency.corePoolSize}")
    private int corePoolSize;

    @Bean
    public PageRepository pageRepository() {
        return new LocalPageRepository();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        return threadPoolTaskExecutor;
    }

    @Bean
    @Profile("default")
    public CommandLineRunner schedulingRunner(MessageChannel discoveredUrls) {
        return new CommandLineRunner() {
            public void run(String... args) throws Exception {
                if (args.length == 0) {
                    logger.error("Pass seed URL as command line argument");
                    System.exit(1);
                }
                discoveredUrls.send(new GenericMessage<Object>(args[0]));
            }
        };
    }
}
