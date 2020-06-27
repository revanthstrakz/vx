package com.android.launcher3.model;

import com.android.launcher3.ItemInfoWithIcon;

public class PackageItemInfo extends ItemInfoWithIcon {
    public String packageName;

    public PackageItemInfo(String str) {
        this.packageName = str;
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.dumpProperties());
        sb.append(" packageName=");
        sb.append(this.packageName);
        return sb.toString();
    }
}
