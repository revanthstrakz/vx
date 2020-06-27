package com.kyleduo.switchbutton;

import android.content.res.ColorStateList;

public class ColorUtils {
    private static final int CHECKED_ATTR = 16842912;
    private static final int ENABLE_ATTR = 16842910;
    private static final int PRESSED_ATTR = 16842919;

    public static ColorStateList generateThumbColorWithTintColor(int i) {
        int i2 = i - -1728053248;
        return new ColorStateList(new int[][]{new int[]{-16842910, CHECKED_ATTR}, new int[]{-16842910}, new int[]{PRESSED_ATTR, -16842912}, new int[]{PRESSED_ATTR, CHECKED_ATTR}, new int[]{CHECKED_ATTR}, new int[]{-16842912}}, new int[]{i - -1442840576, -4539718, i2, i2, i | -16777216, -1118482});
    }

    public static ColorStateList generateBackColorWithTintColor(int i) {
        int i2 = i - -805306368;
        return new ColorStateList(new int[][]{new int[]{-16842910, CHECKED_ATTR}, new int[]{-16842910}, new int[]{CHECKED_ATTR, PRESSED_ATTR}, new int[]{-16842912, PRESSED_ATTR}, new int[]{CHECKED_ATTR}, new int[]{-16842912}}, new int[]{i - -520093696, 268435456, i2, 536870912, i2, 536870912});
    }
}
