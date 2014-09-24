package org.tbot;

import org.tbot.parse.ContentType;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.tbot.entity.FetchUrl;
import org.tbot.fetch.FetchRunner;
import org.tbot.fetch.PageMetadata;
import org.tbot.objects.Domain;
import org.tbot.objects.DomainMetadata;
import org.tbot.parse.ParserFactory;
import org.tbot.repository.QueueRepository;
import org.tbot.repository.impl.BlockingMemoryRepository;
import org.tbot.repository.impl.MemorySetRepository;

public class Bot {

    public void start() throws MalformedURLException {
        int threads = 10;
        ExecutorService exec = Executors.newCachedThreadPool();
        ParserFactory parserFactory = ParserFactory.build();
        QueueRepository<FetchUrl> urlQueue = new BlockingMemoryRepository<>(2000);
        QueueRepository<Domain> domainQueue = new MemorySetRepository<>(10000);
        Domain d1 = Domain.parse("http://en.wikipedia.org/", new DomainMetadata());
        Domain d2 = Domain.parse("http://apache.org/", new DomainMetadata());
        urlQueue.insert(new FetchUrl("http://en.wikipedia.org/", d1, new PageMetadata(0)));
        urlQueue.insert(new FetchUrl("http://apache.org/", d2, new PageMetadata(0)));
        d1.getMetadata().acquireUrl();
        d2.getMetadata().acquireUrl();
        for (int i = 0; i < threads; i++) {
            Runnable fetcher = new FetchRunner(urlQueue, domainQueue, parserFactory, i + "");
            exec.execute(fetcher);
        }

        CompletionService<Integer> t;
    }

    public static void main(String[] args) throws InterruptedException, MalformedURLException, IOException, Exception {
        new Bot().start();
    }

    public static void loadProperties(String file) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        for (Object prop : props.keySet()) {
            System.getProperties().setProperty(prop.toString(), props.getProperty(prop.toString()));
        }
    }

    private static ContentType parseType(String type) {
        String pTypeString = type.contains("/") ? type.split("/")[1] : type;
        pTypeString = pTypeString.contains(";") ? pTypeString.split(";")[0] : pTypeString;
        for (ContentType pType : ContentType.values()) {
            if (pType.toString().equals(pTypeString.toLowerCase().trim())) {
                return pType;
            }
        }
        return ContentType.Unknown;
    }

    public static int maxNextDepth() {
        return Integer.valueOf(System.getProperty("twinklebot.nextDepthUrls"));
    }


}
