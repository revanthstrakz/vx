package com.android.launcher3.compat;

import android.support.annotation.Nullable;

public abstract class WallpaperManagerCompat {
    private static WallpaperManagerCompat sInstance;
    private static final Object sInstanceLock = new Object();

    public interface OnColorsChangedListenerCompat {
        void onColorsChanged(WallpaperColorsCompat wallpaperColorsCompat, int i);
    }

    public abstract void addOnColorsChangedListener(OnColorsChangedListenerCompat onColorsChangedListenerCompat);

    @Nullable
    public abstract WallpaperColorsCompat getWallpaperColors(int i);

    public abstract void updateAllListeners();

    /* JADX WARNING: Can't wrap try/catch for region: R(5:5|(2:7|8)|9|10|(1:12)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0016 */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x001a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.android.launcher3.compat.WallpaperManagerCompat getInstance(android.content.Context r2) {
        /*
            java.lang.Object r0 = sInstanceLock
            monitor-enter(r0)
            com.android.launcher3.compat.WallpaperManagerCompat r1 = sInstance     // Catch:{ all -> 0x0025 }
            if (r1 != 0) goto L_0x0021
            android.content.Context r2 = r2.getApplicationContext()     // Catch:{ all -> 0x0025 }
            boolean r1 = com.android.launcher3.Utilities.ATLEAST_OREO_MR1     // Catch:{ all -> 0x0025 }
            if (r1 == 0) goto L_0x0016
            com.android.launcher3.compat.WallpaperManagerCompatVOMR1 r1 = new com.android.launcher3.compat.WallpaperManagerCompatVOMR1     // Catch:{ Throwable -> 0x0016 }
            r1.<init>(r2)     // Catch:{ Throwable -> 0x0016 }
            sInstance = r1     // Catch:{ Throwable -> 0x0016 }
        L_0x0016:
            com.android.launcher3.compat.WallpaperManagerCompat r1 = sInstance     // Catch:{ all -> 0x0025 }
            if (r1 != 0) goto L_0x0021
            com.android.launcher3.compat.WallpaperManagerCompatVL r1 = new com.android.launcher3.compat.WallpaperManagerCompatVL     // Catch:{ all -> 0x0025 }
            r1.<init>(r2)     // Catch:{ all -> 0x0025 }
            sInstance = r1     // Catch:{ all -> 0x0025 }
        L_0x0021:
            com.android.launcher3.compat.WallpaperManagerCompat r2 = sInstance     // Catch:{ all -> 0x0025 }
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            return r2
        L_0x0025:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.compat.WallpaperManagerCompat.getInstance(android.content.Context):com.android.launcher3.compat.WallpaperManagerCompat");
    }
}
