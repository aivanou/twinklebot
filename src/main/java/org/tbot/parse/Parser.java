package org.tbot.parse;

import java.util.EnumMap;
import java.util.Map;
import org.tbot.fetch.ProtocolOutput;

/**
 *
 */
public abstract class Parser {

    public abstract ParsedPage parse(ProtocolOutput entity) throws ParserException;

    public abstract ParsedPage parse(String htmlContent, String urlFrom) throws ParserException;

    protected static final Map<ContentType, Parser> parsers = new EnumMap<>(ContentType.class);

    public static void build() {
        parsers.put(ContentType.TextHtml, HtmlParser.instance());
        parsers.put(ContentType.TextHtml5, HtmlParser.instance());
    }

    public static Parser provide(ContentType type) {
        if (!parsers.containsKey(type)) {
            return null;
        }
        return parsers.get(type);
    }

    public static void register(ContentType type, Parser parser) {
        parsers.put(type, parser);
    }
}
