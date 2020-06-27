package com.android.launcher3.widget;

import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;

public class PendingAddShortcutInfo extends PendingAddItemInfo {
    public ShortcutConfigActivityInfo activityInfo;

    public PendingAddShortcutInfo(ShortcutConfigActivityInfo shortcutConfigActivityInfo) {
        this.activityInfo = shortcutConfigActivityInfo;
        this.componentName = shortcutConfigActivityInfo.getComponent();
        this.user = shortcutConfigActivityInfo.getUser();
        this.itemType = shortcutConfigActivityInfo.getItemType();
    }
}
