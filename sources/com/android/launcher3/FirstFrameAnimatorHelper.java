package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver.OnDrawListener;
import p013io.virtualapp.utils.HanziToPinyin.Token;

public class FirstFrameAnimatorHelper extends AnimatorListenerAdapter implements AnimatorUpdateListener {
    private static final boolean DEBUG = false;
    private static final int IDEAL_FRAME_DURATION = 16;
    private static final int MAX_DELAY = 1000;
    private static final String TAG = "FirstFrameAnimatorHlpr";
    private static OnDrawListener sGlobalDrawListener;
    static long sGlobalFrameCounter;
    private static boolean sVisible;
    private boolean mAdjustedSecondFrameTime;
    private boolean mHandlingOnAnimationUpdate;
    private long mStartFrame;
    private long mStartTime = -1;
    private final View mTarget;

    public FirstFrameAnimatorHelper(ValueAnimator valueAnimator, View view) {
        this.mTarget = view;
        valueAnimator.addUpdateListener(this);
    }

    public FirstFrameAnimatorHelper(ViewPropertyAnimator viewPropertyAnimator, View view) {
        this.mTarget = view;
        viewPropertyAnimator.setListener(this);
    }

    public void onAnimationStart(Animator animator) {
        ValueAnimator valueAnimator = (ValueAnimator) animator;
        valueAnimator.addUpdateListener(this);
        onAnimationUpdate(valueAnimator);
    }

    public static void setIsVisible(boolean z) {
        sVisible = z;
    }

    public static void initializeDrawListener(View view) {
        if (sGlobalDrawListener != null) {
            view.getViewTreeObserver().removeOnDrawListener(sGlobalDrawListener);
        }
        sGlobalDrawListener = new OnDrawListener() {
            private long mTime = System.currentTimeMillis();

            public void onDraw() {
                FirstFrameAnimatorHelper.sGlobalFrameCounter++;
            }
        };
        view.getViewTreeObserver().addOnDrawListener(sGlobalDrawListener);
        sVisible = true;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        final ValueAnimator valueAnimator2 = valueAnimator;
        long currentTimeMillis = System.currentTimeMillis();
        if (this.mStartTime == -1) {
            this.mStartFrame = sGlobalFrameCounter;
            this.mStartTime = currentTimeMillis;
        }
        long currentPlayTime = valueAnimator.getCurrentPlayTime();
        boolean z = Float.compare(1.0f, valueAnimator.getAnimatedFraction()) == 0;
        if (!this.mHandlingOnAnimationUpdate && sVisible && currentPlayTime < valueAnimator.getDuration() && !z) {
            this.mHandlingOnAnimationUpdate = true;
            long j = sGlobalFrameCounter - this.mStartFrame;
            if (j != 0 || currentTimeMillis >= this.mStartTime + 1000 || currentPlayTime <= 0) {
                int i = (j > 1 ? 1 : (j == 1 ? 0 : -1));
                if (i == 0 && currentTimeMillis < this.mStartTime + 1000 && !this.mAdjustedSecondFrameTime && currentTimeMillis > this.mStartTime + 16 && currentPlayTime > 16) {
                    valueAnimator2.setCurrentPlayTime(16);
                    this.mAdjustedSecondFrameTime = true;
                } else if (i > 0) {
                    this.mTarget.post(new Runnable() {
                        public void run() {
                            valueAnimator2.removeUpdateListener(FirstFrameAnimatorHelper.this);
                        }
                    });
                }
            } else {
                this.mTarget.getRootView().invalidate();
                valueAnimator2.setCurrentPlayTime(0);
            }
            this.mHandlingOnAnimationUpdate = false;
        }
    }

    public void print(ValueAnimator valueAnimator) {
        float currentPlayTime = ((float) valueAnimator.getCurrentPlayTime()) / ((float) valueAnimator.getDuration());
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(sGlobalFrameCounter);
        sb.append("(");
        sb.append(sGlobalFrameCounter - this.mStartFrame);
        sb.append(") ");
        sb.append(this.mTarget);
        sb.append(" dirty? ");
        sb.append(this.mTarget.isDirty());
        sb.append(Token.SEPARATOR);
        sb.append(currentPlayTime);
        sb.append(Token.SEPARATOR);
        sb.append(this);
        sb.append(Token.SEPARATOR);
        sb.append(valueAnimator);
        Log.d(str, sb.toString());
    }
}
