package com.android.launcher3.model;

import android.content.Context;
import android.os.UserHandle;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ItemInfoMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class UserLockStateChangedTask extends BaseModelUpdateTask {
    private final UserHandle mUser;

    public UserLockStateChangedTask(UserHandle userHandle) {
        this.mUser = userHandle;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        Context context = launcherAppState.getContext();
        boolean isUserUnlocked = UserManagerCompat.getInstance(context).isUserUnlocked(this.mUser);
        DeepShortcutManager instance = DeepShortcutManager.getInstance(context);
        HashMap hashMap = new HashMap();
        if (isUserUnlocked) {
            List<ShortcutInfoCompat> queryForPinnedShortcuts = instance.queryForPinnedShortcuts(null, this.mUser);
            if (instance.wasLastCallSuccess()) {
                for (ShortcutInfoCompat shortcutInfoCompat : queryForPinnedShortcuts) {
                    hashMap.put(ShortcutKey.fromInfo(shortcutInfoCompat), shortcutInfoCompat);
                }
            } else {
                isUserUnlocked = false;
            }
        }
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        Iterator it = bgDataModel.itemsIdMap.iterator();
        while (it.hasNext()) {
            ItemInfo itemInfo = (ItemInfo) it.next();
            if (itemInfo.itemType == 6 && this.mUser.equals(itemInfo.user)) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                if (isUserUnlocked) {
                    ShortcutKey fromItemInfo = ShortcutKey.fromItemInfo(shortcutInfo);
                    ShortcutInfoCompat shortcutInfoCompat2 = (ShortcutInfoCompat) hashMap.get(fromItemInfo);
                    if (shortcutInfoCompat2 == null) {
                        hashSet.add(fromItemInfo);
                    } else {
                        shortcutInfo.isDisabled &= -33;
                        shortcutInfo.updateFromDeepShortcutInfo(shortcutInfoCompat2, context);
                        shortcutInfo.iconBitmap = LauncherIcons.createShortcutIcon(shortcutInfoCompat2, context, shortcutInfo.iconBitmap);
                    }
                } else {
                    shortcutInfo.isDisabled |= 32;
                }
                arrayList.add(shortcutInfo);
            }
        }
        bindUpdatedShortcuts(arrayList, this.mUser);
        if (!hashSet.isEmpty()) {
            deleteAndBindComponentsRemoved(ItemInfoMatcher.ofShortcutKeys(hashSet));
        }
        Iterator it2 = bgDataModel.deepShortcutMap.keySet().iterator();
        while (it2.hasNext()) {
            if (((ComponentKey) it2.next()).user.equals(this.mUser)) {
                it2.remove();
            }
        }
        if (isUserUnlocked) {
            bgDataModel.updateDeepShortcutMap(null, this.mUser, instance.queryForAllShortcuts(this.mUser));
        }
        bindDeepShortcuts(bgDataModel);
    }
}
