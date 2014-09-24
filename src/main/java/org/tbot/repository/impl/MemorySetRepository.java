package org.tbot.repository.impl;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import org.tbot.repository.QueueRepository;

/**
 *
 */
public class MemorySetRepository<T> extends AbstractQueueRepository<T> implements QueueRepository<T> {

    private final Queue<T> cache;
    private final Set<Integer> domainsInCache;

    public MemorySetRepository(int capacity) {
        this.cache = new ArrayDeque<>(capacity);
        this.domainsInCache = new HashSet<>(capacity);
    }

    @Override
    protected T getNextElement() {
        T domain = cache.poll();
        if (domain != null) {
            domainsInCache.remove(domain.hashCode());
        }
        return domain;
    }

    @Override
    protected boolean insertElement(T object) {
        if (domainsInCache.contains(object.hashCode())) {
            return true;
        }
        boolean isInserted = cache.offer(object);
        if (isInserted) {
            domainsInCache.add(object.hashCode());
        }
        return isInserted;
    }

    @Override
    public int size() {
        return cache.size();
    }

}
