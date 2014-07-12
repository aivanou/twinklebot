package org.tbot.entity;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public abstract class Runner implements Runnable {

    public static final long PAUSE_TIMEOUT = 25000l;
    private volatile boolean run = true, pause = false;

    public Runner() {
    }

    public void pause() {
        pause = true;
    }

    public void resume() {
        pause = false;
    }

    public void stop() {
        run = false;
    }

    protected abstract void doJob();

    @Override
    public void run() {
        while (pause) {
            try {
                Thread.sleep(PAUSE_TIMEOUT);//TODO : change to normal pool ;wtf??
            } catch (InterruptedException ex) {
                Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        run = true;
        while (run) {
            doJob();
        }
    }
}
