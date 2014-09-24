package org.tbot.fetch;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import org.tbot.entity.Runner;
import org.tbot.log.LoggerCreator;
import org.apache.log4j.Logger;
import org.tbot.conf.CrawlerConfig;
import org.tbot.entity.FetchUrl;
import org.tbot.fetch.http.HttpFetcher;
import org.tbot.objects.Domain;
import org.tbot.objects.DomainMetadata;
import org.tbot.objects.DomainState;
import org.tbot.objects.Link;
import org.tbot.parse.ContentType;
import org.tbot.parse.ParsedPage;
import org.tbot.parse.ParserException;
import org.tbot.parse.ParserFactory;
import org.tbot.repository.QueueRepository;
import org.tbot.repository.impl.BlockingMemoryRepository;
import org.tbot.repository.impl.MemorySetRepository;
import org.tbot.util.HttpUtil;

/**
 *
 */
public final class FetchRunner extends Runner {

    private final Logger logger = LoggerCreator.getLogger();
    private final QueueRepository<FetchUrl> urlQueue;
    private final QueueRepository<Domain> domainRepo;
    private final ProtocolFetcher fetcher = HttpFetcher.init(new CrawlerConfig());
    private final ParserFactory parserFactory;
    private final String id;

    private final static Object lock = new Object();

    public FetchRunner(QueueRepository<FetchUrl> urlQueue, QueueRepository<Domain> domainRepo, ParserFactory parserFactory, String id) {
        this.urlQueue = urlQueue;
        this.domainRepo = domainRepo;
        this.parserFactory = parserFactory;
        this.id = id;
    }

    @Override
    protected void doJob() {
        FetchUrl fetchEntity = urlQueue.getNext();
        ProtocolResponse fetchResult;
        long startTime = System.currentTimeMillis();
        try {
            fetchResult = fetcher.fetch(fetchEntity.getProcessUrl().toString());
        } catch (ProtocolException ex) {
//            System.err.println("exception occured: " + ex.getLocalizedMessage());
            fetchEntity.getHead().getMetadata().crawlPage(fetchEntity.getProcessUrl().toString());
            fetchEntity.getHead().getMetadata().releaseUrl();
            changeDomain(fetchEntity);
            return;
        }
        if (!fetchResult.getStatusState().getState().equals(ProtocolStatus.Type.Valid)) {
            fetchEntity.getHead().getMetadata().crawlPage(fetchEntity.getProcessUrl().toString());
            fetchEntity.getHead().getMetadata().releaseUrl();
            changeDomain(fetchEntity);
            return;
        }
        long endTime = System.currentTimeMillis() - startTime;
//        startTime = System.currentTimeMillis();

        ParsedPage parsedPage;
        try {
            parsedPage = parserFactory.provide(ContentType.TextHtml).parse(fetchResult.getProtocolOutput());
        } catch (ParserException ex) {
            java.util.logging.Logger.getLogger(FetchRunner.class.getName()).log(Level.SEVERE, null, ex);
            fetchEntity.getHead().getMetadata().crawlPage(fetchEntity.getProcessUrl().toString());
            fetchEntity.getHead().getMetadata().releaseUrl();
            changeDomain(fetchEntity);
            return;
        }
        fetchEntity.getHead().getMetadata().crawlPage(fetchEntity.getProcessUrl().toString());
//        System.out.println("thread: " + id + ":  parse time : " + (System.currentTimeMillis() - startTime) + " url:  " + fetchEntity.getProcessUrl().toString());
        Collection<String> strDomains = HttpUtil.getDomainsFromLinksTo(parsedPage.getOutLinks());
        Collection<Domain> newDomains = buildDomains(strDomains);
//        startTime = System.currentTimeMillis();
        for (Domain domain : newDomains) {
            domainRepo.insert(domain);
        }

        validate(fetchEntity, parsedPage);

        changeDomain(fetchEntity);
//        System.out.println("thread: " + id + ":  validation time : " + (System.currentTimeMillis() - startTime));
        System.out.println("thread: " + id + " busy urls: " + fetchEntity.getHead().getMetadata().getBusyUrlsAmount() + "  fetch time: " + endTime + "  url: " + fetchEntity.getProcessUrl().toString() + "  status: " + fetchResult.getStatusState().getState());
    }

    private void changeDomain(FetchUrl fetchEntity) {
        if (fetchEntity.getHead().getMetadata().getBusyUrlsAmount() == 0) {
            fetchEntity.getHead().getMetadata().changeDomainState(DomainState.Crawled);
            Domain nextDomain = domainRepo.getNext();
            try {
                FetchUrl furl = new FetchUrl(nextDomain.toString(), nextDomain, new PageMetadata(1));
                urlQueue.insert(furl);
                nextDomain.getMetadata().acquireUrl();
            } catch (MalformedURLException ex) {
                java.util.logging.Logger.getLogger(FetchRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void validate(FetchUrl fetchEntity, ParsedPage parsedPage) {
        if (fetchEntity.getPageMetadata().getDepth() > fetchEntity.getHead().getMetadata().getMaxDepth()) {

        } else {
            Collection<FetchUrl> newUrls = buildFetchUrls(parsedPage.getInLinks(), fetchEntity.getHead(), fetchEntity.getPageMetadata());
            for (FetchUrl fetchUrl : newUrls) {
                if (!fetchEntity.getHead().getMetadata().isPageCrawled(fetchUrl.getProcessUrl().toString())) {
                    boolean isInserted = urlQueue.insert(fetchUrl);
                    if (isInserted) {
                        fetchEntity.getHead().getMetadata().acquireUrl();
                    }
                }
            }
        }
        fetchEntity.getHead().getMetadata().releaseUrl();
        if (fetchEntity.getHead().getMetadata().getBusyUrlsAmount() == 0) {
            System.out.println("thread: " + id + ":  host: " + fetchEntity.getHead().getDomain().toString() + " crawled, urls count: " + fetchEntity.getHead().getMetadata().getCrawledUrlsSize() + " cache size:  " + urlQueue.size());
        }
    }

    private Collection<FetchUrl> buildFetchUrls(Collection<Link> links, Domain domain, PageMetadata pageFromMetadata) {
        Collection<FetchUrl> urls = new HashSet<>();
        for (Link link : links) {
            try {
                urls.add(new FetchUrl(link.to().toString(), domain, new PageMetadata(pageFromMetadata.getDepth() + 1)));
            } catch (MalformedURLException ex) {
                java.util.logging.Logger.getLogger(FetchRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return urls;
    }

    private Collection<Domain> buildDomains(Collection<String> strDomains) {
        Collection<Domain> domains = new ArrayList<>(strDomains.size());
        for (String d : strDomains) {
            domains.add(Domain.parse(d));
        }
        return domains;
    }

    public static void main(String[] args) throws MalformedURLException {
        ParserFactory parserFactory = ParserFactory.build();
        QueueRepository<FetchUrl> urlQueue = new BlockingMemoryRepository<>(1000);
        QueueRepository<Domain> domainQueue = new MemorySetRepository<>(1000);
        Domain d = Domain.parse("http://en.wikipedia.org", new DomainMetadata());
        urlQueue.insert(new FetchUrl(d.toString(), d, new PageMetadata(1)));
        d.getMetadata().acquireUrl();
        new FetchRunner(urlQueue, domainQueue, parserFactory, "test").doJob();
    }
}
