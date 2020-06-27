package org.jdeferred.impl;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.Promise.State;

public class DeferredObject<D, F, P> extends AbstractPromise<D, F, P> implements Deferred<D, F, P> {
    public Promise<D, F, P> promise() {
        return this;
    }

    public Deferred<D, F, P> resolve(D d) {
        synchronized (this) {
            if (isPending()) {
                this.state = State.RESOLVED;
                this.resolveResult = d;
                try {
                    triggerDone(d);
                } finally {
                    triggerAlways(this.state, d, null);
                }
            } else {
                throw new IllegalStateException("Deferred object already finished, cannot resolve again");
            }
        }
        return this;
    }

    public Deferred<D, F, P> notify(P p) {
        synchronized (this) {
            if (isPending()) {
                triggerProgress(p);
            } else {
                throw new IllegalStateException("Deferred object already finished, cannot notify progress");
            }
        }
        return this;
    }

    public Deferred<D, F, P> reject(F f) {
        synchronized (this) {
            if (isPending()) {
                this.state = State.REJECTED;
                this.rejectResult = f;
                try {
                    triggerFail(f);
                } finally {
                    triggerAlways(this.state, null, f);
                }
            } else {
                throw new IllegalStateException("Deferred object already finished, cannot reject again");
            }
        }
        return this;
    }
}
