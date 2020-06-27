package com.android.launcher3.model;

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Process;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.AppInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherModel.CallbackTask;
import com.android.launcher3.LauncherModel.Callbacks;
import com.android.launcher3.PromiseAppInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.PackageInstallerCompat.PackageInstallInfo;
import com.android.launcher3.util.InstantAppResolver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class PackageInstallStateChangedTask extends BaseModelUpdateTask {
    private final PackageInstallInfo mInstallInfo;

    public PackageInstallStateChangedTask(PackageInstallInfo packageInstallInfo) {
        this.mInstallInfo = packageInstallInfo;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        if (this.mInstallInfo.state == 0) {
            try {
                ApplicationInfo applicationInfo = launcherAppState.getContext().getPackageManager().getApplicationInfo(this.mInstallInfo.packageName, 0);
                if (InstantAppResolver.newInstance(launcherAppState.getContext()).isInstantApp(applicationInfo)) {
                    launcherAppState.getModel().onPackageAdded(applicationInfo.packageName, Process.myUserHandle());
                }
            } catch (NameNotFoundException unused) {
            }
            return;
        }
        synchronized (allAppsList) {
            final PromiseAppInfo promiseAppInfo = null;
            final ArrayList arrayList = new ArrayList();
            for (int i = 0; i < allAppsList.size(); i++) {
                AppInfo appInfo = allAppsList.get(i);
                ComponentName targetComponent = appInfo.getTargetComponent();
                if (targetComponent != null && targetComponent.getPackageName().equals(this.mInstallInfo.packageName) && (appInfo instanceof PromiseAppInfo)) {
                    PromiseAppInfo promiseAppInfo2 = (PromiseAppInfo) appInfo;
                    if (this.mInstallInfo.state == 1) {
                        promiseAppInfo2.level = this.mInstallInfo.progress;
                        promiseAppInfo = promiseAppInfo2;
                    } else if (this.mInstallInfo.state == 2) {
                        allAppsList.removePromiseApp(appInfo);
                        arrayList.add(appInfo);
                    }
                }
            }
            if (promiseAppInfo != null) {
                scheduleCallbackTask(new CallbackTask() {
                    public void execute(Callbacks callbacks) {
                        callbacks.bindPromiseAppProgressUpdated(promiseAppInfo);
                    }
                });
            }
            if (!arrayList.isEmpty()) {
                scheduleCallbackTask(new CallbackTask() {
                    public void execute(Callbacks callbacks) {
                        callbacks.bindAppInfosRemoved(arrayList);
                    }
                });
            }
        }
        synchronized (bgDataModel) {
            final HashSet hashSet = new HashSet();
            Iterator it = bgDataModel.itemsIdMap.iterator();
            while (it.hasNext()) {
                ItemInfo itemInfo = (ItemInfo) it.next();
                if (itemInfo instanceof ShortcutInfo) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                    ComponentName targetComponent2 = shortcutInfo.getTargetComponent();
                    if (shortcutInfo.hasPromiseIconUi() && targetComponent2 != null && this.mInstallInfo.packageName.equals(targetComponent2.getPackageName())) {
                        shortcutInfo.setInstallProgress(this.mInstallInfo.progress);
                        if (this.mInstallInfo.state == 2) {
                            shortcutInfo.status &= -5;
                        }
                        hashSet.add(shortcutInfo);
                    }
                }
            }
            Iterator it2 = bgDataModel.appWidgets.iterator();
            while (it2.hasNext()) {
                LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) it2.next();
                if (launcherAppWidgetInfo.providerName.getPackageName().equals(this.mInstallInfo.packageName)) {
                    launcherAppWidgetInfo.installProgress = this.mInstallInfo.progress;
                    hashSet.add(launcherAppWidgetInfo);
                }
            }
            if (!hashSet.isEmpty()) {
                scheduleCallbackTask(new CallbackTask() {
                    public void execute(Callbacks callbacks) {
                        callbacks.bindRestoreItemsChange(hashSet);
                    }
                });
            }
        }
    }
}
