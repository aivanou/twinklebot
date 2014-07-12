package org.tbot.fetch;

import java.util.Collection;
import java.util.logging.Level;
import org.tbot.entity.Runner;
import org.tbot.Bot;
import org.tbot.loggin.LoggerCreator;
import org.tbot.pool.Pool;
import org.tbot.statistics.Statistics;
import org.apache.log4j.Logger;
import org.tbot.entity.FetchUrl;
import org.tbot.fetch.http.HttpFetcher;
import org.tbot.objects.Domain;
import org.tbot.parse.HtmlParser;
import org.tbot.parse.ParsedPage;
import org.tbot.parse.Parser;
import org.tbot.parse.ParserException;
import org.tbot.repository.QueueRepository;
import org.tbot.repository.impl.MemoryRepository;
import org.tbot.repository.impl.MemorySetRepository;

/**
 *
 */
public final class FetchRunner extends Runner {

    private final Pool<FetchUrl> fetchPool;
    private final String id;
    private final Logger logger = LoggerCreator.getLogger();
    private final QueueRepository<FetchUrl> urlQueue = new MemoryRepository<>(1000);
    private final QueueRepository<Domain> domainRepo = new MemorySetRepository<>(100);
    private final ProtocolFetcher fetcher = new HttpFetcher();
    private final Parser htmlParser = HtmlParser.instance();

    public FetchRunner(Pool<FetchUrl> fetchPool, Bot bot, String id) {
        this.id = id;
        this.fetchPool = fetchPool;
    }

    @Override
    protected void doJob() {
        FetchUrl fetchEntity = urlQueue.getNext(); // change to bolocking queue
        if (fetchEntity == null) {
            return;
        }
        ProtocolResponse fetchResult;
        try {
            fetchResult = fetcher.fetch(fetchEntity.getProcessUrl().toString());
        } catch (ProtocolException ex) {
            java.util.logging.Logger.getLogger(FetchRunner.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        ParsedPage parsedPage;
        try {
            parsedPage = htmlParser.parse(fetchResult.getProtocolOutput());
        } catch (ParserException ex) {
            java.util.logging.Logger.getLogger(FetchRunner.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        // send(parsedPage)
        Collection<Domain> newDomains = null;
        domainRepo.batchInsert(newDomains);
        // check current domain
        // change domain state if necessary
        // if state finished -- add new url to the with new domain
    }

    private ProtocolResponse fetch(String fetchEntity) {

        return null;
    }
}
