package com.android.launcher3;

import android.content.Context;
import android.provider.Settings.Global;
import android.util.AttributeSet;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.UninstallDropTarget.DropTargetResultCallback;
import com.android.launcher3.util.Themes;

public class InfoDropTarget extends UninstallDropTarget {
    private static final String TAG = "InfoDropTarget";

    public InfoDropTarget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public InfoDropTarget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void setupUi() {
        this.mHoverColor = Themes.getColorAccent(getContext());
        setDrawable(C0622R.C0624drawable.ic_info_shadow);
    }

    public void completeDrop(DragObject dragObject) {
        startDetailsActivityForInfo(dragObject.dragInfo, this.mLauncher, dragObject.dragSource instanceof DropTargetResultCallback ? (DropTargetResultCallback) dragObject.dragSource : null);
    }

    public static boolean startDetailsActivityForInfo(ItemInfo itemInfo, Launcher launcher, DropTargetResultCallback dropTargetResultCallback) {
        return startDetailsActivityForInfo(itemInfo, launcher, dropTargetResultCallback, null, null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0057  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean startDetailsActivityForInfo(com.android.launcher3.ItemInfo r5, com.android.launcher3.Launcher r6, com.android.launcher3.UninstallDropTarget.DropTargetResultCallback r7, android.graphics.Rect r8, android.os.Bundle r9) {
        /*
            boolean r0 = r5 instanceof com.android.launcher3.PromiseAppInfo
            r1 = 1
            if (r0 == 0) goto L_0x000f
            com.android.launcher3.PromiseAppInfo r5 = (com.android.launcher3.PromiseAppInfo) r5
            android.content.Intent r5 = r5.getMarketIntent()
            r6.startActivity(r5)
            return r1
        L_0x000f:
            r0 = 0
            boolean r2 = r5 instanceof com.android.launcher3.AppInfo
            if (r2 == 0) goto L_0x001a
            r0 = r5
            com.android.launcher3.AppInfo r0 = (com.android.launcher3.AppInfo) r0
            android.content.ComponentName r0 = r0.componentName
            goto L_0x0036
        L_0x001a:
            boolean r2 = r5 instanceof com.android.launcher3.ShortcutInfo
            if (r2 == 0) goto L_0x0023
            android.content.ComponentName r0 = r5.getTargetComponent()
            goto L_0x0036
        L_0x0023:
            boolean r2 = r5 instanceof com.android.launcher3.PendingAddItemInfo
            if (r2 == 0) goto L_0x002d
            r0 = r5
            com.android.launcher3.PendingAddItemInfo r0 = (com.android.launcher3.PendingAddItemInfo) r0
            android.content.ComponentName r0 = r0.componentName
            goto L_0x0036
        L_0x002d:
            boolean r2 = r5 instanceof com.android.launcher3.LauncherAppWidgetInfo
            if (r2 == 0) goto L_0x0036
            r0 = r5
            com.android.launcher3.LauncherAppWidgetInfo r0 = (com.android.launcher3.LauncherAppWidgetInfo) r0
            android.content.ComponentName r0 = r0.providerName
        L_0x0036:
            r2 = 0
            if (r0 == 0) goto L_0x0054
            com.android.launcher3.compat.LauncherAppsCompat r3 = com.android.launcher3.compat.LauncherAppsCompat.getInstance(r6)     // Catch:{ ActivityNotFoundException | SecurityException -> 0x0043 }
            android.os.UserHandle r4 = r5.user     // Catch:{ ActivityNotFoundException | SecurityException -> 0x0043 }
            r3.showAppDetailsForProfile(r0, r4, r8, r9)     // Catch:{ ActivityNotFoundException | SecurityException -> 0x0043 }
            goto L_0x0055
        L_0x0043:
            r8 = move-exception
            int r9 = com.android.launcher3.C0622R.string.activity_not_found
            android.widget.Toast r9 = android.widget.Toast.makeText(r6, r9, r2)
            r9.show()
            java.lang.String r9 = "InfoDropTarget"
            java.lang.String r1 = "Unable to launch settings"
            android.util.Log.e(r9, r1, r8)
        L_0x0054:
            r1 = 0
        L_0x0055:
            if (r7 == 0) goto L_0x005c
            android.os.UserHandle r5 = r5.user
            sendUninstallResult(r6, r1, r0, r5, r7)
        L_0x005c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.InfoDropTarget.startDetailsActivityForInfo(com.android.launcher3.ItemInfo, com.android.launcher3.Launcher, com.android.launcher3.UninstallDropTarget$DropTargetResultCallback, android.graphics.Rect, android.os.Bundle):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean supportsDrop(DragSource dragSource, ItemInfo itemInfo) {
        return dragSource.supportsAppInfoDropTarget() && supportsDrop(getContext(), itemInfo);
    }

    public static boolean supportsDrop(Context context, ItemInfo itemInfo) {
        boolean z = true;
        if (!(Global.getInt(context.getContentResolver(), "development_settings_enabled", 0) == 1)) {
            return false;
        }
        if (itemInfo.itemType == 1 || (!(itemInfo instanceof AppInfo) && ((!(itemInfo instanceof ShortcutInfo) || ((ShortcutInfo) itemInfo).isPromise()) && ((!(itemInfo instanceof LauncherAppWidgetInfo) || ((LauncherAppWidgetInfo) itemInfo).restoreStatus != 0) && !(itemInfo instanceof PendingAddItemInfo))))) {
            z = false;
        }
        return z;
    }
}
