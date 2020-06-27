package com.android.launcher3;

import android.animation.TimeInterpolator;

/* compiled from: WorkspaceStateTransitionAnimation */
class InverseZInterpolator implements TimeInterpolator {
    private ZInterpolator zInterpolator;

    public InverseZInterpolator(float f) {
        this.zInterpolator = new ZInterpolator(f);
    }

    public float getInterpolation(float f) {
        return 1.0f - this.zInterpolator.getInterpolation(1.0f - f);
    }
}
