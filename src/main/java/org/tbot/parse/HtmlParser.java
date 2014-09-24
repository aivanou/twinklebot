package org.tbot.parse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import org.tbot.objects.Link;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tbot.fetch.ProtocolOutput;
import org.tbot.util.HttpUtil;

/**
 * This class is html code parser. Using open source project - Jsoup.
 */
public class HtmlParser extends Parser {

    protected static final Logger logger = Logger.getLogger(HtmlParser.class.getName());
    private static HtmlParser instance = null;

    private HtmlParser() {
    }

    public static synchronized HtmlParser instance() {
        if (instance == null) {
            instance = new HtmlParser();
        }
        return instance;
    }

    @Override
    public ParsedPage parse(ProtocolOutput entity) throws ParserException {
        String htmlContent;
        if (entity.getEncoding() == null) {
            htmlContent = new String(entity.getContent().array());
        } else {
            htmlContent = new String(entity.getContent().array(), Charset.forName(entity.getEncoding()));
        }
        return parse(htmlContent, entity.getUrl());
    }

    @Override
    public ParsedPage parse(String htmlContent, String urlFrom) throws ParserException {
        Document doc = Jsoup.parse(htmlContent);
        String domainFrom = HttpUtil.getProtocol(urlFrom).toString() + "://" + HttpUtil.getDomain(urlFrom);
        Collection<Link> inLinks = new ArrayList<>();
        Collection<Link> outLinks = new ArrayList<>();
        gatherLinks(doc, urlFrom, domainFrom, inLinks, outLinks);
        Map<String, String> content = gatherContent(doc);
        ParsedPage page = new ParsedPage(htmlContent, content, inLinks, outLinks, urlFrom);
        return page;
    }

    private Map<String, String> gatherContent(Document doc) {
        Map<String, String> content = new HashMap<>();
        content.put("title", doc.title());
        content.put("content", doc.text());
        return content;
    }

    private Collection<Link> gatherLinks(Document doc, String urlFrom, String domainFrom, Collection<Link> inLinks, Collection<Link> outLinks) {
        Elements links = doc.select("a[href]");
        Collection<Link> linkSet = new ArrayList<>();
        for (Element el : links) {
            String linkAddress = el.attr("href").trim();
            if (linkAddress != null && !linkAddress.isEmpty()) {
                String relevantAddres = "";
                try {
                    relevantAddres = HttpUtil.normalizeUrl(domainFrom, linkAddress);
                    Link link = new Link(urlFrom, relevantAddres, el.text(), "default");
                    if (urlBelongsToDomain(link.to().toString(), domainFrom)) {
                        inLinks.add(link);
                    } else {
                        outLinks.add(link);
                    }
//                    System.out.println("url:  " + relevantAddres + "  from:  " + linkAddress);
                } catch (MalformedURLException ignore) {
//                    System.out.println("Incorrect url: " + linkAddress + "   to:  " + relevantAddres);
                }
            }
        }
        return linkSet;
    }

    private boolean urlBelongsToDomain(String url, String domain) {
        return url.startsWith(domain);
    }

    public static void main(String[] args) throws Exception {
        URL url = ClassLoader.getSystemResource("test3.html");
        String content = readFile(url.toURI());
        HtmlParser p = new HtmlParser();
        ParsedPage page = p.parse(content, "http://stackoverflow.com");
//        for (Link link : page.getLinks()) {
//            System.out.println(link.to());
//        }
        int i = 0;
    }

    public static String readFile(URI uri) throws Exception {
        File testFile = new File(uri);
        BufferedInputStream str = new BufferedInputStream(new FileInputStream(testFile));
        StringBuilder sb = new StringBuilder();
        int a;
        while ((a = str.read()) > 0) {
            sb.append((char) a);
        }
        str.close();
        return sb.toString();
    }
}
