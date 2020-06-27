package com.google.android.apps.nexuslauncher.clock;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.graphics.IconNormalizer;
import com.android.launcher3.util.Preconditions;
import com.google.android.apps.nexuslauncher.utils.ActionIntentFilter;
import java.util.Collections;
import java.util.Set;
import java.util.TimeZone;
import java.util.WeakHashMap;

public class DynamicClock extends BroadcastReceiver {
    public static final ComponentName DESK_CLOCK = new ComponentName("com.google.android.deskclock", "com.android.deskclock.DeskClock");
    /* access modifiers changed from: private */
    public final Context mContext;
    private ClockLayers mLayers = new ClockLayers();
    private final Set<AutoUpdateClock> mUpdaters = Collections.newSetFromMap(new WeakHashMap());

    public DynamicClock(Context context) {
        this.mContext = context;
        Handler handler = new Handler(LauncherModel.getWorkerLooper());
        this.mContext.registerReceiver(this, ActionIntentFilter.newInstance("com.google.android.deskclock", "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED"), null, handler);
        handler.post(new Runnable() {
            public void run() {
                DynamicClock.this.updateMainThread();
            }
        });
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                DynamicClock.this.loadTimeZone(intent.getStringExtra("time-zone"));
            }
        }, new IntentFilter("android.intent.action.TIMEZONE_CHANGED"), null, new Handler(Looper.getMainLooper()));
    }

    public static Drawable getClock(Context context, int i) {
        ClockLayers clone = getClockLayers(context, i, false).clone();
        if (clone == null) {
            return null;
        }
        clone.updateAngles();
        return clone.mDrawable;
    }

    /* access modifiers changed from: private */
    public static ClockLayers getClockLayers(Context context, int i, boolean z) {
        Preconditions.assertWorkerThread();
        ClockLayers clockLayers = new ClockLayers();
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.google.android.deskclock", 8320);
            Bundle bundle = applicationInfo.metaData;
            if (bundle != null) {
                int i2 = bundle.getInt("com.google.android.apps.nexuslauncher.LEVEL_PER_TICK_ICON_ROUND", 0);
                if (i2 != 0) {
                    clockLayers.mDrawable = packageManager.getResourcesForApplication(applicationInfo).getDrawableForDensity(i2, i).mutate();
                    clockLayers.mHourIndex = bundle.getInt("com.google.android.apps.nexuslauncher.HOUR_LAYER_INDEX", -1);
                    clockLayers.mMinuteIndex = bundle.getInt("com.google.android.apps.nexuslauncher.MINUTE_LAYER_INDEX", -1);
                    clockLayers.mSecondIndex = bundle.getInt("com.google.android.apps.nexuslauncher.SECOND_LAYER_INDEX", -1);
                    clockLayers.mDefaultHour = bundle.getInt("com.google.android.apps.nexuslauncher.DEFAULT_HOUR", 0);
                    clockLayers.mDefaultMinute = bundle.getInt("com.google.android.apps.nexuslauncher.DEFAULT_MINUTE", 0);
                    clockLayers.mDefaultSecond = bundle.getInt("com.google.android.apps.nexuslauncher.DEFAULT_SECOND", 0);
                    if (z) {
                        clockLayers.scale = IconNormalizer.getInstance(context).getScale(clockLayers.mDrawable, null, null, null);
                    }
                    LayerDrawable layerDrawable = clockLayers.getLayerDrawable();
                    int numberOfLayers = layerDrawable.getNumberOfLayers();
                    if (clockLayers.mHourIndex < 0 || clockLayers.mHourIndex >= numberOfLayers) {
                        clockLayers.mHourIndex = -1;
                    }
                    if (clockLayers.mMinuteIndex < 0 || clockLayers.mMinuteIndex >= numberOfLayers) {
                        clockLayers.mMinuteIndex = -1;
                    }
                    if (clockLayers.mSecondIndex >= 0) {
                        if (clockLayers.mSecondIndex < numberOfLayers) {
                            if (Utilities.ATLEAST_MARSHMALLOW) {
                                layerDrawable.setDrawable(clockLayers.mSecondIndex, null);
                                clockLayers.mSecondIndex = -1;
                            }
                        }
                    }
                    clockLayers.mSecondIndex = -1;
                }
            }
        } catch (Exception unused) {
            clockLayers.mDrawable = null;
        }
        return clockLayers;
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

    /* access modifiers changed from: private */
    public void updateMainThread() {
        new MainThreadExecutor().execute(new Runnable() {
            public void run() {
                DynamicClock.this.updateWrapper(DynamicClock.getClockLayers(DynamicClock.this.mContext, LauncherAppState.getIDP(DynamicClock.this.mContext).fillResIconDpi, !FeatureFlags.LAUNCHER3_DISABLE_ICON_NORMALIZATION));
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateWrapper(ClockLayers clockLayers) {
        this.mLayers = clockLayers;
        for (AutoUpdateClock updateLayers : this.mUpdaters) {
            updateLayers.updateLayers(clockLayers.clone());
        }
    }

    public AutoUpdateClock drawIcon(Bitmap bitmap) {
        AutoUpdateClock autoUpdateClock = new AutoUpdateClock(bitmap, this.mLayers.clone());
        this.mUpdaters.add(autoUpdateClock);
        return autoUpdateClock;
    }

    public void onReceive(Context context, Intent intent) {
        updateMainThread();
    }
}
