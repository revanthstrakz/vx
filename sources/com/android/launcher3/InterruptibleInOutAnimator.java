package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;

public class InterruptibleInOutAnimator {

    /* renamed from: IN */
    private static final int f51IN = 1;
    private static final int OUT = 2;
    private static final int STOPPED = 0;
    private ValueAnimator mAnimator;
    int mDirection = 0;
    private boolean mFirstRun = true;
    private long mOriginalDuration;
    private float mOriginalFromValue;
    private float mOriginalToValue;
    private Object mTag = null;

    public InterruptibleInOutAnimator(View view, long j, float f, float f2) {
        this.mAnimator = LauncherAnimUtils.ofFloat(f, f2).setDuration(j);
        this.mOriginalDuration = j;
        this.mOriginalFromValue = f;
        this.mOriginalToValue = f2;
        this.mAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                InterruptibleInOutAnimator.this.mDirection = 0;
            }
        });
    }

    private void animate(int i) {
        float f;
        long currentPlayTime = this.mAnimator.getCurrentPlayTime();
        float f2 = i == 1 ? this.mOriginalToValue : this.mOriginalFromValue;
        if (this.mFirstRun) {
            f = this.mOriginalFromValue;
        } else {
            f = ((Float) this.mAnimator.getAnimatedValue()).floatValue();
        }
        cancel();
        this.mDirection = i;
        this.mAnimator.setDuration(Math.max(0, Math.min(this.mOriginalDuration - currentPlayTime, this.mOriginalDuration)));
        this.mAnimator.setFloatValues(new float[]{f, f2});
        this.mAnimator.start();
        this.mFirstRun = false;
    }

    public void cancel() {
        this.mAnimator.cancel();
        this.mDirection = 0;
    }

    public void end() {
        this.mAnimator.end();
        this.mDirection = 0;
    }

    public boolean isStopped() {
        return this.mDirection == 0;
    }

    public void animateIn() {
        animate(1);
    }

    public void animateOut() {
        animate(2);
    }

    public void setTag(Object obj) {
        this.mTag = obj;
    }

    public Object getTag() {
        return this.mTag;
    }

    public ValueAnimator getAnimator() {
        return this.mAnimator;
    }
}
