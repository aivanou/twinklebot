package org.tbot.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.tbot.repository.QueueRepository;

/**
 *
 * @param <T>
 */
public abstract class AbstractQueueRepository<T> implements QueueRepository<T> {

    protected final Object lock = new Object();

    protected abstract T getNextElement();

    protected abstract boolean insertElement(T object);

    @Override
    public T getNext() {
        T domain;
        synchronized (lock) {
            domain = getNextElement();
        }
        return domain;
    }

    @Override
    public Collection<T> getNext(int amount) {
        List<T> domains = new ArrayList<>();
        synchronized (lock) {
            for (int i = 0; i < amount; ++i) {
                T d = getNextElement();
                if (d == null) {
                    return domains;
                }
                domains.add(d);
            }
        }
        return domains;
    }

    @Override
    public boolean insert(T object) {
        synchronized (lock) {
            return insertElement(object);
        }
    }

    @Override
    public void batchInsert(Collection<T> objects) {
        synchronized (lock) {
            for (T domain : objects) {
                insertElement(domain);
            }
        }
    }

    @Override
    public void delayedInsert(T object) {
        insert(object);
    }

    @Override
    public T get(String id) {
        return getNext();
    }

    @Override
    public Collection<T> get(Collection<String> ids) {
        return getNext(ids.size());
    }

}
