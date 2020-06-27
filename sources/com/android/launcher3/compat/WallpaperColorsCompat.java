package com.android.launcher3.compat;

public class WallpaperColorsCompat {
    public static final int HINT_SUPPORTS_DARK_TEXT = 1;
    public static final int HINT_SUPPORTS_DARK_THEME = 2;
    public static final int HINT_SUPPORTS_TRANSPARENCY = 256;
    private final int mColorHints;
    private final int mPrimaryColor;
    private final int mSecondaryColor;
    private final int mTertiaryColor;

    public WallpaperColorsCompat(int i, int i2, int i3, int i4) {
        this.mPrimaryColor = i;
        this.mSecondaryColor = i2;
        this.mTertiaryColor = i3;
        this.mColorHints = i4;
    }

    public int getPrimaryColor() {
        return this.mPrimaryColor;
    }

    public int getSecondaryColor() {
        return this.mSecondaryColor;
    }

    public int getTertiaryColor() {
        return this.mTertiaryColor;
    }

    public int getColorHints() {
        return this.mColorHints;
    }
}
