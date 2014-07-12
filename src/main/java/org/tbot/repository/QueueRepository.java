package org.tbot.repository;

import java.util.Collection;

/**
 *
 * @param <T>
 */
public interface QueueRepository<T> extends Repository<T> {

    T getNext();

    Collection<T> getNext(int capacity);

}
