package org.jdeferred;

import java.util.concurrent.Callable;
import org.jdeferred.DeferredManager.StartPolicy;
import org.jdeferred.impl.DeferredObject;

public abstract class DeferredCallable<D, P> implements Callable<D> {
    private final Deferred<D, Throwable, P> deferred;
    private final StartPolicy startPolicy;

    public DeferredCallable() {
        this.deferred = new DeferredObject();
        this.startPolicy = StartPolicy.DEFAULT;
    }

    public DeferredCallable(StartPolicy startPolicy2) {
        this.deferred = new DeferredObject();
        this.startPolicy = startPolicy2;
    }

    /* access modifiers changed from: protected */
    public void notify(P p) {
        this.deferred.notify(p);
    }

    /* access modifiers changed from: protected */
    public Deferred<D, Throwable, P> getDeferred() {
        return this.deferred;
    }

    public StartPolicy getStartPolicy() {
        return this.startPolicy;
    }
}
