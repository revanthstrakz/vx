package com.google.android.apps.nexuslauncher.clock;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.graphics.IconNormalizer;
import com.android.launcher3.util.Preconditions;
import java.util.Collections;
import java.util.Set;
import java.util.TimeZone;
import java.util.WeakHashMap;

@TargetApi(26)
public class CustomClock {
    private final Context mContext;
    private final Set<AutoUpdateClock> mUpdaters = Collections.newSetFromMap(new WeakHashMap());

    public static class Metadata {
        final int DEFAULT_HOUR;
        final int DEFAULT_MINUTE;
        final int DEFAULT_SECOND;
        final int HOUR_LAYER_INDEX;
        final int MINUTE_LAYER_INDEX;
        final int SECOND_LAYER_INDEX;

        public Metadata(int i, int i2, int i3, int i4, int i5, int i6) {
            this.HOUR_LAYER_INDEX = i;
            this.MINUTE_LAYER_INDEX = i2;
            this.SECOND_LAYER_INDEX = i3;
            this.DEFAULT_HOUR = i4;
            this.DEFAULT_MINUTE = i5;
            this.DEFAULT_SECOND = i6;
        }
    }

    public CustomClock(Context context) {
        this.mContext = context;
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                CustomClock.this.loadTimeZone(intent.getStringExtra("time-zone"));
            }
        }, new IntentFilter("android.intent.action.TIMEZONE_CHANGED"), null, new Handler(Looper.getMainLooper()));
    }

    public static Drawable getClock(Context context, Drawable drawable, Metadata metadata, int i) {
        ClockLayers clone = getClockLayers(context, drawable, metadata, i, false).clone();
        if (clone == null) {
            return null;
        }
        clone.updateAngles();
        return clone.mDrawable;
    }

    private static ClockLayers getClockLayers(Context context, Drawable drawable, Metadata metadata, int i, boolean z) {
        Preconditions.assertWorkerThread();
        ClockLayers clockLayers = new ClockLayers();
        clockLayers.mDrawable = drawable.mutate();
        clockLayers.mHourIndex = metadata.HOUR_LAYER_INDEX;
        clockLayers.mMinuteIndex = metadata.MINUTE_LAYER_INDEX;
        clockLayers.mSecondIndex = metadata.SECOND_LAYER_INDEX;
        clockLayers.mDefaultHour = metadata.DEFAULT_HOUR;
        clockLayers.mDefaultMinute = metadata.DEFAULT_MINUTE;
        clockLayers.mDefaultSecond = metadata.DEFAULT_SECOND;
        if (z) {
            clockLayers.scale = IconNormalizer.getInstance(context).getScale(clockLayers.mDrawable, null, null, null);
        }
        int numberOfLayers = clockLayers.getLayerDrawable().getNumberOfLayers();
        if (clockLayers.mHourIndex < 0 || clockLayers.mHourIndex >= numberOfLayers) {
            clockLayers.mHourIndex = -1;
        }
        if (clockLayers.mMinuteIndex < 0 || clockLayers.mMinuteIndex >= numberOfLayers) {
            clockLayers.mMinuteIndex = -1;
        }
        if (clockLayers.mSecondIndex < 0 || clockLayers.mSecondIndex >= numberOfLayers) {
            clockLayers.mSecondIndex = -1;
        }
        return clockLayers;
    }

    public FastBitmapDrawable drawIcon(Bitmap bitmap, Drawable drawable, Metadata metadata) {
        AutoUpdateClock autoUpdateClock = new AutoUpdateClock(bitmap, getClockLayers(this.mContext, drawable, metadata, LauncherAppState.getIDP(this.mContext).fillResIconDpi, !FeatureFlags.LAUNCHER3_DISABLE_ICON_NORMALIZATION).clone());
        this.mUpdaters.add(autoUpdateClock);
        return autoUpdateClock;
    }

    /* access modifiers changed from: private */
    public void loadTimeZone(String str) {
        TimeZone timeZone;
        if (str == null) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = TimeZone.getTimeZone(str);
        }
        for (AutoUpdateClock timeZone2 : this.mUpdaters) {
            timeZone2.setTimeZone(timeZone);
        }
    }
}
