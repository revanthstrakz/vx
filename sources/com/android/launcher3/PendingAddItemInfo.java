package com.android.launcher3;

import android.content.ComponentName;

public class PendingAddItemInfo extends ItemInfo {
    public ComponentName componentName;

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.dumpProperties());
        sb.append(" componentName=");
        sb.append(this.componentName);
        return sb.toString();
    }
}
