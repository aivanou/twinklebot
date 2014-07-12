package org.tbot.repository.impl;

import java.util.ArrayDeque;
import java.util.Queue;
import org.tbot.repository.QueueRepository;

/**
 *
 * @param <T>
 */
public class MemoryRepository<T> extends AbstractQueueRepository<T> implements QueueRepository<T> {

    private final Queue<T> cache;

    public MemoryRepository(int capacity) {
        this.cache = new ArrayDeque<>();
    }

    @Override
    protected T getNextElement() {
        return cache.poll();
    }

    @Override
    protected boolean insertElement(T object) {
        return cache.offer(object);
    }

}
