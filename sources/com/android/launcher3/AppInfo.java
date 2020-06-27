package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.os.UserHandle;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageManagerHelper;

public class AppInfo extends ItemInfoWithIcon {
    public static final int FLAG_SYSTEM_NO = 2;
    public static final int FLAG_SYSTEM_UNKNOWN = 0;
    public static final int FLAG_SYSTEM_YES = 1;
    public ComponentName componentName;
    public Intent intent;
    public int isDisabled;
    public int isSystemApp;

    public AppInfo() {
        this.isDisabled = 0;
        this.itemType = 0;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public AppInfo(Context context, LauncherActivityInfo launcherActivityInfo, UserHandle userHandle) {
        this(launcherActivityInfo, userHandle, UserManagerCompat.getInstance(context).isQuietModeEnabled(userHandle));
    }

    public AppInfo(LauncherActivityInfo launcherActivityInfo, UserHandle userHandle, boolean z) {
        this.isDisabled = 0;
        this.componentName = launcherActivityInfo.getComponentName();
        this.container = -1;
        this.user = userHandle;
        if (PackageManagerHelper.isAppSuspended(launcherActivityInfo.getApplicationInfo())) {
            this.isDisabled |= 4;
        }
        if (z) {
            this.isDisabled |= 8;
        }
        this.intent = makeLaunchIntent(launcherActivityInfo);
        int i = 1;
        if ((launcherActivityInfo.getApplicationInfo().flags & 1) == 0) {
            i = 2;
        }
        this.isSystemApp = i;
    }

    public AppInfo(AppInfo appInfo) {
        super(appInfo);
        this.isDisabled = 0;
        this.componentName = appInfo.componentName;
        this.title = Utilities.trim(appInfo.title);
        this.intent = new Intent(appInfo.intent);
        this.isDisabled = appInfo.isDisabled;
        this.isSystemApp = appInfo.isSystemApp;
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.dumpProperties());
        sb.append(" componentName=");
        sb.append(this.componentName);
        return sb.toString();
    }

    public ShortcutInfo makeShortcut() {
        return new ShortcutInfo(this);
    }

    public ComponentKey toComponentKey() {
        return new ComponentKey(this.componentName, this.user);
    }

    public static Intent makeLaunchIntent(LauncherActivityInfo launcherActivityInfo) {
        return makeLaunchIntent(launcherActivityInfo.getComponentName());
    }

    public static Intent makeLaunchIntent(ComponentName componentName2) {
        return new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(componentName2).setFlags(270532608);
    }

    public boolean isDisabled() {
        return this.isDisabled != 0;
    }
}
