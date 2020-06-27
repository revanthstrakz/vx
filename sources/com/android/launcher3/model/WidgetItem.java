package com.android.launcher3.model;

import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.util.ComponentKey;
import java.text.Collator;

public class WidgetItem extends ComponentKey implements Comparable<WidgetItem> {
    private static Collator sCollator;
    private static UserHandle sMyUserHandle;
    public final ShortcutConfigActivityInfo activityInfo;
    public final String label;
    public final int spanX;
    public final int spanY;
    public final LauncherAppWidgetProviderInfo widgetInfo;

    public WidgetItem(LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, PackageManager packageManager, InvariantDeviceProfile invariantDeviceProfile) {
        super(launcherAppWidgetProviderInfo.provider, launcherAppWidgetProviderInfo.getProfile());
        this.label = Utilities.trim(launcherAppWidgetProviderInfo.getLabel(packageManager));
        this.widgetInfo = launcherAppWidgetProviderInfo;
        this.activityInfo = null;
        this.spanX = Math.min(launcherAppWidgetProviderInfo.spanX, invariantDeviceProfile.numColumns);
        this.spanY = Math.min(launcherAppWidgetProviderInfo.spanY, invariantDeviceProfile.numRows);
    }

    public WidgetItem(ShortcutConfigActivityInfo shortcutConfigActivityInfo) {
        super(shortcutConfigActivityInfo.getComponent(), shortcutConfigActivityInfo.getUser());
        this.label = Utilities.trim(shortcutConfigActivityInfo.getLabel());
        this.widgetInfo = null;
        this.activityInfo = shortcutConfigActivityInfo;
        this.spanY = 1;
        this.spanX = 1;
    }

    public int compareTo(WidgetItem widgetItem) {
        int i;
        if (sMyUserHandle == null) {
            sMyUserHandle = Process.myUserHandle();
            sCollator = Collator.getInstance();
        }
        int i2 = 1;
        boolean z = !sMyUserHandle.equals(this.user);
        if ((!sMyUserHandle.equals(widgetItem.user)) ^ z) {
            if (!z) {
                i2 = -1;
            }
            return i2;
        }
        int compare = sCollator.compare(this.label, widgetItem.label);
        if (compare != 0) {
            return compare;
        }
        int i3 = this.spanX * this.spanY;
        int i4 = widgetItem.spanX * widgetItem.spanY;
        if (i3 == i4) {
            i = Integer.compare(this.spanY, widgetItem.spanY);
        } else {
            i = Integer.compare(i3, i4);
        }
        return i;
    }
}
