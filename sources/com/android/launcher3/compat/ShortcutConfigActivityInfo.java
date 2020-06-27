package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;
import com.android.launcher3.C0622R;
import com.android.launcher3.IconCache;
import com.android.launcher3.ShortcutInfo;

public abstract class ShortcutConfigActivityInfo {
    private static final String TAG = "SCActivityInfo";
    private final ComponentName mCn;
    private final UserHandle mUser;

    static class ShortcutConfigActivityInfoVL extends ShortcutConfigActivityInfo {
        private final ActivityInfo mInfo;
        private final PackageManager mPm;

        public ShortcutConfigActivityInfoVL(ActivityInfo activityInfo, PackageManager packageManager) {
            super(new ComponentName(activityInfo.packageName, activityInfo.name), Process.myUserHandle());
            this.mInfo = activityInfo;
            this.mPm = packageManager;
        }

        public CharSequence getLabel() {
            return this.mInfo.loadLabel(this.mPm);
        }

        public Drawable getFullResIcon(IconCache iconCache) {
            return iconCache.getFullResIcon(this.mInfo);
        }
    }

    @TargetApi(26)
    public static class ShortcutConfigActivityInfoVO extends ShortcutConfigActivityInfo {
        private final LauncherActivityInfo mInfo;

        public ShortcutConfigActivityInfoVO(LauncherActivityInfo launcherActivityInfo) {
            super(launcherActivityInfo.getComponentName(), launcherActivityInfo.getUser());
            this.mInfo = launcherActivityInfo;
        }

        public CharSequence getLabel() {
            return this.mInfo.getLabel();
        }

        public Drawable getFullResIcon(IconCache iconCache) {
            return iconCache.getFullResIcon(this.mInfo);
        }

        public boolean startConfigActivity(Activity activity, int i) {
            if (getUser().equals(Process.myUserHandle())) {
                return ShortcutConfigActivityInfo.super.startConfigActivity(activity, i);
            }
            try {
                activity.startIntentSenderForResult(((LauncherApps) activity.getSystemService(LauncherApps.class)).getShortcutConfigActivityIntent(this.mInfo), i, null, 0, 0, 0);
                return true;
            } catch (SendIntentException unused) {
                Toast.makeText(activity, C0622R.string.activity_not_found, 0).show();
                return false;
            }
        }
    }

    public ShortcutInfo createShortcutInfo() {
        return null;
    }

    public abstract Drawable getFullResIcon(IconCache iconCache);

    public int getItemType() {
        return 1;
    }

    public abstract CharSequence getLabel();

    public boolean isPersistable() {
        return true;
    }

    protected ShortcutConfigActivityInfo(ComponentName componentName, UserHandle userHandle) {
        this.mCn = componentName;
        this.mUser = userHandle;
    }

    public ComponentName getComponent() {
        return this.mCn;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public boolean startConfigActivity(Activity activity, int i) {
        Intent component = new Intent("android.intent.action.CREATE_SHORTCUT").setComponent(getComponent());
        try {
            activity.startActivityForResult(component, i);
            return true;
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(activity, C0622R.string.activity_not_found, 0).show();
            return false;
        } catch (SecurityException e) {
            Toast.makeText(activity, C0622R.string.activity_not_found, 0).show();
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Launcher does not have the permission to launch ");
            sb.append(component);
            sb.append(". Make sure to create a MAIN intent-filter for the corresponding activity ");
            sb.append("or use the exported attribute for this activity.");
            Log.e(str, sb.toString(), e);
            return false;
        }
    }
}
