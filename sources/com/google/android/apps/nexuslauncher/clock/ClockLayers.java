package com.google.android.apps.nexuslauncher.clock;

import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import com.android.launcher3.Utilities;
import java.util.Calendar;
import java.util.TimeZone;

public class ClockLayers {
    private final Calendar mCurrentTime = Calendar.getInstance();
    int mDefaultHour;
    int mDefaultMinute;
    int mDefaultSecond;
    Drawable mDrawable;
    int mHourIndex;
    private LayerDrawable mLayerDrawable;
    int mMinuteIndex;
    int mSecondIndex;
    float scale;

    ClockLayers() {
    }

    public ClockLayers clone() {
        if (this.mDrawable == null) {
            return null;
        }
        ClockLayers clockLayers = new ClockLayers();
        clockLayers.scale = this.scale;
        clockLayers.mHourIndex = this.mHourIndex;
        clockLayers.mMinuteIndex = this.mMinuteIndex;
        clockLayers.mSecondIndex = this.mSecondIndex;
        clockLayers.mDefaultHour = this.mDefaultHour;
        clockLayers.mDefaultMinute = this.mDefaultMinute;
        clockLayers.mDefaultSecond = this.mDefaultSecond;
        clockLayers.mDrawable = this.mDrawable.getConstantState().newDrawable();
        clockLayers.mLayerDrawable = clockLayers.getLayerDrawable();
        if (clockLayers.mLayerDrawable == null) {
            clockLayers = null;
        }
        return clockLayers;
    }

    /* access modifiers changed from: 0000 */
    public boolean updateAngles() {
        this.mCurrentTime.setTimeInMillis(System.currentTimeMillis());
        int i = (this.mCurrentTime.get(12) + (60 - this.mDefaultMinute)) % 60;
        int i2 = (this.mCurrentTime.get(13) + (60 - this.mDefaultSecond)) % 60;
        boolean z = this.mHourIndex != -1 && this.mLayerDrawable.getDrawable(this.mHourIndex).setLevel((((this.mCurrentTime.get(10) + (12 - this.mDefaultHour)) % 12) * 60) + this.mCurrentTime.get(12));
        if (this.mMinuteIndex != -1 && this.mLayerDrawable.getDrawable(this.mMinuteIndex).setLevel(i + (this.mCurrentTime.get(10) * 60))) {
            z = true;
        }
        if (this.mSecondIndex == -1 || !this.mLayerDrawable.getDrawable(this.mSecondIndex).setLevel(i2 * 10)) {
            return z;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void setTimeZone(TimeZone timeZone) {
        this.mCurrentTime.setTimeZone(timeZone);
    }

    /* access modifiers changed from: 0000 */
    public LayerDrawable getLayerDrawable() {
        if (this.mDrawable instanceof LayerDrawable) {
            return (LayerDrawable) this.mDrawable;
        }
        if (Utilities.ATLEAST_OREO && (this.mDrawable instanceof AdaptiveIconDrawable)) {
            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) this.mDrawable;
            if (adaptiveIconDrawable.getForeground() instanceof LayerDrawable) {
                return (LayerDrawable) adaptiveIconDrawable.getForeground();
            }
        }
        return null;
    }
}
