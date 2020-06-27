package com.android.launcher3;

import android.animation.TimeInterpolator;

public class LogAccelerateInterpolator implements TimeInterpolator {
    int mBase;
    int mDrift;
    final float mLogScale = (1.0f / computeLog(1.0f, this.mBase, this.mDrift));

    public LogAccelerateInterpolator(int i, int i2) {
        this.mBase = i;
        this.mDrift = i2;
    }

    static float computeLog(float f, int i, int i2) {
        return ((float) (-Math.pow((double) i, (double) (-f)))) + 1.0f + (((float) i2) * f);
    }

    public float getInterpolation(float f) {
        if (Float.compare(f, 1.0f) == 0) {
            return 1.0f;
        }
        return 1.0f - (computeLog(1.0f - f, this.mBase, this.mDrift) * this.mLogScale);
    }
}
