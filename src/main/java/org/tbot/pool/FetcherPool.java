package org.tbot.pool;

import org.tbot.Bot;
import org.tbot.loggin.LoggerCreator;
import org.tbot.objects.ObjectToCrawl;
import org.tbot.state.CrawlState;
import org.tbot.storage.SiteStorage;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.tbot.entity.FetchUrl;

/**
 *
 */
public class FetcherPool extends CommonPool<FetchUrl> {

    private final SiteStorage storage;
    private final Queue<ObjectToCrawl> urlsInUse;
    private final int maxDomainsInQueue;
    private final Logger logger = LoggerCreator.getLogger();

    public FetcherPool(int criticalSize,
            SiteStorage storage, int maxDomainsIntQueue, int maxThreadsPerHost) {
        super(criticalSize);
        this.urlsInUse = new ConcurrentLinkedQueue<>();
        this.storage = storage;
        this.maxDomainsInQueue = maxDomainsIntQueue;
        synchronized (this.urlsInUse) {
            this.initFetcherPool();
        }
    }

    private void initFetcherPool() {
        Collection<ObjectToCrawl> startEntities = this.readFromStorage(this.maxDomainsInQueue);
//        Collection<ObjectToCrawl> startEntities = new LinkedList<ObjectToCrawl>();
//        startEntities.add(new ObjectToCrawl("http://th.wikipedia.org/w/index.php?title=%E0%B8%9E%E0%B8%B4%E0%B9%80%E0%B8%A8%E0%B8%A9:%E0%B8%9A%E0%B8%97%E0%B8%84%E0%B8%A7%E0%B8%B2%E0%B8%A1%E0%B8%97%E0%B8%B5%E0%B9%88%E0%B9%82%E0%B8%A2%E0%B8%87%E0%B8%A1%E0%B8%B2&target=%E0%B8%A0%E0%B8%B2%E0%B8%A9%E0%B8%B2%E0%B8%AD%E0%B8%B9%E0%B8%A3%E0%B8%94%E0%B8%B9", 100, 100000, 109409, CrawlObjectType.Domain, 1.0f, NextDepthInsertStrategy.OnlyCurrentDomainUrls));
        String[] ids = Bot.getIds();
        int index = 0;
        for (ObjectToCrawl obj : startEntities) {
            this.initCrawlState(obj, ids[index]);
            BlockingQueue<FetchUrl> queue = new LinkedBlockingQueue<>(this.criticalSize);
            String id = ids[index];
            this.initFetcherQueue(obj, queue);
            FetcherPoolEntity ent = new FetcherPoolEntity(id, obj.getUrl(), queue);
            this.fetcherPool.add(ent);
            index++;
        }
    }

