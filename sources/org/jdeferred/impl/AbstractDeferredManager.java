package org.jdeferred.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.jdeferred.DeferredCallable;
import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DeferredManager;
import org.jdeferred.DeferredManager.StartPolicy;
import org.jdeferred.DeferredRunnable;
import org.jdeferred.Promise;
import org.jdeferred.multiple.MasterDeferredObject;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDeferredManager implements DeferredManager {
    protected final Logger log = LoggerFactory.getLogger(AbstractDeferredManager.class);

    public abstract boolean isAutoSubmit();

    /* access modifiers changed from: protected */
    public abstract void submit(Runnable runnable);

    /* access modifiers changed from: protected */
    public abstract void submit(Callable callable);

    public <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise) {
        return promise;
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(Runnable... runnableArr) {
        assertNotEmpty(runnableArr);
        Promise[] promiseArr = new Promise[runnableArr.length];
        for (int i = 0; i < runnableArr.length; i++) {
            if (runnableArr[i] instanceof DeferredRunnable) {
                promiseArr[i] = when(runnableArr[i]);
            } else {
                promiseArr[i] = when(runnableArr[i]);
            }
        }
        return when(promiseArr);
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(Callable<?>... callableArr) {
        assertNotEmpty(callableArr);
        Promise[] promiseArr = new Promise[callableArr.length];
        for (int i = 0; i < callableArr.length; i++) {
            if (callableArr[i] instanceof DeferredCallable) {
                promiseArr[i] = when(callableArr[i]);
            } else {
                promiseArr[i] = when(callableArr[i]);
            }
        }
        return when(promiseArr);
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(DeferredRunnable<?>... deferredRunnableArr) {
        assertNotEmpty(deferredRunnableArr);
        Promise[] promiseArr = new Promise[deferredRunnableArr.length];
        for (int i = 0; i < deferredRunnableArr.length; i++) {
            promiseArr[i] = when(deferredRunnableArr[i]);
        }
        return when(promiseArr);
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(DeferredCallable<?, ?>... deferredCallableArr) {
        assertNotEmpty(deferredCallableArr);
        Promise[] promiseArr = new Promise[deferredCallableArr.length];
        for (int i = 0; i < deferredCallableArr.length; i++) {
            promiseArr[i] = when(deferredCallableArr[i]);
        }
        return when(promiseArr);
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(DeferredFutureTask<?, ?>... deferredFutureTaskArr) {
        assertNotEmpty(deferredFutureTaskArr);
        Promise[] promiseArr = new Promise[deferredFutureTaskArr.length];
        for (int i = 0; i < deferredFutureTaskArr.length; i++) {
            promiseArr[i] = when(deferredFutureTaskArr[i]);
        }
        return when(promiseArr);
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(Future<?>... futureArr) {
        assertNotEmpty(futureArr);
        Promise[] promiseArr = new Promise[futureArr.length];
        for (int i = 0; i < futureArr.length; i++) {
            promiseArr[i] = when(futureArr[i]);
        }
        return when(promiseArr);
    }

    public Promise<MultipleResults, OneReject, MasterProgress> when(Promise... promiseArr) {
        assertNotEmpty(promiseArr);
        return new MasterDeferredObject(promiseArr).promise();
    }

    public <P> Promise<Void, Throwable, P> when(DeferredRunnable<P> deferredRunnable) {
        return when(new DeferredFutureTask<>(deferredRunnable));
    }

    public <D, P> Promise<D, Throwable, P> when(DeferredCallable<D, P> deferredCallable) {
        return when(new DeferredFutureTask<>(deferredCallable));
    }

    public Promise<Void, Throwable, Void> when(Runnable runnable) {
        return when(new DeferredFutureTask<>(runnable));
    }

    public <D> Promise<D, Throwable, Void> when(Callable<D> callable) {
        return when(new DeferredFutureTask<>(callable));
    }

    public <D, P> Promise<D, Throwable, P> when(DeferredFutureTask<D, P> deferredFutureTask) {
        if (deferredFutureTask.getStartPolicy() == StartPolicy.AUTO || (deferredFutureTask.getStartPolicy() == StartPolicy.DEFAULT && isAutoSubmit())) {
            submit((Runnable) deferredFutureTask);
        }
        return deferredFutureTask.promise();
    }

    public <D> Promise<D, Throwable, Void> when(final Future<D> future) {
        return when((DeferredCallable<D, P>) new DeferredCallable<D, Void>(StartPolicy.AUTO) {
            public D call() throws Exception {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    throw e;
                } catch (ExecutionException e2) {
                    if (e2.getCause() instanceof Exception) {
                        throw ((Exception) e2.getCause());
                    }
                    throw e2;
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void assertNotEmpty(Object[] objArr) {
        if (objArr == null || objArr.length == 0) {
            throw new IllegalArgumentException("Arguments is null or its length is empty");
        }
    }
}
