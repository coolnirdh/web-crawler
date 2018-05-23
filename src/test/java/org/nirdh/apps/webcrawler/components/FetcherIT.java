package org.nirdh.apps.webcrawler.components;

import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nirdh.apps.webcrawler.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/**
 * Created by Nirdh on 23-05-2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FetcherIT {

    @Autowired
    private Fetcher fetcher;

    @Autowired
    private TaskExecutor taskExecutor;

    @Test
    public void consecutiveRequestsAreSlowestWithoutConnectionPooling() throws Exception {
        Fetcher slowFetcher = new Fetcher(HttpClients.createDefault());
        List<String> urlsToVisit = getListOfUrlsToVisit();

        long startTimeInMillis = System.currentTimeMillis();
        for (String url : urlsToVisit) {
            slowFetcher.fetchPage(url);
        }
        long timeTakenInMillis = System.currentTimeMillis() - startTimeInMillis;
        System.out.println("Time taken: " + timeTakenInMillis);
        assertThat(timeTakenInMillis, is(lessThanOrEqualTo(7000L)));
    }

    @Test
    public void consecutiveRequestsPerformFasterDueToConnectionPooling() throws Exception {
        List<String> urlsToVisit = getListOfUrlsToVisit();
        long startTimeInMillis = System.currentTimeMillis();
        for (String url : urlsToVisit) {
            fetcher.fetchPage(url);
        }
        long timeTakenInMillis = System.currentTimeMillis() - startTimeInMillis;
        System.out.println("Time taken: " + timeTakenInMillis);
        assertThat(timeTakenInMillis, is(lessThanOrEqualTo(6000L)));
    }

    @Test
    public void consecutiveRequestsPerformFastestWithConnectionPoolingAndThreading() throws Exception {
        List<String> urlsToVisit = getListOfUrlsToVisit();
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) this.taskExecutor;

        long startTimeInMillis = System.currentTimeMillis();
        List<Future<Page>> futures = new LinkedList<>();
        for (String url : urlsToVisit) {
            futures.add(taskExecutor.submit(() -> fetcher.fetchPage(url)));
        }

        for (Future<Page> future : futures) {
            future.get();
        }
        long timeTakenInMillis = System.currentTimeMillis() - startTimeInMillis;
        System.out.println("Time taken: " + timeTakenInMillis);
        assertThat(timeTakenInMillis, is(lessThanOrEqualTo(2000L)));
    }

    private List<String> getListOfUrlsToVisit() {
        return Arrays.asList("https://google.com/",
                "https://www.google.com/search?q=test",
                "https://www.google.com/search?q=kafka",
                "https://www.google.com/search?q=spring",
                "https://www.google.com/search?q=jsoup",
                "https://www.google.com/search?q=httpclient",
                "https://www.google.com/search?q=cassandra",
                "https://www.google.com/search?q=redis",
                "https://www.google.com/search?q=mongo",
                "https://www.google.com/search?q=maven"
        );
    }
}