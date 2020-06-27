package com.android.launcher3.shortcuts;

import android.annotation.TargetApi;
import android.util.ArrayMap;
import android.util.LruCache;
import java.util.List;

@TargetApi(24)
public class ShortcutCache {
    private static final int CACHE_SIZE = 30;
    private final LruCache<ShortcutKey, ShortcutInfoCompat> mCachedShortcuts = new LruCache<>(30);
    private final ArrayMap<ShortcutKey, ShortcutInfoCompat> mPinnedShortcuts = new ArrayMap<>();

    public void removeShortcuts(List<ShortcutInfoCompat> list) {
        for (ShortcutInfoCompat fromInfo : list) {
            ShortcutKey fromInfo2 = ShortcutKey.fromInfo(fromInfo);
            this.mCachedShortcuts.remove(fromInfo2);
            this.mPinnedShortcuts.remove(fromInfo2);
        }
    }

    public ShortcutInfoCompat get(ShortcutKey shortcutKey) {
        if (this.mPinnedShortcuts.containsKey(shortcutKey)) {
            return (ShortcutInfoCompat) this.mPinnedShortcuts.get(shortcutKey);
        }
        return (ShortcutInfoCompat) this.mCachedShortcuts.get(shortcutKey);
    }

    public void put(ShortcutKey shortcutKey, ShortcutInfoCompat shortcutInfoCompat) {
        if (shortcutInfoCompat.isPinned()) {
            this.mPinnedShortcuts.put(shortcutKey, shortcutInfoCompat);
        } else {
            this.mCachedShortcuts.put(shortcutKey, shortcutInfoCompat);
        }
    }
}
