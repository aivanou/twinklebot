package org.tbot.repository;

/**
 *
 * @param <T>
 */
public interface Repository<T> {

    boolean insert(T object);
    
    int size();

}
