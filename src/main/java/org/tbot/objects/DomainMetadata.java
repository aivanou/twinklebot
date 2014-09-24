package org.tbot.objects;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class DomainMetadata {

    private final Set<Integer> crawledUrls;
    private final AtomicInteger crawledUrlsAmount;
    private final AtomicInteger busyUrls;
    private DomainState domainState;
    private final AtomicInteger depth;
    private final int maxDepth = 4;
    private final int maxPages = 10;

    private final Object lock;

    public DomainMetadata() {
        this.crawledUrls = java.util.Collections.synchronizedSet(new HashSet<Integer>());
        this.domainState = DomainState.NotCrawled;
        this.lock = new Object();
        this.busyUrls = new AtomicInteger(0);
        this.depth = new AtomicInteger(0);
        this.crawledUrlsAmount = new AtomicInteger(0);
    }

    public void incDepth() {
        depth.incrementAndGet();
    }

    public int getDepth() {
        return depth.get();
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void acquireUrl() {
        busyUrls.incrementAndGet();
    }

    public void releaseUrl() {
        busyUrls.decrementAndGet();
    }

    public int getBusyUrlsAmount() {
        return busyUrls.get();
    }

    public DomainState getDomainState() {
        return domainState;
    }

    public boolean isPageCrawled(String pageUrl) {
        return crawledUrls.contains(pageUrl.hashCode());
    }

    public void crawlPage(String pageUrl) {
        if (crawledUrls.add(pageUrl.hashCode())) {
            crawledUrlsAmount.incrementAndGet();
        }
    }

    public void changeDomainState(DomainState newState) {
        synchronized (lock) {
            domainState = newState;
        }
    }

    public int getCrawledUrlsSize() {
        return crawledUrlsAmount.get();
    }

}
