package org.tbot.state;

import org.tbot.Bot;
import org.tbot.fetch.ProtocolType;
import org.tbot.objects.Domain;
import org.tbot.objects.Link;
import org.tbot.objects.ObjectToCrawl;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class CrawlState {

    private ObjectToCrawl head;
    private final Set<String> nextDepthUrls;
    private final Set<String> currentDepthUrls;
    private Iterator<String> currIt;
    private AtomicInteger currentDepth;
    private AtomicInteger takenUrls = new AtomicInteger(0);
    private AtomicInteger code = new AtomicInteger(0);
    private final AtomicInteger currentCrawledUrls = new AtomicInteger(0);
//    private RobotRules rules;
    private Set<Domain> inLinks, outLinks;
    private static final Map<String, CrawlState> states = new ConcurrentHashMap<String, CrawlState>();
    private final int maxNextDepthUrls;
    private final int maxLinksStore = 10000;
    private final int maxOutputLinks = 1000;
    private volatile int currentOutLinks = 0;
    private volatile int currentInLinks = 0;
    //TODO: make limit for the outLinks collection

    public CrawlState(ObjectToCrawl object) throws MalformedURLException {
        this.maxNextDepthUrls = Bot.maxNextDepth();
//        this.rules = RobotRulesFactory.getRobotRules(new URL(object.getUrl()));
        this.head = object;
        this.inLinks = new HashSet<Domain>();//ConcurrentSkipListSet<Domain>();
        this.outLinks = new HashSet<Domain>();//ConcurrentSkipListSet<Domain>();

        this.nextDepthUrls = new HashSet<String>();
        this.currentDepthUrls = new HashSet<String>();
        this.currentDepth = new AtomicInteger(0);
    }

    public int getMaxLinksStore() {
        return this.maxLinksStore;
    }

    public int getCode() {
        return code.get();
    }

    public void setCode(int code) {
        this.code.set(code);
    }

    public Set<Domain> getInLinks() {
        return inLinks;
    }

    public Set<Domain> getOutLinks() {
        return outLinks;
    }

    public void setCurrentDepth(int depth) {
        this.currentDepth.set(depth);
    }

    public int getCrawled() {
        return this.currentCrawledUrls.get();
    }

    public static CrawlState initCrawlState(ObjectToCrawl object) throws MalformedURLException {
        CrawlState st = null;
        if (!states.containsKey(object.getUrl())) {
            st = new CrawlState(object);
            states.put(object.getUrl(), st);
        }
        if (st == null) {
            return states.get(object.getUrl());
        }
        return st;
    }

    public synchronized static CrawlState getCrawlState(String name) {
        if (!states.containsKey(name)) {
            return null;
        }
        return states.get(name);
    }

    public int getCurrentDepth() {
        return this.currentDepth.get();
    }

//    public int getCrawled() {
//        return this.currentCrawled.get();
//    }
    public void incDepth() {
        this.currentDepth.incrementAndGet();
    }

    public int getTakenUrls() {
        return this.takenUrls.get();
    }

    public int nextUrlSize() {
        return this.nextDepthUrls.size();
    }

    public int currUrlSize() {
        return this.currentDepthUrls.size();
    }

    public String nextUrl(String id) {
        this.currentCrawledUrls.incrementAndGet();
        synchronized (this.currentDepthUrls) {
            if (this.currIt != null && this.currIt.hasNext()) {
                String url = currIt.next();
                this.takenUrls.incrementAndGet();
                return url;
            }
        }
        if (this.takenUrls.get() == 0) {
            this.finishCurrentDepth();
            this.currentDepth.incrementAndGet();
        } else {
            return "";
        }
        if (this.currIt != null && this.currIt.hasNext()) {
            takenUrls.incrementAndGet();
            String url = currIt.next();
            return url;
        }
        return null;
    }

    public int releaseUrl() {
        if (this.takenUrls.get() > 0) {
            return this.takenUrls.decrementAndGet();
        }
        return 0;
    }

    public void releaseAllUrls() {
        this.takenUrls.set(0);
    }

    public int getNextDepthUrls() {
        return nextDepthUrls.size();
    }

    public void addUrlToNextDepth(String url) {
        String path = null;
        try {
            URL eUrl = new URL(url);
            path = eUrl.getPath();
        } catch (MalformedURLException ex) {
            return;
        }
        synchronized (this.currentDepthUrls) {
            if (this.currentDepthUrls.contains(url)) {
                return;
            }
        }
        synchronized (this.nextDepthUrls) {
            if (this.nextDepthUrls.size() >= this.maxNextDepthUrls) {
                return;
            }
            this.nextDepthUrls.add(url);
        }
    }

    public void finishCurrentDepth() {
        synchronized (this.currentDepthUrls) {
            this.currentDepthUrls.clear();
            this.currentDepthUrls.addAll(this.nextDepthUrls);
            this.currIt = this.currentDepthUrls.iterator();
        }
        synchronized (this.nextDepthUrls) {
            this.nextDepthUrls.clear();
        }
    }

    public ObjectToCrawl getState() {
        return head;
    }

    public boolean validate() {
        if (this.getCurrentDepth() > this.head.getMaxDepth()) {
            return false;
        }
        if (this.currentCrawledUrls.get() > this.head.getMaxCrawlPages()) {
            return false;
        }
        return true;
    }

    public void addInLinks(Collection<Link> inLinks) {
        if (this.currentInLinks >= this.maxOutputLinks) {
            return;
        }
        if (inLinks != null) {
            for (Link link : inLinks) {
                Domain domain = null;
                this.inLinks.add(domain);
                this.currentInLinks += 1;
            }
        }
    }

    public void addOutLinks(Collection<Link> outLinks) {
        if (this.currentOutLinks >= this.maxOutputLinks) {
            return;
        }
        if (outLinks != null) {
            synchronized (this) {
                for (Link link : outLinks) {
                    Domain domain = null;
                    this.outLinks.add(domain);
                    this.currentOutLinks += 1;
                }
            }
        }
    }

    public void clear() {
        this.takenUrls.set(0);
        this.nextDepthUrls.clear();
        this.currentDepthUrls.clear();
        this.currIt = null;
        this.inLinks.clear();
        this.outLinks.clear();
        this.currentDepth.set(0);
    }

    public synchronized static void clear(String site) {
        CrawlState state = states.get(site);
        if (state == null) {
            return;
        }
        state.clear();
        states.remove(site);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CrawlState other = (CrawlState) obj;
        if (this.head != other.head && (this.head == null || !this.head.equals(other.head))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.head != null ? this.head.hashCode() : 0);
        return hash;
    }
}
