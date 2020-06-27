package org.jdeferred.impl;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DefaultDeferredManager extends AbstractDeferredManager {
    public static final boolean DEFAULT_AUTO_SUBMIT = true;
    private boolean autoSubmit;
    private final ExecutorService executorService;

    public DefaultDeferredManager() {
        this.autoSubmit = true;
        this.executorService = Executors.newCachedThreadPool();
    }

    public DefaultDeferredManager(ExecutorService executorService2) {
        this.autoSubmit = true;
        this.executorService = executorService2;
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public boolean awaitTermination(long j, TimeUnit timeUnit) throws InterruptedException {
        return this.executorService.awaitTermination(j, timeUnit);
    }

    public boolean isShutdown() {
        return this.executorService.isShutdown();
    }

    public boolean isTerminated() {
        return this.executorService.isTerminated();
    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return this.executorService.shutdownNow();
    }

    /* access modifiers changed from: protected */
    public void submit(Runnable runnable) {
        this.executorService.submit(runnable);
    }

    /* access modifiers changed from: protected */
    public void submit(Callable callable) {
        this.executorService.submit(callable);
    }

    public boolean isAutoSubmit() {
        return this.autoSubmit;
    }

    public void setAutoSubmit(boolean z) {
        this.autoSubmit = z;
    }
}
