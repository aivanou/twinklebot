package org.tbot.fetch.ftp;

import org.tbot.fetch.ProtocolException;
import org.tbot.fetch.ProtocolFetcher;
import org.tbot.fetch.ProtocolResponse;

/**
 *
 */
public class FtpUrlFetcher extends ProtocolFetcher {

    @Override
    public ProtocolResponse fetch(String path) throws ProtocolException {
        throw new IllegalArgumentException("not implemented yet.");
    }
}
