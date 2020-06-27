package org.jdeferred.android;

import android.os.AsyncTask;
import java.util.concurrent.CancellationException;
import org.jdeferred.DeferredManager.StartPolicy;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DeferredAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private final DeferredObject<Result, Throwable, Progress> deferred;
    protected final Logger log;
    private final StartPolicy startPolicy;
    private Throwable throwable;

    /* access modifiers changed from: protected */
    public abstract Result doInBackgroundSafe(Params... paramsArr) throws Exception;

    public DeferredAsyncTask() {
        this.log = LoggerFactory.getLogger(DeferredAsyncTask.class);
        this.deferred = new DeferredObject<>();
        this.startPolicy = StartPolicy.DEFAULT;
    }

    public DeferredAsyncTask(StartPolicy startPolicy2) {
        this.log = LoggerFactory.getLogger(DeferredAsyncTask.class);
        this.deferred = new DeferredObject<>();
        this.startPolicy = startPolicy2;
    }

    /* access modifiers changed from: protected */
    public final void onCancelled() {
        this.deferred.reject(new CancellationException());
    }

    /* access modifiers changed from: protected */
    public final void onCancelled(Result result) {
        this.deferred.reject(new CancellationException());
    }

    /* access modifiers changed from: protected */
    public final void onPostExecute(Result result) {
        if (this.throwable != null) {
            this.deferred.reject(this.throwable);
        } else {
            this.deferred.resolve(result);
        }
    }

    /* access modifiers changed from: protected */
    public final void onProgressUpdate(Progress... progressArr) {
        if (progressArr == null || progressArr.length == 0) {
            this.deferred.notify(null);
        } else if (progressArr.length > 0) {
            this.log.warn("There were multiple progress values.  Only the first one was used!");
            this.deferred.notify(progressArr[0]);
        }
    }

    /* access modifiers changed from: protected */
    public final Result doInBackground(Params... paramsArr) {
        try {
            return doInBackgroundSafe(paramsArr);
        } catch (Throwable th) {
            this.throwable = th;
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public final void notify(Progress progress) {
        publishProgress(new Object[]{progress});
    }

    public Promise<Result, Throwable, Progress> promise() {
        return this.deferred.promise();
    }

    public StartPolicy getStartPolicy() {
        return this.startPolicy;
    }
}
