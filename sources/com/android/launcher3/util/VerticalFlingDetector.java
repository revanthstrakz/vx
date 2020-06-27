package com.android.launcher3.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;

public class VerticalFlingDetector implements OnTouchListener {
    private static final float CUSTOM_SLOP_MULTIPLIER = 2.2f;
    private static final int SEC_IN_MILLIS = 1000;
    private double mCustomTouchSlop;
    private float mDownX;
    private float mDownY;
    private float mMaximumFlingVelocity;
    private float mMinimumFlingVelocity;
    private boolean mShouldCheckFling;
    private VelocityTracker mVelocityTracker;

    public VerticalFlingDetector(Context context) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mMinimumFlingVelocity = (float) viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumFlingVelocity = (float) viewConfiguration.getScaledMaximumFlingVelocity();
        this.mCustomTouchSlop = (double) (((float) viewConfiguration.getScaledTouchSlop()) * CUSTOM_SLOP_MULTIPLIER);
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        switch (motionEvent.getAction()) {
            case 0:
                this.mDownX = motionEvent.getX();
                this.mDownY = motionEvent.getY();
                this.mShouldCheckFling = false;
                break;
            case 1:
                if (this.mShouldCheckFling) {
                    this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                    if (this.mVelocityTracker.getYVelocity() > this.mMinimumFlingVelocity) {
                        cleanUp();
                        return true;
                    }
                }
                break;
            case 2:
                if (!this.mShouldCheckFling && ((double) Math.abs(motionEvent.getY() - this.mDownY)) > this.mCustomTouchSlop && Math.abs(motionEvent.getY() - this.mDownY) > Math.abs(motionEvent.getX() - this.mDownX)) {
                    this.mShouldCheckFling = true;
                    break;
                }
            case 3:
                break;
        }
        cleanUp();
        return false;
    }

    private void cleanUp() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
}
