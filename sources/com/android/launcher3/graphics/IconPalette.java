package com.android.launcher3.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p001v4.graphics.ColorUtils;
import com.android.launcher3.C0622R;
import com.android.launcher3.util.Themes;

public class IconPalette {
    private static final boolean DEBUG = false;
    private static final float MIN_PRELOAD_COLOR_LIGHTNESS = 0.6f;
    private static final float MIN_PRELOAD_COLOR_SATURATION = 0.2f;
    private static final String TAG = "IconPalette";
    private static IconPalette sBadgePalette;
    private static IconPalette sFolderBadgePalette;
    public final int backgroundColor;
    public final ColorMatrixColorFilter backgroundColorMatrixFilter;
    public final int dominantColor;
    public final ColorMatrixColorFilter saturatedBackgroundColorMatrixFilter;
    public final int secondaryColor;
    public final int textColor;

    private IconPalette(int i, boolean z) {
        this.dominantColor = i;
        this.backgroundColor = z ? getMutedColor(this.dominantColor, 0.87f) : this.dominantColor;
        ColorMatrix colorMatrix = new ColorMatrix();
        Themes.setColorScaleOnMatrix(this.backgroundColor, colorMatrix);
        this.backgroundColorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        if (!z) {
            this.saturatedBackgroundColorMatrixFilter = this.backgroundColorMatrixFilter;
        } else {
            Themes.setColorScaleOnMatrix(getMutedColor(this.dominantColor, 0.54f), colorMatrix);
            this.saturatedBackgroundColorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        }
        this.textColor = getTextColorForBackground(this.backgroundColor);
        this.secondaryColor = getLowContrastColor(this.backgroundColor);
    }

    public int getPreloadProgressColor(Context context) {
        float[] fArr = new float[3];
        Color.colorToHSV(this.dominantColor, fArr);
        if (fArr[1] < 0.2f) {
            return Themes.getColorAccent(context);
        }
        fArr[2] = Math.max(MIN_PRELOAD_COLOR_LIGHTNESS, fArr[2]);
        return Color.HSVToColor(fArr);
    }

    public static IconPalette fromDominantColor(int i, boolean z) {
        return new IconPalette(i, z);
    }

    @Nullable
    public static IconPalette getBadgePalette(Resources resources) {
        int color = resources.getColor(C0622R.color.badge_color);
        if (color == 0) {
            return null;
        }
        if (sBadgePalette == null) {
            sBadgePalette = fromDominantColor(color, false);
        }
        return sBadgePalette;
    }

    @NonNull
    public static IconPalette getFolderBadgePalette(Resources resources) {
        if (sFolderBadgePalette == null) {
            sFolderBadgePalette = fromDominantColor(resources.getColor(C0622R.color.folder_badge_color), false);
        }
        return sFolderBadgePalette;
    }

    public static int resolveContrastColor(Context context, int i, int i2) {
        return ensureTextContrast(resolveColor(context, i), i2);
    }

    private static int resolveColor(Context context, int i) {
        return i == 0 ? context.getColor(C0622R.color.notification_icon_default_color) : i;
    }

    private static String contrastChange(int i, int i2, int i3) {
        return String.format("from %.2f:1 to %.2f:1", new Object[]{Double.valueOf(ColorUtils.calculateContrast(i, i3)), Double.valueOf(ColorUtils.calculateContrast(i2, i3))});
    }

    private static int ensureTextContrast(int i, int i2) {
        return findContrastColor(i, i2, 4.5d);
    }

    private static int findContrastColor(int i, int i2, double d) {
        int i3 = i;
        int i4 = i2;
        if (ColorUtils.calculateContrast(i, i2) >= d) {
            return i3;
        }
        double[] dArr = new double[3];
        ColorUtils.colorToLAB(i4, dArr);
        double d2 = dArr[0];
        ColorUtils.colorToLAB(i3, dArr);
        double d3 = dArr[0];
        boolean z = d2 < 50.0d;
        double d4 = z ? d3 : 0.0d;
        if (z) {
            d3 = 100.0d;
        }
        double d5 = dArr[1];
        double d6 = dArr[2];
        for (int i5 = 0; i5 < 15 && d3 - d4 > 1.0E-5d; i5++) {
            double d7 = (d4 + d3) / 2.0d;
            if (ColorUtils.calculateContrast(ColorUtils.LABToColor(d7, d5, d6), i4) <= d ? !z : z) {
                d3 = d7;
            } else {
                d4 = d7;
            }
        }
        return ColorUtils.LABToColor(d4, d5, d6);
    }

    private static int getMutedColor(int i, float f) {
        return ColorUtils.compositeColors(ColorUtils.setAlphaComponent(-1, (int) (f * 255.0f)), i);
    }

    private static int getTextColorForBackground(int i) {
        return getLighterOrDarkerVersionOfColor(i, 4.5f);
    }

    private static int getLowContrastColor(int i) {
        return getLighterOrDarkerVersionOfColor(i, 1.5f);
    }

    private static int getLighterOrDarkerVersionOfColor(int i, float f) {
        int i2 = -1;
        int calculateMinimumAlpha = ColorUtils.calculateMinimumAlpha(-1, i, f);
        int calculateMinimumAlpha2 = ColorUtils.calculateMinimumAlpha(-16777216, i, f);
        if (calculateMinimumAlpha >= 0) {
            i2 = ColorUtils.setAlphaComponent(-1, calculateMinimumAlpha);
        } else if (calculateMinimumAlpha2 >= 0) {
            i2 = ColorUtils.setAlphaComponent(-16777216, calculateMinimumAlpha2);
        }
        return ColorUtils.compositeColors(i2, i);
    }
}
