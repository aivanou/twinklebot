package org.tbot.parse;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.tbot.objects.Link;

/**
 *
 */
public class ParsedPage {

    private final String htmlPage;
    private final Map<String, String> parsedContent;
    private final Collection<Link> inLinks, outLinks;
    private final String url;

    public ParsedPage(String htmlPage, Map<String, String> parsedContent, Collection<Link> inLinks, Collection<Link> outLinks, String url) {
        this.htmlPage = htmlPage;
        this.parsedContent = parsedContent;
        this.inLinks = inLinks;
        this.outLinks = outLinks;
        this.url = url;
    }

    public String getHtmlPage() {
        return htmlPage;
    }

    public Map<String, String> getParsedContent() {
        return parsedContent;
    }

    public Collection<Link> getInLinks() {
        return inLinks;
    }

    public Collection<Link> getOutLinks() {
        return outLinks;
    }

    public String getUrl() {
        return url;
    }

}
