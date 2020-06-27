package com.android.launcher3.model;

import android.content.Context;
import android.os.UserHandle;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.MultiHashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ShortcutsChangedTask extends BaseModelUpdateTask {
    private final String mPackageName;
    private final List<ShortcutInfoCompat> mShortcuts;
    private final boolean mUpdateIdMap;
    private final UserHandle mUser;

    public ShortcutsChangedTask(String str, List<ShortcutInfoCompat> list, UserHandle userHandle, boolean z) {
        this.mPackageName = str;
        this.mShortcuts = list;
        this.mUser = userHandle;
        this.mUpdateIdMap = z;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        Context context = launcherAppState.getContext();
        DeepShortcutManager instance = DeepShortcutManager.getInstance(context);
        instance.onShortcutsChanged(this.mShortcuts);
        HashSet hashSet = new HashSet();
        MultiHashMap multiHashMap = new MultiHashMap();
        HashSet hashSet2 = new HashSet();
        Iterator it = bgDataModel.itemsIdMap.iterator();
        while (it.hasNext()) {
            ItemInfo itemInfo = (ItemInfo) it.next();
            if (itemInfo.itemType == 6) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                if (shortcutInfo.getIntent().getPackage().equals(this.mPackageName) && shortcutInfo.user.equals(this.mUser)) {
                    multiHashMap.addToList(ShortcutKey.fromItemInfo(shortcutInfo), shortcutInfo);
                    hashSet2.add(shortcutInfo.getDeepShortcutId());
                }
            }
        }
        ArrayList arrayList = new ArrayList();
        if (!multiHashMap.isEmpty()) {
            for (ShortcutInfoCompat shortcutInfoCompat : instance.queryForFullDetails(this.mPackageName, new ArrayList(hashSet2), this.mUser)) {
                ShortcutKey fromInfo = ShortcutKey.fromInfo(shortcutInfoCompat);
                List<ShortcutInfo> list = (List) multiHashMap.remove(fromInfo);
                if (!shortcutInfoCompat.isPinned()) {
                    hashSet.add(fromInfo);
                } else {
                    for (ShortcutInfo shortcutInfo2 : list) {
                        shortcutInfo2.updateFromDeepShortcutInfo(shortcutInfoCompat, context);
                        shortcutInfo2.iconBitmap = LauncherIcons.createShortcutIcon(shortcutInfoCompat, context, shortcutInfo2.iconBitmap);
                        arrayList.add(shortcutInfo2);
                    }
                }
            }
        }
        hashSet.addAll(multiHashMap.keySet());
        bindUpdatedShortcuts(arrayList, this.mUser);
        if (!multiHashMap.isEmpty()) {
            deleteAndBindComponentsRemoved(ItemInfoMatcher.ofShortcutKeys(hashSet));
        }
        if (this.mUpdateIdMap) {
            bgDataModel.updateDeepShortcutMap(this.mPackageName, this.mUser, this.mShortcuts);
            bindDeepShortcuts(bgDataModel);
        }
    }
}
