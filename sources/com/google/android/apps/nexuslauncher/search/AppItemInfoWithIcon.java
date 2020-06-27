package com.google.android.apps.nexuslauncher.search;

import android.content.Intent;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.util.ComponentKey;

public class AppItemInfoWithIcon extends ItemInfoWithIcon {
    private Intent mIntent;

    public AppItemInfoWithIcon(ComponentKey componentKey) {
        this.mIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(componentKey.componentName).addFlags(270532608);
        this.user = componentKey.user;
        this.itemType = 0;
    }

    public Intent getIntent() {
        return this.mIntent;
    }
}
