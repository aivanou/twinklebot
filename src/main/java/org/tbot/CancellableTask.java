package org.tbot;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 *
 * @author tierex
 */
public interface CancellableTask<T> extends Callable<T> {

    void cancel();

    RunnableFuture<T> newTask();
}
