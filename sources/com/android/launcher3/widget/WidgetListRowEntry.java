package com.android.launcher3.widget;

import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.WidgetItem;
import com.microsoft.appcenter.Constants;
import java.util.ArrayList;

public class WidgetListRowEntry {
    public final PackageItemInfo pkgItem;
    public String titleSectionName;
    public final ArrayList<WidgetItem> widgets;

    public WidgetListRowEntry(PackageItemInfo packageItemInfo, ArrayList<WidgetItem> arrayList) {
        this.pkgItem = packageItemInfo;
        this.widgets = arrayList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.pkgItem.packageName);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(this.widgets.size());
        return sb.toString();
    }
}
