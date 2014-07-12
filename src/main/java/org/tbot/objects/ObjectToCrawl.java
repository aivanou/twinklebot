package org.tbot.objects;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class ObjectToCrawl implements Serializable {

    private String url;
    private final int maxDepth;
    private int maxCrawlPages;
    private int contentHash;
    private final CrawlObjectType type;
    private final AtomicBoolean stateCreationFinished;
    private final float defaultBoost;

    public ObjectToCrawl(String url, int maxDepth, int maxCrawlPages,
            int contentHash, CrawlObjectType type, float defaultBoost) {
        this.stateCreationFinished = new AtomicBoolean(false);
        this.url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        this.maxDepth = maxDepth;
        this.maxCrawlPages = maxCrawlPages;
        this.contentHash = contentHash;
        this.type = type;
        this.defaultBoost = defaultBoost;
    }

    public float getDefaultBoost() {
        return defaultBoost;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMaxCrawlPages(int maxCrawlPages) {
        this.maxCrawlPages = maxCrawlPages;
    }

    public void setContentHash(int contentHash) {
        this.contentHash = contentHash;
    }

    public boolean getStateCreationFinished() {
        return stateCreationFinished.get();
    }

    public void setStateCreationFinished(boolean state) {
        this.stateCreationFinished.set(state);
    }

    public int getContentHash() {
        return contentHash;
    }

    public int getMaxCrawlPages() {
        return maxCrawlPages;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public CrawlObjectType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ObjectToCrawl other = (ObjectToCrawl) obj;
        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url)) {
            return false;
        }
        if (this.maxDepth != other.maxDepth) {
            return false;
        }
        if (this.maxCrawlPages != other.maxCrawlPages) {
            return false;
        }
        if (this.contentHash != other.contentHash) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.url != null ? this.url.hashCode() : 0);
        hash = 83 * hash + this.maxDepth;
        hash = 83 * hash + this.maxCrawlPages;
        hash = 83 * hash + this.contentHash;
        hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}
