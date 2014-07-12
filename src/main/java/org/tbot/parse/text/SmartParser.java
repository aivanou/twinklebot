/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.parse.text;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;

/**
 * User: igor
 */
public class SmartParser {

//    private static final String GENERAL_TAG_NAME = "div";
    private static final String LINK_ATTR = "href";
    private static int MIN_LENGTH_OWN_TEXT = 5;
    private static final String[] ILLEGAL_TAGS = {"form", "select", "button"};
    private static final List<String> ILLEGAL_TAGS_LIST = Arrays.asList(ILLEGAL_TAGS); //todo is it normal?
    private Elements illegalElements = new Elements();
    private Elements approvedElements = new Elements();
    private StringBuilder sb = new StringBuilder();
    private String[] forbiddenSymbols = new String[]{",", "\\.", "/", "\'", "@", "\"", ":", "!", "#", "$", "%", "\\^", "&", "\\(", "\\)", "\\[", "\\]"};
    private String[] forbiddenTags = new String[]{"a", "li", "option", "img"};

    public SmartParser() {
    }

    public String parse(String content) {
        Document document = document = Jsoup.parse(content);
        Element root = document.body();
        if (root == null) {
            return "";
        }
        this.processNode(root);
        return root.text();
    }

    private boolean processNode(Element currNode) {
        Iterator<Element> it = currNode.children().iterator();
        int links = 0;
        while (it.hasNext()) {
            Element node = it.next();
            if (node == null) {
                links++;
                continue;
            }
            if (this.forbiddenElement(node)) {
                node.remove();
            } else if (!this.validation(node)) {
                links += 1;
            } else {
                this.processNode(node);
            }
        }
        if (links != 0 && currNode.children().size() <= links) {
            currNode.remove();
        }
        return false;
    }

    private boolean validation(Element el) {
        for (Object tag : this.forbiddenTags) {
            if (el.tag().getName().equals(tag)) {
                return false;
            }
        }
        if (!el.hasText()) {
            return false;
        }
        String text = el.text();
        for (String symb : this.forbiddenSymbols) {
            text.replaceAll(symb + "", " ");
        }
        if (text.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean forbiddenElement(Element el) {
        return el.tag().getName().equals("script");
    }

    private void processElement(Element element) {
        if ((!isLink(element) && !isPartOfLink(element))
                && (hasValidText(element))
                && (!approvedElements.contains(element))
                && (!illegalElements.contains(element))) {
            sb.append(element.text()).append("\n");
            approvedElements.addAll(element.getAllElements());
        } else if (!element.children().isEmpty()) {
            Elements children = element.children();
            for (Element child : children) {
                processElement(child);
            }
        } else {
            return;
        }
    }

    private void processElements(Elements elements) {
        for (Element el : elements) {
            processElement(el);
        }
    }

    private boolean isLink(Element element) {
        return (element.hasAttr(LINK_ATTR));
    }

    private boolean isPartOfLink(Element element) {
        for (Element parent : element.parents()) {
            if (isLink(parent)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasValidText(Element element) {
        return ((!element.ownText().isEmpty()) && (element.ownText().length() >= MIN_LENGTH_OWN_TEXT));
    }

    private void findIllegalElement(Element element) {
        if (ILLEGAL_TAGS_LIST.contains(element.tagName())) {
            illegalElements.addAll(element.getAllElements());
        } else if (!element.children().isEmpty()) {
            for (Element el : element.children()) {
                findIllegalElement(el);
            }
        } else {
            return;
        }
    }

    /**
     * Try to get page encoding from special html meta-tag
     * (http-equiv="Content-Type" content=plain/text; charset=UTF-8 for example)
     * in case when encoding not specified in web-server response header.
     *
     * @param html content of the page
     * @return
     */
    protected String getContentCharSet(String html) {
        String charset = null;
        Document doc = Jsoup.parse(html);
        // I expect only exactly one <head> tag to be there
        if (doc.getElementsByTag("head").size() >= 1) {
            Element head = doc.getElementsByTag("head").get(0);
            Elements headElements = head.children();
            for (Element headElement : headElements) {
                if (headElement.hasAttr("http-equiv")) {
                    if (headElement.attr("http-equiv").equalsIgnoreCase("content-type")) {
                        String content = headElement.attr("content");
                        if (content.contains("charset=")) {
                            int i = content.indexOf("charset=");
                            charset = content.substring(i + 8).toUpperCase();
                            break;
                        }
                    }
                }
            }
            // The case for HTML5 syntax of charset attribute in meta-tag
            if (charset == null) {
                for (Element headElement : headElements) {
                    if (headElement.hasAttr("charset")) {
                        charset = headElement.attr("charset");
                    }
                }
            }
        }
        return charset == null || charset.isEmpty() ? null : charset;
    }

    private void findIllegalElements(Elements elements) {
        for (Element el : elements) {
            findIllegalElement(el);
        }
    }
}
