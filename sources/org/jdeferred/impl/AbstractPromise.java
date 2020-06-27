package org.jdeferred.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jdeferred.AlwaysCallback;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPromise<D, F, P> implements Promise<D, F, P> {
    protected final List<AlwaysCallback<D, F>> alwaysCallbacks = new CopyOnWriteArrayList();
    protected final List<DoneCallback<D>> doneCallbacks = new CopyOnWriteArrayList();
    protected final List<FailCallback<F>> failCallbacks = new CopyOnWriteArrayList();
    protected final Logger log = LoggerFactory.getLogger(AbstractPromise.class);
    protected final List<ProgressCallback<P>> progressCallbacks = new CopyOnWriteArrayList();
    protected F rejectResult;
    protected D resolveResult;
    protected volatile State state = State.PENDING;

    public State state() {
        return this.state;
    }

    public Promise<D, F, P> done(DoneCallback<D> doneCallback) {
        synchronized (this) {
            if (isResolved()) {
                triggerDone(doneCallback, this.resolveResult);
            } else {
                this.doneCallbacks.add(doneCallback);
            }
        }
        return this;
    }

    public Promise<D, F, P> fail(FailCallback<F> failCallback) {
        synchronized (this) {
            if (isRejected()) {
                triggerFail(failCallback, this.rejectResult);
            } else {
                this.failCallbacks.add(failCallback);
            }
        }
        return this;
    }

    public Promise<D, F, P> always(AlwaysCallback<D, F> alwaysCallback) {
        synchronized (this) {
            if (isPending()) {
                this.alwaysCallbacks.add(alwaysCallback);
            } else {
                triggerAlways(alwaysCallback, this.state, this.resolveResult, this.rejectResult);
            }
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public void triggerDone(D d) {
        for (DoneCallback triggerDone : this.doneCallbacks) {
            try {
                triggerDone(triggerDone, d);
            } catch (Exception e) {
                this.log.error("an uncaught exception occured in a DoneCallback", (Throwable) e);
            }
        }
        this.doneCallbacks.clear();
    }

    /* access modifiers changed from: protected */
    public void triggerDone(DoneCallback<D> doneCallback, D d) {
        doneCallback.onDone(d);
    }

    /* access modifiers changed from: protected */
    public void triggerFail(F f) {
        for (FailCallback triggerFail : this.failCallbacks) {
            try {
                triggerFail(triggerFail, f);
            } catch (Exception e) {
                this.log.error("an uncaught exception occured in a FailCallback", (Throwable) e);
            }
        }
        this.failCallbacks.clear();
    }

    /* access modifiers changed from: protected */
    public void triggerFail(FailCallback<F> failCallback, F f) {
        failCallback.onFail(f);
    }

    /* access modifiers changed from: protected */
    public void triggerProgress(P p) {
        for (ProgressCallback triggerProgress : this.progressCallbacks) {
            try {
                triggerProgress(triggerProgress, p);
            } catch (Exception e) {
                this.log.error("an uncaught exception occured in a ProgressCallback", (Throwable) e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void triggerProgress(ProgressCallback<P> progressCallback, P p) {
        progressCallback.onProgress(p);
    }

    /* access modifiers changed from: protected */
    public void triggerAlways(State state2, D d, F f) {
        for (AlwaysCallback triggerAlways : this.alwaysCallbacks) {
            try {
                triggerAlways(triggerAlways, state2, d, f);
            } catch (Exception e) {
                this.log.error("an uncaught exception occured in a AlwaysCallback", (Throwable) e);
            }
        }
        this.alwaysCallbacks.clear();
        synchronized (this) {
            notifyAll();
        }
    }

    /* access modifiers changed from: protected */
    public void triggerAlways(AlwaysCallback<D, F> alwaysCallback, State state2, D d, F f) {
        alwaysCallback.onAlways(state2, d, f);
    }

    public Promise<D, F, P> progress(ProgressCallback<P> progressCallback) {
        this.progressCallbacks.add(progressCallback);
        return this;
    }

    public Promise<D, F, P> then(DoneCallback<D> doneCallback) {
        return done(doneCallback);
    }

    public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback) {
        done(doneCallback);
        fail(failCallback);
        return this;
    }

    public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback, ProgressCallback<P> progressCallback) {
        done(doneCallback);
        fail(failCallback);
        progress(progressCallback);
        return this;
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter) {
        return new FilteredPromise(this, doneFilter, null, null);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter) {
        return new FilteredPromise(this, doneFilter, failFilter, null);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter, ProgressFilter<P, P_OUT> progressFilter) {
        return new FilteredPromise(this, doneFilter, failFilter, progressFilter);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe) {
        return new PipedPromise(this, donePipe, null, null);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe) {
        return new PipedPromise(this, donePipe, failPipe, null);
    }

    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe, ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressPipe) {
        return new PipedPromise(this, donePipe, failPipe, progressPipe);
    }

    public boolean isPending() {
        return this.state == State.PENDING;
    }

    public boolean isResolved() {
        return this.state == State.RESOLVED;
    }

    public boolean isRejected() {
        return this.state == State.REJECTED;
    }

    public void waitSafely() throws InterruptedException {
        waitSafely(-1);
    }

    public void waitSafely(long j) throws InterruptedException {
        long currentTimeMillis = System.currentTimeMillis();
        synchronized (this) {
            while (isPending()) {
                int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
                if (i <= 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                } else {
                    wait(j - (System.currentTimeMillis() - currentTimeMillis));
                }
                if (i > 0) {
                    if (System.currentTimeMillis() - currentTimeMillis >= j) {
                        return;
                    }
                }
            }
        }
    }
}
