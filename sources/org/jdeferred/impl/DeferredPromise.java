package org.jdeferred.impl;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.FailPipe;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.ProgressPipe;
import org.jdeferred.Promise;
import org.jdeferred.Promise.State;

public class DeferredPromise<D, F, P> implements Promise<D, F, P> {
    protected final Deferred<D, F, P> deferred;
    private final Promise<D, F, P> promise;

    public DeferredPromise(Deferred<D, F, P> deferred2) {
        this.deferred = deferred2;
        this.promise = deferred2.promise();
    }

    public State state() {
        return this.promise.state();
    }

    public boolean isPending() {
        return this.promise.isPending();
    }

    public boolean isResolved() {
        return this.promise.isResolved();
    }

    public boolean isRejected() {
        return this.promise.isRejected();
    }

    public Promise<D, F, P> then(DoneCallback<D> doneCallback) {
        return this.promise.then(doneCallback);
    }

    public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback) {
        return this.promise.then(doneCallback, failCallback);
    }

    public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback, ProgressCallback<P> progressCallback) {
        return this.promise.then(doneCallback, failCallback, progressCallback);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter) {
        return this.promise.then(doneFilter);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter) {
        return this.promise.then(doneFilter, failFilter);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter, ProgressFilter<P, P_OUT> progressFilter) {
        return this.promise.then(doneFilter, failFilter, progressFilter);
    }

    public Promise<D, F, P> done(DoneCallback<D> doneCallback) {
        return this.promise.done(doneCallback);
    }

    public Promise<D, F, P> fail(FailCallback<F> failCallback) {
        return this.promise.fail(failCallback);
    }

    public Promise<D, F, P> always(AlwaysCallback<D, F> alwaysCallback) {
        return this.promise.always(alwaysCallback);
    }

    public Promise<D, F, P> progress(ProgressCallback<P> progressCallback) {
        return this.promise.progress(progressCallback);
    }

    public void waitSafely() throws InterruptedException {
        this.promise.waitSafely();
    }

    public void waitSafely(long j) throws InterruptedException {
        this.promise.waitSafely(j);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe) {
        return this.promise.then(donePipe);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe) {
        return this.promise.then(donePipe, failPipe);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe, ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressPipe) {
        return this.promise.then(donePipe, failPipe, progressPipe);
    }
}
