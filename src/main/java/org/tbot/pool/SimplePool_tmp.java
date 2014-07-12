/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.pool;

import org.tbot.entity.CrawlEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author alex
 */
public class SimplePool_tmp<T> {

    protected final Queue<T> pool = new LinkedList<T>();
    private final int critical;

    public SimplePool_tmp(int critical) {
        this.critical = critical > 0 ? critical : 1;
    }

    /*
     * return size of a pool
     */
    public int size() {
        synchronized (pool) {
            return pool.size();
        }
    }

    public int critical() {
        return critical;
    }

    /**
     * add specified object to pool
     *
     * @param object to add
     */
    public void add(final T object) throws InterruptedException {
        synchronized (pool) {
            while (pool.size() >= critical) {
                pool.wait();
            }
            pool.add(object);
            pool.notifyAll();
        }
    }

    /**
     * add a collection of objects to pool
     *
     * @param objects to add
     */
    public void add(final Collection<T> objects) throws InterruptedException {
        synchronized (pool) {
            while (pool.size() >= critical) {
                pool.wait();
            }
            pool.addAll(objects);
            pool.notifyAll();
        }
    }

    /**
     * Метод ждет до тех пор, пока в пуле не появятся объекты, и возвращает
     * очередной объект из пула
     *
     * @return очередной объет из пула
     * @throws InterruptedException, если поток поврежден
     */
    public T poll() throws InterruptedException {
        synchronized (pool) {
            while (pool.isEmpty()) {
                pool.wait();
            }
            pool.notifyAll();
            return pool.poll();
        }
    }

    /**
     * Возвращает
     *
     * @param count
     * @return
     * @throws InterruptedException
     */
    public Collection<T> poll(int count) throws InterruptedException {
        synchronized (pool) {
            while (pool.isEmpty() || count > pool.size()) {
                pool.wait();
            }
            Collection<T> result = new ArrayList<T>(count);
            for (int i = 0; i < count; ++i) {
                result.add(pool.poll());
            }
            pool.notifyAll();
            return result;
        }
    }

    public Collection<T> pollAll() throws InterruptedException {
        synchronized (pool) {
            while (pool.isEmpty()) {
                pool.wait();
            }
            int count = pool.size();
            Collection<T> result = new ArrayList<T>(count);
            for (int i = 0; i < count; ++i) {
                result.add(pool.poll());
            }
            //pool.notify();
            return result;
        }
    }
}
