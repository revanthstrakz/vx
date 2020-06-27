package org.jdeferred;

public interface Deferred<D, F, P> extends Promise<D, F, P> {
    Deferred<D, F, P> notify(P p);

    Promise<D, F, P> promise();

    Deferred<D, F, P> reject(F f);

    Deferred<D, F, P> resolve(D d);
}
