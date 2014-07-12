package org.tbot.entity;

import org.tbot.objects.ObjectToCrawl;
import java.net.URL;

/**
 *
 */
public abstract class CrawlEntity {

    protected ObjectToCrawl head;
    protected URL processUrl;

    public URL getProcessUrl() {
        return processUrl;
    }

    public CrawlEntity(ObjectToCrawl head, URL processUrl) {
        this.head = head;
        this.processUrl = processUrl;
    }

    public ObjectToCrawl getHead() {
        return head;
    }

    public static int getHashCode(String text) {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CrawlEntity other = (CrawlEntity) obj;
        if (this.head != other.head && (this.head == null || !this.head.equals(other.head))) {
            return false;
        }
        if (this.processUrl != other.processUrl && (this.processUrl == null || !this.processUrl.equals(other.processUrl))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.head != null ? this.head.hashCode() : 0);
        hash = 89 * hash + (this.processUrl != null ? this.processUrl.hashCode() : 0);
        return hash;
    }
}
