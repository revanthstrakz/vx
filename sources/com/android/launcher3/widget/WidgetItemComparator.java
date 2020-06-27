package com.android.launcher3.widget;

import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.model.WidgetItem;
import java.text.Collator;
import java.util.Comparator;

public class WidgetItemComparator implements Comparator<WidgetItem> {
    private final Collator mCollator = Collator.getInstance();
    private final UserHandle mMyUserHandle = Process.myUserHandle();

    public int compare(WidgetItem widgetItem, WidgetItem widgetItem2) {
        int i;
        int i2 = 1;
        boolean z = !this.mMyUserHandle.equals(widgetItem.user);
        if ((!this.mMyUserHandle.equals(widgetItem2.user)) ^ z) {
            if (!z) {
                i2 = -1;
            }
            return i2;
        }
        int compare = this.mCollator.compare(widgetItem.label, widgetItem2.label);
        if (compare != 0) {
            return compare;
        }
        int i3 = widgetItem.spanX * widgetItem.spanY;
        int i4 = widgetItem2.spanX * widgetItem2.spanY;
        if (i3 == i4) {
            i = Integer.compare(widgetItem.spanY, widgetItem2.spanY);
        } else {
            i = Integer.compare(i3, i4);
        }
        return i;
    }
}
