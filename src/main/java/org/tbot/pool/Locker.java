package org.tbot.pool;

/**
 * simple locker, thread-safe
 *
 * @author Vadim Martos
 *         Date: 11/18/11
 */

public abstract class Locker {
    /**
     * indicates, if it's locked
     */
    private volatile boolean locked = false;
    /**
     * indicates, if it's stopped
     * use this flag in thread to exit from cycle in run() method
     */
    private volatile boolean stopped = false;

    public void lock() {
        locked = true;
    }

    private void unlock() {
        locked = false;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void wakeUp() {
        synchronized (this) {
            this.unlock();
            this.notifyAll();
        }
    }

    public void sleepDown() {
        synchronized (this) {
            this.lock();
        }
    }

    public void stop() {
        synchronized (this) {
            stopped = true;
        }
    }
}
