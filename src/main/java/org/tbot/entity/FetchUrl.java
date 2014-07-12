package org.tbot.entity;

import org.tbot.objects.ObjectToCrawl;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class FetchUrl extends CrawlEntity {

    public FetchUrl(URL url, ObjectToCrawl head) {
        super(head, url);
    }

    public FetchUrl(String url, ObjectToCrawl head) throws MalformedURLException {
        super(head, new URL(url));
    }

    public void setProcessUrl(URL processUrl) {
        this.processUrl = processUrl;
    }

}
