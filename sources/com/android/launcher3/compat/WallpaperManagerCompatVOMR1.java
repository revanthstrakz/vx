package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.app.WallpaperManager.OnColorsChangedListener;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.launcher3.compat.WallpaperManagerCompat.OnColorsChangedListenerCompat;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

@TargetApi(27)
public class WallpaperManagerCompatVOMR1 extends WallpaperManagerCompat {
    private static final String TAG = "WMCompatVOMR1";
    private final ArrayList<OnColorsChangedListenerCompat> mListeners = new ArrayList<>();
    private Method mWCColorHintsMethod;
    private final WallpaperManager mWm;

    WallpaperManagerCompatVOMR1(Context context) throws Throwable {
        this.mWm = (WallpaperManager) context.getSystemService(WallpaperManager.class);
        WallpaperColors.class.getName();
        try {
            this.mWCColorHintsMethod = WallpaperColors.class.getDeclaredMethod("getColorHints", new Class[0]);
        } catch (Exception e) {
            Log.e(TAG, "getColorHints not available", e);
        }
    }

    @Nullable
    public WallpaperColorsCompat getWallpaperColors(int i) {
        return convertColorsObject(this.mWm.getWallpaperColors(i));
    }

    public void addOnColorsChangedListener(final OnColorsChangedListenerCompat onColorsChangedListenerCompat) {
        this.mListeners.add(onColorsChangedListenerCompat);
        this.mWm.addOnColorsChangedListener(new OnColorsChangedListener() {
            public void onColorsChanged(WallpaperColors wallpaperColors, int i) {
                onColorsChangedListenerCompat.onColorsChanged(WallpaperManagerCompatVOMR1.this.convertColorsObject(wallpaperColors), i);
            }
        }, null);
    }

    public void updateAllListeners() {
        WallpaperColorsCompat wallpaperColors = getWallpaperColors(1);
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            ((OnColorsChangedListenerCompat) it.next()).onColorsChanged(wallpaperColors, 1);
        }
    }

    /* access modifiers changed from: private */
    public WallpaperColorsCompat convertColorsObject(WallpaperColors wallpaperColors) {
        if (wallpaperColors == null) {
            return null;
        }
        Color primaryColor = wallpaperColors.getPrimaryColor();
        Color secondaryColor = wallpaperColors.getSecondaryColor();
        Color tertiaryColor = wallpaperColors.getTertiaryColor();
        int i = 0;
        int argb = primaryColor != null ? primaryColor.toArgb() : 0;
        int argb2 = secondaryColor != null ? secondaryColor.toArgb() : 0;
        int argb3 = tertiaryColor != null ? tertiaryColor.toArgb() : 0;
        try {
            if (this.mWCColorHintsMethod != null) {
                i = ((Integer) this.mWCColorHintsMethod.invoke(wallpaperColors, new Object[0])).intValue();
            }
        } catch (Exception e) {
            Log.e(TAG, "error calling color hints", e);
        }
        return new WallpaperColorsCompat(argb, argb2, argb3, i);
    }
}
