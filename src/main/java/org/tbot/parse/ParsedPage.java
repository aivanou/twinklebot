package org.tbot.parse;

import java.util.Map;
import java.util.Set;
import org.tbot.objects.Domain;
import org.tbot.objects.Link;

/**
 *
 */
public class ParsedPage {

    private final String htmlPage;
    private final Map<String, String> parsedContent;
    private final Set<Link> links;
    private final String url;

    public ParsedPage(String htmlPage, Map<String, String> parsedContent, Set<Link> links, String url) {
        this.htmlPage = htmlPage;
        this.parsedContent = parsedContent;
        this.links = links;
        this.url = url;
    }

    public String getHtmlPage() {
        return htmlPage;
    }

    public Map<String, String> getParsedContent() {
        return parsedContent;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public String getUrl() {
        return url;
    }

}
