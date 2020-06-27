package com.android.launcher3;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.android.launcher3.compat.PackageInstallerCompat.PackageInstallInfo;
import com.android.launcher3.util.PackageManagerHelper;

public class PromiseAppInfo extends AppInfo {
    public int level = 0;

    public PromiseAppInfo(@NonNull PackageInstallInfo packageInstallInfo) {
        this.componentName = packageInstallInfo.componentName;
        this.intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(this.componentName).setFlags(270532608);
    }

    public ShortcutInfo makeShortcut() {
        ShortcutInfo shortcutInfo = new ShortcutInfo((AppInfo) this);
        shortcutInfo.setInstallProgress(this.level);
        shortcutInfo.status |= 2;
        shortcutInfo.status |= 8;
        return shortcutInfo;
    }

    public Intent getMarketIntent() {
        return PackageManagerHelper.getMarketIntent(this.componentName.getPackageName());
    }
}
