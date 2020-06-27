package com.android.launcher3.dynamicui;

import android.content.Context;
import android.util.Pair;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.WallpaperColorsCompat;
import com.android.launcher3.compat.WallpaperManagerCompat;
import com.android.launcher3.compat.WallpaperManagerCompat.OnColorsChangedListenerCompat;
import java.util.ArrayList;
import java.util.Iterator;

public class WallpaperColorInfo implements OnColorsChangedListenerCompat {
    private static final int FALLBACK_COLOR = -1;
    private static WallpaperColorInfo sInstance;
    private static final Object sInstanceLock = new Object();
    private final Context mContext;
    private final ColorExtractionAlgorithm mExtractionType;
    private boolean mIsDark;
    private boolean mIsTransparent;
    private final ArrayList<OnChangeListener> mListeners = new ArrayList<>();
    private int mMainColor;
    private OnThemeChangeListener mOnThemeChangeListener;
    private int mSecondaryColor;
    private boolean mSupportsDarkText;
    private final WallpaperManagerCompat mWallpaperManager;

    public interface OnChangeListener {
        void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo);
    }

    public interface OnThemeChangeListener {
        void onThemeChanged();
    }

    public static WallpaperColorInfo getInstance(Context context) {
        WallpaperColorInfo wallpaperColorInfo;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new WallpaperColorInfo(context.getApplicationContext());
            }
            wallpaperColorInfo = sInstance;
        }
        return wallpaperColorInfo;
    }

    private WallpaperColorInfo(Context context) {
        this.mContext = context;
        this.mWallpaperManager = WallpaperManagerCompat.getInstance(context);
        this.mWallpaperManager.addOnColorsChangedListener(this);
        this.mExtractionType = ColorExtractionAlgorithm.newInstance(context);
        update(this.mWallpaperManager.getWallpaperColors(1));
    }

    public int getMainColor() {
        return this.mMainColor;
    }

    public int getSecondaryColor() {
        return this.mSecondaryColor;
    }

    public boolean isDark() {
        return this.mIsDark;
    }

    public boolean supportsDarkText() {
        return this.mSupportsDarkText;
    }

    public boolean isTransparent() {
        return this.mIsTransparent;
    }

    public void onColorsChanged(WallpaperColorsCompat wallpaperColorsCompat, int i) {
        boolean z = true;
        if ((i & 1) != 0) {
            boolean z2 = this.mIsDark;
            boolean z3 = this.mSupportsDarkText;
            boolean z4 = this.mIsTransparent;
            update(wallpaperColorsCompat);
            if (z2 == this.mIsDark && z3 == this.mSupportsDarkText && z4 == this.mIsTransparent) {
                z = false;
            }
            notifyChange(z);
        }
    }

    private void update(WallpaperColorsCompat wallpaperColorsCompat) {
        int i;
        Pair extractInto = this.mExtractionType.extractInto(wallpaperColorsCompat);
        if (extractInto != null) {
            this.mMainColor = ((Integer) extractInto.first).intValue();
            this.mSecondaryColor = ((Integer) extractInto.second).intValue();
        } else {
            this.mMainColor = -1;
            this.mSecondaryColor = -1;
        }
        Context context = this.mContext;
        boolean z = false;
        if (wallpaperColorsCompat == null) {
            i = 0;
        } else {
            i = wallpaperColorsCompat.getColorHints();
        }
        int themeHints = Utilities.getThemeHints(context, i);
        this.mSupportsDarkText = (themeHints & 1) > 0;
        this.mIsDark = (themeHints & 2) > 0;
        if ((themeHints & 256) > 0) {
            z = true;
        }
        this.mIsTransparent = z;
    }

    public void setOnThemeChangeListener(OnThemeChangeListener onThemeChangeListener) {
        this.mOnThemeChangeListener = onThemeChangeListener;
    }

    public void addOnChangeListener(OnChangeListener onChangeListener) {
        this.mListeners.add(onChangeListener);
    }

    public void removeOnChangeListener(OnChangeListener onChangeListener) {
        this.mListeners.remove(onChangeListener);
    }

    public void notifyChange(boolean z) {
        if (!z) {
            Iterator it = this.mListeners.iterator();
            while (it.hasNext()) {
                ((OnChangeListener) it.next()).onExtractedColorsChanged(this);
            }
        } else if (this.mOnThemeChangeListener != null) {
            this.mOnThemeChangeListener.onThemeChanged();
        }
    }
}
