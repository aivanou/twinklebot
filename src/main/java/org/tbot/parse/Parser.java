package org.tbot.parse;

import org.tbot.fetch.ProtocolOutput;

/**
 *
 */
public abstract class Parser {

    public abstract ParsedPage parse(ProtocolOutput entity) throws ParserException;

    public abstract ParsedPage parse(String htmlContent, String urlFrom) throws ParserException;

}
