package com.android.launcher3;

import android.os.Handler;
import android.os.SystemClock;

public class Alarm implements Runnable {
    private OnAlarmListener mAlarmListener;
    private boolean mAlarmPending = false;
    private long mAlarmTriggerTime;
    private Handler mHandler = new Handler();
    private boolean mWaitingForCallback;

    public void setOnAlarmListener(OnAlarmListener onAlarmListener) {
        this.mAlarmListener = onAlarmListener;
    }

    public void setAlarm(long j) {
        long uptimeMillis = SystemClock.uptimeMillis();
        this.mAlarmPending = true;
        long j2 = this.mAlarmTriggerTime;
        this.mAlarmTriggerTime = j + uptimeMillis;
        if (this.mWaitingForCallback && j2 > this.mAlarmTriggerTime) {
            this.mHandler.removeCallbacks(this);
            this.mWaitingForCallback = false;
        }
        if (!this.mWaitingForCallback) {
            this.mHandler.postDelayed(this, this.mAlarmTriggerTime - uptimeMillis);
            this.mWaitingForCallback = true;
        }
    }

    public void cancelAlarm() {
        this.mAlarmPending = false;
    }

    public void run() {
        this.mWaitingForCallback = false;
        if (this.mAlarmPending) {
            long uptimeMillis = SystemClock.uptimeMillis();
            if (this.mAlarmTriggerTime > uptimeMillis) {
                this.mHandler.postDelayed(this, Math.max(0, this.mAlarmTriggerTime - uptimeMillis));
                this.mWaitingForCallback = true;
                return;
            }
            this.mAlarmPending = false;
            if (this.mAlarmListener != null) {
                this.mAlarmListener.onAlarm(this);
            }
        }
    }

    public boolean alarmPending() {
        return this.mAlarmPending;
    }
}
