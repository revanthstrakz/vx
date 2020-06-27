package com.android.launcher3.util;

import android.content.ComponentName;
import android.os.UserHandle;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import java.util.HashSet;
import java.util.Iterator;

public abstract class ItemInfoMatcher {
    public abstract boolean matches(ItemInfo itemInfo, ComponentName componentName);

    public final HashSet<ItemInfo> filterItemInfos(Iterable<ItemInfo> iterable) {
        HashSet<ItemInfo> hashSet = new HashSet<>();
        for (ItemInfo itemInfo : iterable) {
            if (itemInfo instanceof ShortcutInfo) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                ComponentName targetComponent = shortcutInfo.getTargetComponent();
                if (targetComponent != null && matches(shortcutInfo, targetComponent)) {
                    hashSet.add(shortcutInfo);
                }
            } else if (itemInfo instanceof FolderInfo) {
                Iterator it = ((FolderInfo) itemInfo).contents.iterator();
                while (it.hasNext()) {
                    ShortcutInfo shortcutInfo2 = (ShortcutInfo) it.next();
                    ComponentName targetComponent2 = shortcutInfo2.getTargetComponent();
                    if (targetComponent2 != null && matches(shortcutInfo2, targetComponent2)) {
                        hashSet.add(shortcutInfo2);
                    }
                }
            } else if (itemInfo instanceof LauncherAppWidgetInfo) {
                LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
                ComponentName componentName = launcherAppWidgetInfo.providerName;
                if (componentName != null && matches(launcherAppWidgetInfo, componentName)) {
                    hashSet.add(launcherAppWidgetInfo);
                }
            }
        }
        return hashSet;
    }

    /* renamed from: or */
    public ItemInfoMatcher mo11537or(final ItemInfoMatcher itemInfoMatcher) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo itemInfo, ComponentName componentName) {
                return this.matches(itemInfo, componentName) || itemInfoMatcher.matches(itemInfo, componentName);
            }
        };
    }

    public ItemInfoMatcher and(final ItemInfoMatcher itemInfoMatcher) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo itemInfo, ComponentName componentName) {
                return this.matches(itemInfo, componentName) && itemInfoMatcher.matches(itemInfo, componentName);
            }
        };
    }

    public static ItemInfoMatcher ofUser(final UserHandle userHandle) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo itemInfo, ComponentName componentName) {
                return itemInfo.user.equals(userHandle);
            }
        };
    }

    public static ItemInfoMatcher ofComponents(final HashSet<ComponentName> hashSet, final UserHandle userHandle) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo itemInfo, ComponentName componentName) {
                return hashSet.contains(componentName) && itemInfo.user.equals(userHandle);
            }
        };
    }

    public static ItemInfoMatcher ofPackages(final HashSet<String> hashSet, final UserHandle userHandle) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo itemInfo, ComponentName componentName) {
                return hashSet.contains(componentName.getPackageName()) && itemInfo.user.equals(userHandle);
            }
        };
    }

    public static ItemInfoMatcher ofShortcutKeys(final HashSet<ShortcutKey> hashSet) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo itemInfo, ComponentName componentName) {
                return itemInfo.itemType == 6 && hashSet.contains(ShortcutKey.fromItemInfo(itemInfo));
            }
        };
    }

    public static ItemInfoMatcher ofItemIds(final LongArrayMap<Boolean> longArrayMap, final Boolean bool) {
        return new ItemInfoMatcher() {
            public boolean matches(ItemInfo itemInfo, ComponentName componentName) {
                return ((Boolean) longArrayMap.get(itemInfo.f52id, bool)).booleanValue();
            }
        };
    }
}
