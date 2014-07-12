package org.tbot.pool;

import org.tbot.entity.CrawlEntity;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * class for manipulations with pool is threads-safe mode
 *
 * @param <T>
 */
public abstract class CommonPool<T extends CrawlEntity> implements Pool<T> {

    protected AtomicInteger poolSize;
    protected int defaultProvideValue = 10;
    protected final int criticalSize;
    protected final List<FetcherPoolEntity> fetcherPool;

    public CommonPool(int critSize) {
        criticalSize = critSize;
        poolSize = new AtomicInteger(0);
        fetcherPool = new LinkedList<>();
    }

    public CommonPool(int criticalSize, int defaultProvideValue) {
        this(criticalSize);
        this.defaultProvideValue = defaultProvideValue;
    }

    protected boolean containsUrl(String url) {
        for (FetcherPoolEntity fe : fetcherPool) {
            if (fe.getProcessingUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }

    protected FetcherPoolEntity getByUrl(String url) {
        for (FetcherPoolEntity fe : fetcherPool) {
            if (fe.getProcessingUrl().equals(url.trim())) {
                return fe;
            }
        }
        return null;
    }

    protected FetcherPoolEntity getById(String id) {
        for (FetcherPoolEntity fe : fetcherPool) {
            if (fe.getId().equals(id)) {
                return fe;
            }
        }
        return null;
    }

    @Override
    public int size(String id) {
        return getById(id).getQueue().size();
    }
}
