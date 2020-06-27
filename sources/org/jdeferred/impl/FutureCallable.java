package org.jdeferred.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureCallable<V> implements Callable<V> {
    private final Future<V> future;

    public FutureCallable(Future<V> future2) {
        this.future = future2;
    }

    public V call() throws Exception {
        try {
            return this.future.get();
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e2) {
            if (e2.getCause() instanceof Exception) {
                throw ((Exception) e2.getCause());
            }
            throw e2;
        }
    }
}
