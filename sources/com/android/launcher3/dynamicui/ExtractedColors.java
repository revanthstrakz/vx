package com.android.launcher3.dynamicui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.p001v4.graphics.ColorUtils;
import android.support.p004v7.graphics.Palette;
import android.util.Log;
import com.android.launcher3.Utilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ExtractedColors {
    public static final int ALLAPPS_GRADIENT_MAIN_INDEX = 4;
    public static final int ALLAPPS_GRADIENT_SECONDARY_INDEX = 5;
    private static final String COLOR_SEPARATOR = ",";
    public static final int DEFAULT_DARK = -16777216;
    public static final int DEFAULT_LIGHT = -1;
    private static final int[] DEFAULT_VALUES = {VERSION, 1090519039, -16777216, -3355444, -16777216, -16777216};
    public static final int HOTSEAT_INDEX = 1;
    public static final int STATUS_BAR_INDEX = 2;
    private static final String TAG = "ExtractedColors";
    private static final int VERSION = 3;
    public static final int VERSION_INDEX = 0;
    public static final int WALLPAPER_VIBRANT_INDEX = 3;
    private final int[] mColors = Arrays.copyOf(DEFAULT_VALUES, DEFAULT_VALUES.length);
    private final ArrayList<OnChangeListener> mListeners = new ArrayList<>();

    public interface OnChangeListener {
        void onExtractedColorsChanged();
    }

    public void setColorAtIndex(int i, int i2) {
        if (i <= 0 || i >= this.mColors.length) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Attempted to set a color at an invalid index ");
            sb.append(i);
            Log.e(str, sb.toString());
            return;
        }
        this.mColors[i] = i2;
    }

    /* access modifiers changed from: 0000 */
    public String encodeAsString() {
        StringBuilder sb = new StringBuilder();
        for (int append : this.mColors) {
            sb.append(append);
            sb.append(COLOR_SEPARATOR);
        }
        return sb.toString();
    }

    public void load(Context context) {
        SharedPreferences prefs = Utilities.getPrefs(context);
        String str = ExtractionUtils.EXTRACTED_COLORS_PREFERENCE_KEY;
        StringBuilder sb = new StringBuilder();
        sb.append(VERSION);
        sb.append("");
        String[] split = prefs.getString(str, sb.toString()).split(COLOR_SEPARATOR);
        if (split.length == DEFAULT_VALUES.length) {
            if (Integer.parseInt(split[0]) == VERSION) {
                for (int i = 0; i < this.mColors.length; i++) {
                    this.mColors[i] = Integer.parseInt(split[i]);
                }
                return;
            }
        }
        ExtractionUtils.startColorExtractionService(context);
    }

    public int getColor(int i) {
        return this.mColors[i];
    }

    public void updateHotseatPalette(Palette palette) {
        int i;
        if (palette != null && ExtractionUtils.isSuperLight(palette)) {
            i = ColorUtils.setAlphaComponent(-16777216, 30);
        } else if (palette == null || !ExtractionUtils.isSuperDark(palette)) {
            i = DEFAULT_VALUES[1];
        } else {
            i = ColorUtils.setAlphaComponent(-1, 45);
        }
        setColorAtIndex(1, i);
    }

    public void updateStatusBarPalette(Palette palette) {
        setColorAtIndex(2, ExtractionUtils.isSuperLight(palette) ? -1 : -16777216);
    }

    public void updateWallpaperThemePalette(@Nullable Palette palette) {
        int i = DEFAULT_VALUES[3];
        if (palette != null) {
            i = palette.getVibrantColor(i);
        }
        setColorAtIndex(3, i);
    }

    public void addOnChangeListener(OnChangeListener onChangeListener) {
        this.mListeners.add(onChangeListener);
    }

    public void notifyChange() {
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            ((OnChangeListener) it.next()).onExtractedColorsChanged();
        }
    }
}
