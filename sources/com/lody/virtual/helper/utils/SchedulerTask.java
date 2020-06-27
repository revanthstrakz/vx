package com.lody.virtual.helper.utils;

import android.os.Handler;

public abstract class SchedulerTask implements Runnable {
    /* access modifiers changed from: private */
    public long mDelay;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private final Runnable mInnerRunnable = new Runnable() {
        public void run() {
            SchedulerTask.this.run();
            if (SchedulerTask.this.mDelay > 0) {
                SchedulerTask.this.mHandler.postDelayed(this, SchedulerTask.this.mDelay);
            }
        }
    };

    public SchedulerTask(Handler handler, long j) {
        this.mHandler = handler;
        this.mDelay = j;
    }

    public void schedule() {
        this.mHandler.post(this.mInnerRunnable);
    }

    public void cancel() {
        this.mHandler.removeCallbacks(this.mInnerRunnable);
    }
}
