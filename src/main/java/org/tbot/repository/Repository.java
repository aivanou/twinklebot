package org.tbot.repository;

import java.util.Collection;

/**
 *
 * @param <T>
 */
public interface Repository<T> {

    boolean insert(T object);

    void batchInsert(Collection<T> objects);

    void delayedInsert(T object);

    T get(String id);

    Collection<T> get(Collection<String> ids);

}
