package org.tbot.parse;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ParserFactory {

    private final Map<ContentType, Parser> parsers;
    private static ParserFactory factory;

    private ParserFactory() {
        this.parsers = new HashMap<>();
        this.parsers.put(ContentType.TextHtml, HtmlParser.instance());
        this.parsers.put(ContentType.TextHtml5, HtmlParser.instance());
    }

    public static ParserFactory build() {
        if (factory == null) {
            factory = new ParserFactory();
        }
        return factory;
    }

    public Parser provide(ContentType pt) {
        return parsers.get(pt);
    }

}
