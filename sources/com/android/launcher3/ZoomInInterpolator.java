package com.android.launcher3;

import android.animation.TimeInterpolator;
import android.view.animation.DecelerateInterpolator;

/* compiled from: WorkspaceStateTransitionAnimation */
class ZoomInInterpolator implements TimeInterpolator {
    private final DecelerateInterpolator decelerate = new DecelerateInterpolator(3.0f);
    private final InverseZInterpolator inverseZInterpolator = new InverseZInterpolator(0.35f);

    ZoomInInterpolator() {
    }

    public float getInterpolation(float f) {
        return this.decelerate.getInterpolation(this.inverseZInterpolator.getInterpolation(f));
    }
}
