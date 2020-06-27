package com.android.launcher3;

import android.animation.TimeInterpolator;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import com.android.launcher3.util.TouchController;

public class PinchToOverviewListener extends SimpleOnScaleGestureListener implements TouchController {
    private static final float FLING_VELOCITY = 0.003f;
    private static final float OVERVIEW_PROGRESS = 0.0f;
    private static final float WORKSPACE_PROGRESS = 1.0f;
    private PinchAnimationManager mAnimationManager;
    private TimeInterpolator mInterpolator;
    private Launcher mLauncher;
    private boolean mPinchCanceled = false;
    private ScaleGestureDetector mPinchDetector;
    private boolean mPinchStarted = false;
    private float mPreviousProgress;
    private long mPreviousTimeMillis;
    private float mProgressDelta;
    private PinchThresholdManager mThresholdManager;
    private long mTimeDelta;
    private Workspace mWorkspace = null;

    public PinchToOverviewListener(Launcher launcher) {
        this.mLauncher = launcher;
        this.mPinchDetector = new ScaleGestureDetector(this.mLauncher, this);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        this.mPinchDetector.onTouchEvent(motionEvent);
        return this.mPinchStarted;
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        if (this.mPinchStarted) {
            if (motionEvent.getPointerCount() <= 2) {
                return this.mPinchDetector.onTouchEvent(motionEvent);
            }
            cancelPinch(this.mPreviousProgress, -1);
        }
        return false;
    }

    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        if (this.mLauncher.mState != State.WORKSPACE || this.mLauncher.isOnCustomContent()) {
            return false;
        }
        if ((this.mAnimationManager != null && this.mAnimationManager.isAnimating()) || this.mLauncher.isWorkspaceLocked()) {
            return false;
        }
        if (this.mWorkspace == null) {
            this.mWorkspace = this.mLauncher.getWorkspace();
            this.mThresholdManager = new PinchThresholdManager(this.mWorkspace);
            this.mAnimationManager = new PinchAnimationManager(this.mLauncher);
        }
        if (this.mWorkspace.isSwitchingState() || this.mWorkspace.mScrollInteractionBegan || AbstractFloatingView.getTopOpenView(this.mLauncher) != null) {
            return false;
        }
        this.mPreviousProgress = this.mWorkspace.isInOverviewMode() ? 0.0f : 1.0f;
        this.mPreviousTimeMillis = System.currentTimeMillis();
        this.mInterpolator = this.mWorkspace.isInOverviewMode() ? new LogDecelerateInterpolator(100, 0) : new LogAccelerateInterpolator(100, 0);
        this.mPinchStarted = true;
        this.mWorkspace.onPrepareStateTransition(true);
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        super.onScaleEnd(scaleGestureDetector);
        float f = this.mProgressDelta / ((float) this.mTimeDelta);
        float passedThreshold = this.mThresholdManager.getPassedThreshold();
        boolean z = true;
        if (((this.mWorkspace.isInOverviewMode() && f >= FLING_VELOCITY) || (!this.mWorkspace.isInOverviewMode() && f <= -0.003f)) || passedThreshold >= 0.4f) {
            z = false;
        }
        float f2 = this.mPreviousProgress;
        float f3 = 1.0f;
        if (this.mWorkspace.isInOverviewMode() || z) {
            f2 = 1.0f - this.mPreviousProgress;
        }
        int computeDuration = computeDuration(f2, f);
        if (z) {
            cancelPinch(this.mPreviousProgress, computeDuration);
        } else if (passedThreshold < 0.95f) {
            if (!this.mWorkspace.isInOverviewMode()) {
                f3 = 0.0f;
            }
            this.mAnimationManager.animateToProgress(this.mPreviousProgress, f3, computeDuration, this.mThresholdManager);
        } else {
            this.mThresholdManager.reset();
            this.mWorkspace.onEndStateTransition();
        }
        this.mPinchStarted = false;
        this.mPinchCanceled = false;
    }

    private int computeDuration(float f, float f2) {
        return Math.min((int) (f / Math.abs(f2)), this.mAnimationManager.getNormalOverviewTransitionDuration());
    }

    private void cancelPinch(float f, int i) {
        if (!this.mPinchCanceled) {
            this.mPinchCanceled = true;
            this.mAnimationManager.animateToProgress(f, this.mWorkspace.isInOverviewMode() ? 0.0f : 1.0f, i, this.mThresholdManager);
            this.mPinchStarted = false;
        }
    }

    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        if (this.mThresholdManager.getPassedThreshold() == 0.95f) {
            return true;
        }
        if (this.mLauncher.getDragController().isDragging()) {
            this.mLauncher.getDragController().cancelDrag();
        }
        float currentSpan = scaleGestureDetector.getCurrentSpan() - scaleGestureDetector.getPreviousSpan();
        if ((currentSpan < 0.0f && this.mWorkspace.isInOverviewMode()) || (currentSpan > 0.0f && !this.mWorkspace.isInOverviewMode())) {
            return false;
        }
        int width = this.mWorkspace.getWidth();
        float overviewModeShrinkFactor = this.mWorkspace.getOverviewModeShrinkFactor();
        float interpolation = this.mInterpolator.getInterpolation((Math.max(overviewModeShrinkFactor, Math.min((this.mWorkspace.isInOverviewMode() ? overviewModeShrinkFactor : 1.0f) + (currentSpan / ((float) width)), 1.0f)) - overviewModeShrinkFactor) / (1.0f - overviewModeShrinkFactor));
        this.mAnimationManager.setAnimationProgress(interpolation);
        if (this.mThresholdManager.updateAndAnimatePassedThreshold(interpolation, this.mAnimationManager) == 0.95f) {
            return true;
        }
        this.mProgressDelta = interpolation - this.mPreviousProgress;
        this.mPreviousProgress = interpolation;
        this.mTimeDelta = System.currentTimeMillis() - this.mPreviousTimeMillis;
        this.mPreviousTimeMillis = System.currentTimeMillis();
        return false;
    }
}
