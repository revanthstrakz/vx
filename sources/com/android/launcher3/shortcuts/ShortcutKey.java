package com.android.launcher3.shortcuts;

import android.content.ComponentName;
import android.content.Intent;
import android.os.UserHandle;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.util.ComponentKey;

public class ShortcutKey extends ComponentKey {
    public ShortcutKey(String str, UserHandle userHandle, String str2) {
        super(new ComponentName(str, str2), userHandle);
    }

    public String getId() {
        return this.componentName.getClassName();
    }

    public static ShortcutKey fromInfo(ShortcutInfoCompat shortcutInfoCompat) {
        return new ShortcutKey(shortcutInfoCompat.getPackage(), shortcutInfoCompat.getUserHandle(), shortcutInfoCompat.getId());
    }

    public static ShortcutKey fromIntent(Intent intent, UserHandle userHandle) {
        return new ShortcutKey(intent.getPackage(), userHandle, intent.getStringExtra(ShortcutInfoCompat.EXTRA_SHORTCUT_ID));
    }

    public static ShortcutKey fromItemInfo(ItemInfo itemInfo) {
        return fromIntent(itemInfo.getIntent(), itemInfo.user);
    }
}
