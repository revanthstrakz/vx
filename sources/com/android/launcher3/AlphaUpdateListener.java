package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;

/* compiled from: WorkspaceStateTransitionAnimation */
class AlphaUpdateListener extends AnimatorListenerAdapter implements AnimatorUpdateListener {
    private static final float ALPHA_CUTOFF_THRESHOLD = 0.01f;
    private boolean mAccessibilityEnabled;
    private boolean mCanceled = false;
    private View mView;

    public AlphaUpdateListener(View view, boolean z) {
        this.mView = view;
        this.mAccessibilityEnabled = z;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        updateVisibility(this.mView, this.mAccessibilityEnabled);
    }

    public static void updateVisibility(View view, boolean z) {
        int i = z ? 8 : 4;
        if (view.getAlpha() < ALPHA_CUTOFF_THRESHOLD && view.getVisibility() != i) {
            view.setVisibility(i);
        } else if (view.getAlpha() > ALPHA_CUTOFF_THRESHOLD && view.getVisibility() != 0) {
            view.setVisibility(0);
        }
    }

    public void onAnimationCancel(Animator animator) {
        this.mCanceled = true;
    }

    public void onAnimationEnd(Animator animator) {
        if (!this.mCanceled) {
            updateVisibility(this.mView, this.mAccessibilityEnabled);
        }
    }

    public void onAnimationStart(Animator animator) {
        this.mView.setVisibility(0);
    }
}
