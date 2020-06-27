package org.jdeferred.android;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import java.util.concurrent.ExecutorService;
import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DeferredManager.StartPolicy;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;

public class AndroidDeferredManager extends DefaultDeferredManager {
    private static Void[] EMPTY_PARAMS = new Void[0];

    public AndroidDeferredManager() {
    }

    public AndroidDeferredManager(ExecutorService executorService) {
        super(executorService);
    }

    @SuppressLint({"NewApi"})
    public <Progress, Result> Promise<Result, Throwable, Progress> when(DeferredAsyncTask<Void, Progress, Result> deferredAsyncTask) {
        if (deferredAsyncTask.getStartPolicy() == StartPolicy.AUTO || (deferredAsyncTask.getStartPolicy() == StartPolicy.DEFAULT && isAutoSubmit())) {
            if (VERSION.SDK_INT >= 11) {
                deferredAsyncTask.executeOnExecutor(getExecutorService(), EMPTY_PARAMS);
            } else {
                deferredAsyncTask.execute(EMPTY_PARAMS);
            }
        }
        return deferredAsyncTask.promise();
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(DeferredAsyncTask<Void, ?, ?>... deferredAsyncTaskArr) {
        assertNotEmpty(deferredAsyncTaskArr);
        Promise[] promiseArr = new Promise[deferredAsyncTaskArr.length];
        for (int i = 0; i < deferredAsyncTaskArr.length; i++) {
            promiseArr[i] = when(deferredAsyncTaskArr[i]);
        }
        return when(promiseArr);
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(AndroidExecutionScope androidExecutionScope, DeferredAsyncTask<Void, ?, ?>... deferredAsyncTaskArr) {
        assertNotEmpty(deferredAsyncTaskArr);
        Promise[] promiseArr = new Promise[deferredAsyncTaskArr.length];
        for (int i = 0; i < deferredAsyncTaskArr.length; i++) {
            promiseArr[i] = when(deferredAsyncTaskArr[i]);
        }
        return when(androidExecutionScope, promiseArr);
    }

    public <D, P> Promise<D, Throwable, P> when(DeferredFutureTask<D, P> deferredFutureTask) {
        return new AndroidDeferredObject(super.when((DeferredFutureTask) deferredFutureTask)).promise();
    }

    public <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise) {
        if (promise instanceof AndroidDeferredObject) {
            return promise;
        }
        return new AndroidDeferredObject(promise).promise();
    }

    public <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise, AndroidExecutionScope androidExecutionScope) {
        if (promise instanceof AndroidDeferredObject) {
            return promise;
        }
        return new AndroidDeferredObject(promise, androidExecutionScope).promise();
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(Promise... promiseArr) {
        return new AndroidDeferredObject(super.when(promiseArr)).promise();
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(AndroidExecutionScope androidExecutionScope, Promise... promiseArr) {
        return new AndroidDeferredObject(super.when(promiseArr), androidExecutionScope).promise();
    }
}
