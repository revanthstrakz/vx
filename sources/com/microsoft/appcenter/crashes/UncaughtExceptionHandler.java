package com.microsoft.appcenter.crashes;

import android.support.annotation.VisibleForTesting;
import com.microsoft.appcenter.utils.ShutdownHelper;

class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    private java.lang.Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    private boolean mIgnoreDefaultExceptionHandler = false;

    UncaughtExceptionHandler() {
    }

    public void uncaughtException(Thread thread, Throwable th) {
        Crashes.getInstance().saveUncaughtException(thread, th);
        if (this.mDefaultUncaughtExceptionHandler != null) {
            this.mDefaultUncaughtExceptionHandler.uncaughtException(thread, th);
        } else {
            ShutdownHelper.shutdown(10);
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setIgnoreDefaultExceptionHandler(boolean z) {
        this.mIgnoreDefaultExceptionHandler = z;
        if (z) {
            this.mDefaultUncaughtExceptionHandler = null;
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public java.lang.Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
        return this.mDefaultUncaughtExceptionHandler;
    }

    /* access modifiers changed from: 0000 */
    public void register() {
        if (!this.mIgnoreDefaultExceptionHandler) {
            this.mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        } else {
            this.mDefaultUncaughtExceptionHandler = null;
        }
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /* access modifiers changed from: 0000 */
    public void unregister() {
        Thread.setDefaultUncaughtExceptionHandler(this.mDefaultUncaughtExceptionHandler);
    }
}
