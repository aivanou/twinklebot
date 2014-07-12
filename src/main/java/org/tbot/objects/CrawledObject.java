package org.tbot.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class CrawledObject implements Serializable {

    private final String startURL;
    private final CrawlObjectType type;
    private final Collection<Link> links;
    private final int responseCode;
    private final int contentHash;

    public CrawledObject(String startURL, CrawlObjectType type,
            Collection<Link> links, int responseCode, int contentHash) {
        this.startURL = startURL;
        this.type = type;
        this.links = links;
        this.responseCode = responseCode;
        this.contentHash = contentHash;
    }

    public CrawledObject(String url, int code, CrawlObjectType type) {
        this.type = type;
        this.responseCode = code;
        this.startURL = url;
        this.links = new ArrayList<Link>();
        this.contentHash = -1;
    }

    public String getStartURL() {
        return startURL;
    }

    public Collection<Link> getLinks() {
        return links;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getContentHash() {
        return contentHash;
    }

    public CrawlObjectType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CrawledObject other = (CrawledObject) obj;
        if ((this.startURL == null) ? (other.startURL != null) : !this.startURL.equals(other.startURL)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.links != other.links && (this.links == null || !this.links.equals(other.links))) {
            return false;
        }
        if (this.responseCode != other.responseCode) {
            return false;
        }
        if (this.contentHash != other.contentHash) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.startURL != null ? this.startURL.hashCode() : 0);
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + (this.links != null ? this.links.hashCode() : 0);
        hash = 67 * hash + this.responseCode;
        hash = 67 * hash + this.contentHash;
        return hash;
    }
}
