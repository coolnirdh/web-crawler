package org.nirdh.apps.webcrawler.components;

import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nirdh.apps.webcrawler.exceptions.FetcherException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
    private ApplicationContext applicationContext;

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
        List<Thread> threads = new LinkedList<>();
        for (String url : urlsToVisit) {
            threads.add(new FetcherThread(applicationContext.getBean(Fetcher.class), url));
        }

        long startTimeInMillis = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
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

class FetcherThread extends Thread {

    private Fetcher fetcher;
    private final String url;

    FetcherThread(Fetcher fetcher, String url) {
        this.fetcher = fetcher;
        this.url = url;
    }

    public void run(){
        try {
            fetcher.fetchPage(url);
        } catch (FetcherException e) {
            e.printStackTrace();
        }
    }
}