package org.jdeferred.impl;

import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.FailPipe;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressPipe;
import org.jdeferred.Promise;

public class PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT> extends DeferredObject<D_OUT, F_OUT, P_OUT> implements Promise<D_OUT, F_OUT, P_OUT> {
    public PipedPromise(Promise<D, F, P> promise, final DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, final FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe, final ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressPipe) {
        promise.done(new DoneCallback<D>() {
            public void onDone(D d) {
                if (donePipe != null) {
                    PipedPromise.this.pipe(donePipe.pipeDone(d));
                } else {
                    PipedPromise.this.resolve(d);
                }
            }
        }).fail(new FailCallback<F>() {
            public void onFail(F f) {
                if (failPipe != null) {
                    PipedPromise.this.pipe(failPipe.pipeFail(f));
                } else {
                    PipedPromise.this.reject(f);
                }
            }
        }).progress(new ProgressCallback<P>() {
            public void onProgress(P p) {
                if (progressPipe != null) {
                    PipedPromise.this.pipe(progressPipe.pipeProgress(p));
                } else {
                    PipedPromise.this.notify(p);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public Promise<D_OUT, F_OUT, P_OUT> pipe(Promise<D_OUT, F_OUT, P_OUT> promise) {
        promise.done(new DoneCallback<D_OUT>() {
            public void onDone(D_OUT d_out) {
                PipedPromise.this.resolve(d_out);
            }
        }).fail(new FailCallback<F_OUT>() {
            public void onFail(F_OUT f_out) {
                PipedPromise.this.reject(f_out);
            }
        }).progress(new ProgressCallback<P_OUT>() {
            public void onProgress(P_OUT p_out) {
                PipedPromise.this.notify(p_out);
            }
        });
        return promise;
    }
}
