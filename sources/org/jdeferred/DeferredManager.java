package org.jdeferred;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;

public interface DeferredManager {

    public enum StartPolicy {
        DEFAULT,
        AUTO,
        MANAUL
    }

    Promise<Void, Throwable, Void> when(Runnable runnable);

    <D> Promise<D, Throwable, Void> when(Callable<D> callable);

    <D> Promise<D, Throwable, Void> when(Future<D> future);

    <D, P> Promise<D, Throwable, P> when(DeferredCallable<D, P> deferredCallable);

    <D, P> Promise<D, Throwable, P> when(DeferredFutureTask<D, P> deferredFutureTask);

    <P> Promise<Void, Throwable, P> when(DeferredRunnable<P> deferredRunnable);

    <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise);

    Promise<MultipleResults, OneReject, MasterProgress> when(Runnable... runnableArr);

    Promise<MultipleResults, OneReject, MasterProgress> when(Callable<?>... callableArr);

    Promise<MultipleResults, OneReject, MasterProgress> when(Future<?>... futureArr);

    Promise<MultipleResults, OneReject, MasterProgress> when(DeferredCallable<?, ?>... deferredCallableArr);

    Promise<MultipleResults, OneReject, MasterProgress> when(DeferredFutureTask<?, ?>... deferredFutureTaskArr);

    Promise<MultipleResults, OneReject, MasterProgress> when(DeferredRunnable<?>... deferredRunnableArr);

    Promise<MultipleResults, OneReject, MasterProgress> when(Promise... promiseArr);
}