    private void initFetcherQueue(ObjectToCrawl obj, BlockingQueue<FetchUrl> queue) {
        try {
            queue.add(new FetchUrl(obj.getUrl(), obj));
        } catch (MalformedURLException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private Collection<ObjectToCrawl> readFromStorage(int size) {
        Collection<ObjectToCrawl> crObjects = new LinkedList<ObjectToCrawl>();
        for (int i = 0; i < size; i++) {
            ObjectToCrawl crawlObject = storage.pollSite();
            if (!urlsInUse.contains(crawlObject)) {
                this.urlsInUse.add(crawlObject);
                if (crawlObject.getUrl().endsWith("/")) {
                    crawlObject.setUrl(crawlObject.getUrl().substring(0, crawlObject.getUrl().length() - 1));
                }
                crObjects.add(crawlObject);
            }
        }
//        crObjects.add(new ObjectToCrawl("http://yandex.ru", 11, 1111, 1, CrawlObjectType.Domain, NextDepthInsertStrategy.OnlyFirstLevelDomains));
        return crObjects;
    }

    //not thread safe
    @Override
    public void insert(final Collection<FetchUrl> entities) {
//        if (feedback.getStatus().equals(ObjectToCrawlProcessStatus.Finished)) {
//            this.changePoolObject(feedback.getHead());
//            return;
//        }
//        Collection<FetchUrl> tempEntities = null;
//        synchronized (entities) {
//            tempEntities = new ArrayList<FetchUrl>(entities);
//        }
//        BlockingQueue<FetchUrl> fqueue = null;
//        FetcherPoolEntity fe = this.getByUrl(feedback.getHead().getUrl());
//        //if thread that has a feedback.finished execute insert method before another thread
//        //that has feedback.processed , fe with url can have a null value
//        if (fe == null) {
//            CrawlState state = CrawlState.getCrawlState(feedback.getHead().getUrl());
//            if (state != null) {
//                for (int i = 0; i < tempEntities.size(); i++) {
//                    state.releaseUrl();
//                }
//            }
//            return;
//        }
//        fqueue = fe.getQueue();
//        for (FetchUrl ent : tempEntities) {
//            if (ent == null) {
//                CrawlState state = CrawlState.getCrawlState(feedback.getHead().getUrl());
//                if (state != null) {
//                    state.releaseUrl();
//                }
//                continue;
//            }
//            try {
//                fqueue.put(ent);
//            } catch (InterruptedException ex) {
//                logger.error(ex.getMessage(), ex);
//                continue;
//            }
//        }
////        for (FetcherPoolEntity fent : this.fetcherPool) {
////            BlockingQueue<FetchEntity> q = fent.getQueue();
//        this.processEmptyPoolInitialization(fqueue, fe.getProcessingUrl());
////        }
    }

    //make it synchronized?
    private void changePoolObject(ObjectToCrawl objCr) {
        CrawlState state = CrawlState.getCrawlState(objCr.getUrl());
        if (state != null) {
            state.releaseAllUrls();
        }
        FetcherPoolEntity fe = this.getByUrl(objCr.getUrl());
        if (fe == null) {
            return;
        }
        BlockingQueue<FetchUrl> q = fe.getQueue();
        q.clear();
        urlsInUse.remove(objCr);
        ObjectToCrawl newObj = this.storage.pollSite();
        fe.setProcessingUrl(newObj.getUrl());
        urlsInUse.add(newObj);
        initCrawlState(newObj, fe.getId());
        initFetcherQueue(newObj, q);
    }

    private void processEmptyPoolInitialization(BlockingQueue<FetchUrl> queue, String id) {
        if (!queue.isEmpty()) {
            return;
        }
        CrawlState state = CrawlState.getCrawlState(id);
        int remainingCapacity = queue.remainingCapacity();
        for (int i = 0; i < remainingCapacity; i++) {
            String url = state.nextUrl(id);
            //if somehow all threads finished their job and there was no thread with feedback=finished
            //here will be url=null and we processing new url
            if (url == null) {
                if (state.getTakenUrls() == 0) {
                    changePoolObject(state.getState());
                }
                return;
            }
            //there is still threads that did not released url
            if (url.isEmpty()) {
                return;
            }
            try {
                queue.put(new FetchUrl(url, state.getState()));
            } catch (InterruptedException | MalformedURLException ex) {
                logger.error(ex.getMessage(), ex);
                state.releaseUrl();
            }
        }
    }

    protected void initCrawlState(ObjectToCrawl objToCrawl, String id) {
        CrawlState state;
        if ((state = CrawlState.getCrawlState(objToCrawl.getUrl())) == null) {
            try {
                state = CrawlState.initCrawlState(objToCrawl);
            } catch (MalformedURLException ex) {
                return;
            }
        } else {
            state.clear();
        }
        state.addUrlToNextDepth(objToCrawl.getUrl());
        state.finishCurrentDepth();
        state.nextUrl(id);
    }

    @Override
    public Collection<FetchUrl> provide() {
        //TODO make
        return null;
    }

    @Override
    public FetchUrl provideSingle(String id) {
        try {
            BlockingQueue<FetchUrl> queue = this.getById(id).getQueue();
            logger.info("queue size: " + queue.size());
            FetchUrl ent = queue.take();
            return ent;
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public void insert(FetchUrl entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int criticalSize() {
        return criticalSize;
    }
}
