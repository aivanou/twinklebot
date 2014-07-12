package org.tbot.objects;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class DomainMetadata {

    private final Set<Integer> crawledUrls;
    private AtomicInteger busyUrls;
    private DomainState domainState;

    private final Object lock;

    public DomainMetadata() {
        this.crawledUrls = java.util.Collections.synchronizedSet(new HashSet<Integer>());
        this.domainState = DomainState.NotCrawled;
        this.lock = new Object();
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

    public void changeDomainState(DomainState newState) {
        synchronized (lock) {
            domainState = newState;
        }
    }

}
