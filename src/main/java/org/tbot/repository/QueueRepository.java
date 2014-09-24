package org.tbot.repository;

/**
 *
 * @param <T>
 */
public interface QueueRepository<T> extends Repository<T> {

    T getNext();

}
