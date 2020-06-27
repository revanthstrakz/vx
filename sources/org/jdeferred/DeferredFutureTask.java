package org.jdeferred;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.jdeferred.DeferredManager.StartPolicy;
import org.jdeferred.impl.DeferredObject;

public class DeferredFutureTask<D, P> extends FutureTask<D> {
    protected final Deferred<D, Throwable, P> deferred;
    protected final StartPolicy startPolicy;

    public DeferredFutureTask(Callable<D> callable) {
        super(callable);
        this.deferred = new DeferredObject();
        this.startPolicy = StartPolicy.DEFAULT;
    }

    public DeferredFutureTask(Runnable runnable) {
        super(runnable, null);
        this.deferred = new DeferredObject();
        this.startPolicy = StartPolicy.DEFAULT;
    }

    public DeferredFutureTask(DeferredCallable<D, P> deferredCallable) {
        super(deferredCallable);
        this.deferred = deferredCallable.getDeferred();
        this.startPolicy = deferredCallable.getStartPolicy();
    }

    public DeferredFutureTask(DeferredRunnable<P> deferredRunnable) {
        super(deferredRunnable, null);
        this.deferred = deferredRunnable.getDeferred();
        this.startPolicy = deferredRunnable.getStartPolicy();
    }

    public Promise<D, Throwable, P> promise() {
        return this.deferred.promise();
    }

    /* access modifiers changed from: protected */
    public void done() {
        try {
            if (isCancelled()) {
                this.deferred.reject(new CancellationException());
            }
            this.deferred.resolve(get());
        } catch (InterruptedException unused) {
        } catch (ExecutionException e) {
            this.deferred.reject(e.getCause());
        }
    }

    public StartPolicy getStartPolicy() {
        return this.startPolicy;
    }
}
