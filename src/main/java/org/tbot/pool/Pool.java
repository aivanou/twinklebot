package org.tbot.pool;

import org.tbot.entity.CrawlEntity;
import java.util.Collection;

/**
 * class for manipulations with pool is threads-safe mode
 *
 * @param <T>
 */
public interface Pool<T extends CrawlEntity> {

    void insert(final Collection<T> entities);

    void insert(final T entity);

    T provideSingle(String id);

    Collection<T> provide();

    int size(String id);

    int criticalSize();
}
