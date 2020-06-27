package org.jdeferred;

import org.jdeferred.DeferredManager.StartPolicy;
import org.jdeferred.impl.DeferredObject;

public abstract class DeferredRunnable<P> implements Runnable {
    private final Deferred<Void, Throwable, P> deferred;
    private final StartPolicy startPolicy;

    public DeferredRunnable() {
        this.deferred = new DeferredObject();
        this.startPolicy = StartPolicy.DEFAULT;
    }

    public DeferredRunnable(StartPolicy startPolicy2) {
        this.deferred = new DeferredObject();
        this.startPolicy = startPolicy2;
    }

    /* access modifiers changed from: protected */
    public void notify(P p) {
        this.deferred.notify(p);
    }

    /* access modifiers changed from: protected */
    public Deferred<Void, Throwable, P> getDeferred() {
        return this.deferred;
    }

    public StartPolicy getStartPolicy() {
        return this.startPolicy;
    }
}
