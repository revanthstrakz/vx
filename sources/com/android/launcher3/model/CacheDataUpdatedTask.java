package com.android.launcher3.model;

import android.content.ComponentName;
import android.os.UserHandle;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.IconCache;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel.CallbackTask;
import com.android.launcher3.LauncherModel.Callbacks;
import com.android.launcher3.ShortcutInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class CacheDataUpdatedTask extends BaseModelUpdateTask {
    public static final int OP_CACHE_UPDATE = 1;
    public static final int OP_SESSION_UPDATE = 2;
    private final int mOp;
    private final HashSet<String> mPackages;
    private final UserHandle mUser;

    public CacheDataUpdatedTask(int i, UserHandle userHandle, HashSet<String> hashSet) {
        this.mOp = i;
        this.mUser = userHandle;
        this.mPackages = hashSet;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        IconCache iconCache = launcherAppState.getIconCache();
        final ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        synchronized (bgDataModel) {
            Iterator it = bgDataModel.itemsIdMap.iterator();
            while (it.hasNext()) {
                ItemInfo itemInfo = (ItemInfo) it.next();
                if ((itemInfo instanceof ShortcutInfo) && this.mUser.equals(itemInfo.user)) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                    ComponentName targetComponent = shortcutInfo.getTargetComponent();
                    if (shortcutInfo.itemType == 0 && isValidShortcut(shortcutInfo) && targetComponent != null && this.mPackages.contains(targetComponent.getPackageName())) {
                        iconCache.getTitleAndIcon(shortcutInfo, shortcutInfo.usingLowResIcon);
                        arrayList2.add(shortcutInfo);
                    }
                }
            }
            allAppsList.updateIconsAndLabels(this.mPackages, this.mUser, arrayList);
        }
        bindUpdatedShortcuts(arrayList2, this.mUser);
        if (!arrayList.isEmpty()) {
            scheduleCallbackTask(new CallbackTask() {
                public void execute(Callbacks callbacks) {
                    callbacks.bindAppsAddedOrUpdated(arrayList);
                }
            });
        }
    }

    public boolean isValidShortcut(ShortcutInfo shortcutInfo) {
        switch (this.mOp) {
            case 1:
                return true;
            case 2:
                return shortcutInfo.hasPromiseIconUi();
            default:
                return false;
        }
    }
}
