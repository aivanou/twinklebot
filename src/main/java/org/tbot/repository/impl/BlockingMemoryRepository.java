package org.tbot.repository.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tbot.repository.QueueRepository;

/**
 *
 * @param <T>
 */
public class BlockingMemoryRepository<T> extends AbstractQueueRepository<T> implements QueueRepository<T> {

    private final BlockingQueue<T> cache;
    private final Set<Integer> objectsInCache;

    public BlockingMemoryRepository(int capacity) {
        this.cache = new ArrayBlockingQueue<>(capacity);
        this.objectsInCache = new HashSet<>(capacity);
    }

    @Override
    protected T getNextElement() {
        try {
            T obj = cache.take();
            if (obj != null) {
                objectsInCache.remove(obj.hashCode());
            }
            return obj;
        } catch (InterruptedException ex) {
            Logger.getLogger(BlockingMemoryRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected boolean insertElement(T object) {
        if (objectsInCache.contains(object.hashCode())) {
            return false;
        }
        if (cache.offer(object)) {
            objectsInCache.add(object.hashCode());
            return true;
        }
        return false;
    }

    @Override
    public T getNext() {
        return getNextElement();
    }

    @Override
    public boolean insert(T object) {
        return insertElement(object);
    }

    @Override
    public int size() {
        return cache.size();
    }

}
