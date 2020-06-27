package com.android.launcher3;

import android.animation.TimeInterpolator;

/* compiled from: WorkspaceStateTransitionAnimation */
class ZInterpolator implements TimeInterpolator {
    private float focalLength;

    public ZInterpolator(float f) {
        this.focalLength = f;
    }

    public float getInterpolation(float f) {
        return (1.0f - (this.focalLength / (this.focalLength + f))) / (1.0f - (this.focalLength / (this.focalLength + 1.0f)));
    }
}
