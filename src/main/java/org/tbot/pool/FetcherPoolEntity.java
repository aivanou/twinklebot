package org.tbot.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.tbot.entity.FetchUrl;

/**
 *
 */
public class FetcherPoolEntity {

    private final String id;
    private final BlockingQueue<FetchUrl> queue;
    private String processingUrl;

    public FetcherPoolEntity(String id) {
        this.id = id;
        this.queue = new LinkedBlockingQueue<FetchUrl>();
    }

    public FetcherPoolEntity(String id, String url) {
        this.id = id;
        this.queue = new LinkedBlockingQueue<FetchUrl>();
        this.processingUrl = url;
    }

    public FetcherPoolEntity(String id, String url, BlockingQueue<FetchUrl> queue) {
        this.id = id;
        this.queue = queue;
        this.processingUrl = url;
    }

    public void setProcessingUrl(String processingUrl) {
        this.processingUrl = processingUrl;
    }

    public String getId() {
        return id;
    }

    public String getProcessingUrl() {
        return processingUrl;
    }

    public BlockingQueue<FetchUrl> getQueue() {
        return queue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FetcherPoolEntity other = (FetcherPoolEntity) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.queue != null ? this.queue.hashCode() : 0);
        hash = 97 * hash + (this.processingUrl != null ? this.processingUrl.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "FetcherPoolEntity{" + "id=" + id + ", processingUrl=" + processingUrl + '}';
    }
}
