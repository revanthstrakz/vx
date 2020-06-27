package org.jdeferred.impl;

import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.Promise;

public class FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT> extends DeferredObject<D_OUT, F_OUT, P_OUT> implements Promise<D_OUT, F_OUT, P_OUT> {
    protected static final NoOpDoneFilter NO_OP_DONE_FILTER = new NoOpDoneFilter();
    protected static final NoOpFailFilter NO_OP_FAIL_FILTER = new NoOpFailFilter();
    protected static final NoOpProgressFilter NO_OP_PROGRESS_FILTER = new NoOpProgressFilter();
    /* access modifiers changed from: private */
    public final DoneFilter<D, D_OUT> doneFilter;
    /* access modifiers changed from: private */
    public final FailFilter<F, F_OUT> failFilter;
    /* access modifiers changed from: private */
    public final ProgressFilter<P, P_OUT> progressFilter;

    public static final class NoOpDoneFilter<D> implements DoneFilter<D, D> {
        public D filterDone(D d) {
            return d;
        }
    }

    public static final class NoOpFailFilter<F> implements FailFilter<F, F> {
        public F filterFail(F f) {
            return f;
        }
    }

    public static final class NoOpProgressFilter<P> implements ProgressFilter<P, P> {
        public P filterProgress(P p) {
            return p;
        }
    }

    public FilteredPromise(Promise<D, F, P> promise, DoneFilter<D, D_OUT> doneFilter2, FailFilter<F, F_OUT> failFilter2, ProgressFilter<P, P_OUT> progressFilter2) {
        if (doneFilter2 == null) {
            doneFilter2 = NO_OP_DONE_FILTER;
        }
        this.doneFilter = doneFilter2;
        if (failFilter2 == null) {
            failFilter2 = NO_OP_FAIL_FILTER;
        }
        this.failFilter = failFilter2;
        if (progressFilter2 == null) {
            progressFilter2 = NO_OP_PROGRESS_FILTER;
        }
        this.progressFilter = progressFilter2;
        promise.done(new DoneCallback<D>() {
            public void onDone(D d) {
                FilteredPromise.this.resolve(FilteredPromise.this.doneFilter.filterDone(d));
            }
        }).fail(new FailCallback<F>() {
            public void onFail(F f) {
                FilteredPromise.this.reject(FilteredPromise.this.failFilter.filterFail(f));
            }
        }).progress(new ProgressCallback<P>() {
            public void onProgress(P p) {
                FilteredPromise.this.notify(FilteredPromise.this.progressFilter.filterProgress(p));
            }
        });
    }
}
