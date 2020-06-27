package com.android.launcher3.model;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import com.android.launcher3.AppFilter;
import com.android.launcher3.IconCache;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class WidgetsModel {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetsModel";
    private AppFilter mAppFilter;
    private final MultiHashMap<PackageItemInfo, WidgetItem> mWidgetsList = new MultiHashMap<>();

    public synchronized MultiHashMap<PackageItemInfo, WidgetItem> getWidgetsMap() {
        return this.mWidgetsList.clone();
    }

    public void update(LauncherAppState launcherAppState, @Nullable PackageUserKey packageUserKey) {
        Preconditions.assertWorkerThread();
        Context context = launcherAppState.getContext();
        ArrayList arrayList = new ArrayList();
        try {
            PackageManager packageManager = context.getPackageManager();
            InvariantDeviceProfile invariantDeviceProfile = launcherAppState.getInvariantDeviceProfile();
            for (AppWidgetProviderInfo fromProviderInfo : AppWidgetManagerCompat.getInstance(context).getAllProviders(packageUserKey)) {
                arrayList.add(new WidgetItem(LauncherAppWidgetProviderInfo.fromProviderInfo(context, fromProviderInfo), packageManager, invariantDeviceProfile));
            }
            for (ShortcutConfigActivityInfo widgetItem : LauncherAppsCompat.getInstance(context).getCustomShortcutActivityList(packageUserKey)) {
                arrayList.add(new WidgetItem(widgetItem));
            }
            setWidgetsAndShortcuts(arrayList, launcherAppState, packageUserKey);
        } catch (Exception e) {
            if (!Utilities.isBinderSizeError(e)) {
                throw e;
            }
        }
        launcherAppState.getWidgetCache().removeObsoletePreviews(arrayList, packageUserKey);
    }

    private synchronized void setWidgetsAndShortcuts(ArrayList<WidgetItem> arrayList, LauncherAppState launcherAppState, @Nullable PackageUserKey packageUserKey) {
        HashMap hashMap = new HashMap();
        if (packageUserKey == null) {
            this.mWidgetsList.clear();
        } else {
            PackageItemInfo packageItemInfo = null;
            Iterator it = this.mWidgetsList.keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                PackageItemInfo packageItemInfo2 = (PackageItemInfo) it.next();
                if (packageItemInfo2.packageName.equals(packageUserKey.mPackageName)) {
                    packageItemInfo = packageItemInfo2;
                    break;
                }
            }
            if (packageItemInfo != null) {
                hashMap.put(packageItemInfo.packageName, packageItemInfo);
                Iterator it2 = ((ArrayList) this.mWidgetsList.get(packageItemInfo)).iterator();
                while (it2.hasNext()) {
                    WidgetItem widgetItem = (WidgetItem) it2.next();
                    if (widgetItem.componentName.getPackageName().equals(packageUserKey.mPackageName) && widgetItem.user.equals(packageUserKey.mUser)) {
                        it2.remove();
                    }
                }
            }
        }
        InvariantDeviceProfile invariantDeviceProfile = launcherAppState.getInvariantDeviceProfile();
        UserHandle myUserHandle = Process.myUserHandle();
        Iterator it3 = arrayList.iterator();
        while (it3.hasNext()) {
            WidgetItem widgetItem2 = (WidgetItem) it3.next();
            if (widgetItem2.widgetInfo != null) {
                int min = Math.min(widgetItem2.widgetInfo.spanX, widgetItem2.widgetInfo.minSpanX);
                int min2 = Math.min(widgetItem2.widgetInfo.spanY, widgetItem2.widgetInfo.minSpanY);
                if (min <= invariantDeviceProfile.numColumns) {
                    if (min2 > invariantDeviceProfile.numRows) {
                    }
                }
            }
            if (this.mAppFilter == null) {
                this.mAppFilter = AppFilter.newInstance(launcherAppState.getContext());
            }
            if (this.mAppFilter.shouldShowApp(widgetItem2.componentName, widgetItem2.user)) {
                String packageName = widgetItem2.componentName.getPackageName();
                PackageItemInfo packageItemInfo3 = (PackageItemInfo) hashMap.get(packageName);
                if (packageItemInfo3 == null) {
                    packageItemInfo3 = new PackageItemInfo(packageName);
                    packageItemInfo3.user = widgetItem2.user;
                    hashMap.put(packageName, packageItemInfo3);
                } else if (!myUserHandle.equals(packageItemInfo3.user)) {
                    packageItemInfo3.user = widgetItem2.user;
                }
                this.mWidgetsList.addToList(packageItemInfo3, widgetItem2);
            }
        }
        IconCache iconCache = launcherAppState.getIconCache();
        for (PackageItemInfo titleAndIconForApp : hashMap.values()) {
            iconCache.getTitleAndIconForApp(titleAndIconForApp, true);
        }
    }
}
