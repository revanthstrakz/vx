package com.android.launcher3.allapps;

import android.animation.ObjectAnimator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.pageindicators.CaretDrawable;

public class AllAppsCaretController {
    private static final float CARET_THRESHOLD = 0.015f;
    private static final float CARET_THRESHOLD_LAND = 0.5f;
    private static final float PEAK_VELOCITY = 0.7f;
    private ObjectAnimator mCaretAnimator = ObjectAnimator.ofFloat(this.mCaretDrawable, "caretProgress", new float[]{0.0f});
    private CaretDrawable mCaretDrawable;
    private float mLastCaretProgress;
    private Launcher mLauncher;
    private boolean mThresholdCrossed;

    public AllAppsCaretController(CaretDrawable caretDrawable, Launcher launcher) {
        this.mLauncher = launcher;
        this.mCaretDrawable = caretDrawable;
        long integer = (long) launcher.getResources().getInteger(C0622R.integer.config_caretAnimationDuration);
        Interpolator loadInterpolator = AnimationUtils.loadInterpolator(launcher, AndroidResources.FAST_OUT_SLOW_IN);
        this.mCaretAnimator.setDuration(integer);
        this.mCaretAnimator.setInterpolator(loadInterpolator);
    }

    public void updateCaret(float f, float f2, boolean z) {
        if (getThreshold() < f && f < 1.0f - getThreshold() && !this.mLauncher.useVerticalBarLayout()) {
            this.mThresholdCrossed = true;
            float max = Math.max(-1.0f, Math.min(f2 / 0.7f, 1.0f));
            this.mCaretDrawable.setCaretProgress(max);
            this.mLastCaretProgress = max;
            animateCaretToProgress(0.0f);
        } else if (z) {
        } else {
            if (f <= getThreshold()) {
                animateCaretToProgress(1.0f);
            } else if (f >= 1.0f - getThreshold()) {
                animateCaretToProgress(-1.0f);
            }
        }
    }

    private void animateCaretToProgress(float f) {
        if (Float.compare(this.mLastCaretProgress, f) != 0) {
            if (this.mCaretAnimator.isRunning()) {
                this.mCaretAnimator.cancel();
            }
            this.mLastCaretProgress = f;
            this.mCaretAnimator.setFloatValues(new float[]{f});
            this.mCaretAnimator.start();
        }
    }

    private float getThreshold() {
        if (this.mLauncher.useVerticalBarLayout()) {
            return 0.5f;
        }
        return this.mThresholdCrossed ? CARET_THRESHOLD : 0.0f;
    }

    public void onDragStart() {
        this.mThresholdCrossed = false;
    }
}
