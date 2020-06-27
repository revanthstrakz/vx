package com.android.launcher3.dynamicui;

import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.Context;
import android.support.p001v4.graphics.ColorUtils;
import android.support.p004v7.graphics.Palette;
import android.support.p004v7.graphics.Palette.Swatch;
import com.android.launcher3.Utilities;
import java.util.List;

public class ExtractionUtils {
    public static final String EXTRACTED_COLORS_PREFERENCE_KEY = "pref_extractedColors";
    private static final float MIN_CONTRAST_RATIO = 2.0f;
    public static final String WALLPAPER_ID_PREFERENCE_KEY = "pref_wallpaperId";

    public static void startColorExtractionService(Context context) {
    }

    public static void startColorExtractionServiceIfNecessary(Context context) {
    }

    /* access modifiers changed from: private */
    public static boolean hasWallpaperIdChanged(Context context) {
        boolean z = false;
        if (!Utilities.ATLEAST_NOUGAT) {
            return false;
        }
        if (getWallpaperId(WallpaperManager.getInstance(context)) != Utilities.getPrefs(context).getInt(WALLPAPER_ID_PREFERENCE_KEY, -1)) {
            z = true;
        }
        return z;
    }

    @TargetApi(24)
    public static int getWallpaperId(WallpaperManager wallpaperManager) {
        if (Utilities.ATLEAST_NOUGAT) {
            return wallpaperManager.getWallpaperId(1);
        }
        return -1;
    }

    public static boolean isSuperLight(Palette palette) {
        return !isLegibleOnWallpaper(-1, palette.getSwatches());
    }

    public static boolean isSuperDark(Palette palette) {
        return !isLegibleOnWallpaper(-16777216, palette.getSwatches());
    }

    private static boolean isLegibleOnWallpaper(int i, List<Swatch> list) {
        int i2 = 0;
        int i3 = 0;
        for (Swatch swatch : list) {
            if (isLegible(i, swatch.getRgb())) {
                i2 += swatch.getPopulation();
            } else {
                i3 += swatch.getPopulation();
            }
        }
        if (i2 > i3) {
            return true;
        }
        return false;
    }

    private static boolean isLegible(int i, int i2) {
        return ColorUtils.calculateContrast(i, ColorUtils.setAlphaComponent(i2, 255)) >= 2.0d;
    }
}
