package com.android.launcher3;

import com.android.launcher3.Workspace.State;

public class PinchThresholdManager {
    public static final float THRESHOLD_ONE = 0.4f;
    public static final float THRESHOLD_THREE = 0.95f;
    public static final float THRESHOLD_TWO = 0.7f;
    public static final float THRESHOLD_ZERO = 0.0f;
    private float mPassedThreshold = 0.0f;
    private Workspace mWorkspace;

    public PinchThresholdManager(Workspace workspace) {
        this.mWorkspace = workspace;
    }

    public float updateAndAnimatePassedThreshold(float f, PinchAnimationManager pinchAnimationManager) {
        if (!this.mWorkspace.isInOverviewMode()) {
            f = 1.0f - f;
        }
        float f2 = this.mPassedThreshold;
        if (f < 0.4f) {
            this.mPassedThreshold = 0.0f;
        } else if (f < 0.7f) {
            this.mPassedThreshold = 0.4f;
        } else if (f < 0.95f) {
            this.mPassedThreshold = 0.7f;
        } else {
            this.mPassedThreshold = 0.95f;
        }
        if (this.mPassedThreshold != f2) {
            State state = this.mWorkspace.isInOverviewMode() ? State.OVERVIEW : State.NORMAL;
            State state2 = this.mWorkspace.isInOverviewMode() ? State.NORMAL : State.OVERVIEW;
            float f3 = this.mPassedThreshold;
            if (this.mPassedThreshold < f2) {
                state2 = state;
            } else {
                f2 = f3;
            }
            pinchAnimationManager.animateThreshold(f2, state, state2);
        }
        return this.mPassedThreshold;
    }

    public float getPassedThreshold() {
        return this.mPassedThreshold;
    }

    public void reset() {
        this.mPassedThreshold = 0.0f;
    }
}
