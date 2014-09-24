package org.tbot.entity;

import java.net.MalformedURLException;
import java.net.URL;
import org.tbot.fetch.PageMetadata;
import org.tbot.objects.Domain;

/**
 *
 */
public class FetchUrl extends CrawlEntity {

    private final PageMetadata pageMetadata;

    public FetchUrl(URL url, Domain head, PageMetadata pageMetadata) {
        super(head, url);
        this.pageMetadata = pageMetadata;
    }

    public FetchUrl(String url, Domain head, PageMetadata pageMetadata) throws MalformedURLException {
        super(head, new URL(url));
        this.pageMetadata = pageMetadata;
    }

    public void setProcessUrl(URL processUrl) {
        this.processUrl = processUrl;
    }

    public PageMetadata getPageMetadata() {
        return pageMetadata;
    }

}
