package org.tbot.fetch;

import org.tbot.fetch.http.HttpFetcher;
import org.tbot.conf.CrawlerConfig;
import java.util.EnumMap;
import java.util.Map;

public abstract class ProtocolFetcher {

    public abstract ProtocolResponse fetch(String path) throws ProtocolException;
    private static final Map<ProtocolType, ProtocolFetcher> fetchers = new EnumMap<>(ProtocolType.class);

    public static void build(CrawlerConfig config) {
        fetchers.put(ProtocolType.Http, HttpFetcher.init(config));
        fetchers.put(ProtocolType.Https, HttpFetcher.init(config));
        fetchers.put(ProtocolType.Ftp, new FtpUrlFetcher());
    }

    public static ProtocolFetcher getFetcher(ProtocolType type) {
        if (fetchers.containsKey(type)) {
            return fetchers.get(type);
        }
        return null;
    }
}
