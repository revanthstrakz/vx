package com.android.launcher3.touch;

public class OverScroll {
    private static final float OVERSCROLL_DAMP_FACTOR = 0.07f;

    private static float overScrollInfluenceCurve(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2) + 1.0f;
    }

    public static int dampedScroll(float f, int i) {
        if (Float.compare(f, 0.0f) == 0) {
            return 0;
        }
        float f2 = (float) i;
        float f3 = f / f2;
        float abs = (f3 / Math.abs(f3)) * overScrollInfluenceCurve(Math.abs(f3));
        if (Math.abs(abs) >= 1.0f) {
            abs /= Math.abs(abs);
        }
        return Math.round(abs * OVERSCROLL_DAMP_FACTOR * f2);
    }
}
