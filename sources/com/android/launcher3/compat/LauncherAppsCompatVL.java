package com.android.launcher3.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.LauncherApps.Callback;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.List;

public class LauncherAppsCompatVL extends LauncherAppsCompat {
    private final ArrayMap<OnAppsChangedCallbackCompat, WrappedCallback> mCallbacks = new ArrayMap<>();
    protected final Context mContext;
    protected final LauncherApps mLauncherApps;

    private static class WrappedCallback extends Callback {
        private final OnAppsChangedCallbackCompat mCallback;

        public WrappedCallback(OnAppsChangedCallbackCompat onAppsChangedCallbackCompat) {
            this.mCallback = onAppsChangedCallbackCompat;
        }

        public void onPackageRemoved(String str, UserHandle userHandle) {
            this.mCallback.onPackageRemoved(str, userHandle);
        }

        public void onPackageAdded(String str, UserHandle userHandle) {
            this.mCallback.onPackageAdded(str, userHandle);
        }

        public void onPackageChanged(String str, UserHandle userHandle) {
            this.mCallback.onPackageChanged(str, userHandle);
        }

        public void onPackagesAvailable(String[] strArr, UserHandle userHandle, boolean z) {
            this.mCallback.onPackagesAvailable(strArr, userHandle, z);
        }

        public void onPackagesUnavailable(String[] strArr, UserHandle userHandle, boolean z) {
            this.mCallback.onPackagesUnavailable(strArr, userHandle, z);
        }

        public void onPackagesSuspended(String[] strArr, UserHandle userHandle) {
            this.mCallback.onPackagesSuspended(strArr, userHandle);
        }

        public void onPackagesUnsuspended(String[] strArr, UserHandle userHandle) {
            this.mCallback.onPackagesUnsuspended(strArr, userHandle);
        }

        public void onShortcutsChanged(@NonNull String str, @NonNull List<ShortcutInfo> list, @NonNull UserHandle userHandle) {
            ArrayList arrayList = new ArrayList(list.size());
            for (ShortcutInfo shortcutInfoCompat : list) {
                arrayList.add(new ShortcutInfoCompat(shortcutInfoCompat));
            }
            this.mCallback.onShortcutsChanged(str, arrayList, userHandle);
        }
    }

    LauncherAppsCompatVL(Context context) {
        this.mContext = context;
        this.mLauncherApps = (LauncherApps) context.getSystemService("launcherapps");
    }

    public List<LauncherActivityInfo> getActivityList(String str, UserHandle userHandle) {
        return this.mLauncherApps.getActivityList(str, userHandle);
    }

    public LauncherActivityInfo resolveActivity(Intent intent, UserHandle userHandle) {
        return this.mLauncherApps.resolveActivity(intent, userHandle);
    }

    public void startActivityForProfile(ComponentName componentName, UserHandle userHandle, Rect rect, Bundle bundle) {
        this.mLauncherApps.startMainActivity(componentName, userHandle, rect, bundle);
    }

    public ApplicationInfo getApplicationInfo(String str, int i, UserHandle userHandle) {
        boolean equals = Process.myUserHandle().equals(userHandle);
        ApplicationInfo applicationInfo = null;
        if (equals || i != 0) {
            try {
                ApplicationInfo applicationInfo2 = this.mContext.getPackageManager().getApplicationInfo(str, i);
                if ((!equals || (applicationInfo2.flags & 8388608) != 0) && applicationInfo2.enabled) {
                    return applicationInfo2;
                }
                return null;
            } catch (NameNotFoundException unused) {
                return null;
            }
        } else {
            List activityList = this.mLauncherApps.getActivityList(str, userHandle);
            if (activityList.size() > 0) {
                applicationInfo = ((LauncherActivityInfo) activityList.get(0)).getApplicationInfo();
            }
            return applicationInfo;
        }
    }

    public void showAppDetailsForProfile(ComponentName componentName, UserHandle userHandle, Rect rect, Bundle bundle) {
        this.mLauncherApps.startAppDetailsActivity(componentName, userHandle, rect, bundle);
    }

    public void addOnAppsChangedCallback(OnAppsChangedCallbackCompat onAppsChangedCallbackCompat) {
        WrappedCallback wrappedCallback = new WrappedCallback(onAppsChangedCallbackCompat);
        synchronized (this.mCallbacks) {
            this.mCallbacks.put(onAppsChangedCallbackCompat, wrappedCallback);
        }
        this.mLauncherApps.registerCallback(wrappedCallback);
    }

    public void removeOnAppsChangedCallback(OnAppsChangedCallbackCompat onAppsChangedCallbackCompat) {
        WrappedCallback wrappedCallback;
        synchronized (this.mCallbacks) {
            wrappedCallback = (WrappedCallback) this.mCallbacks.remove(onAppsChangedCallbackCompat);
        }
        if (wrappedCallback != null) {
            this.mLauncherApps.unregisterCallback(wrappedCallback);
        }
    }

    public boolean isPackageEnabledForProfile(String str, UserHandle userHandle) {
        return this.mLauncherApps.isPackageEnabled(str, userHandle);
    }

    public boolean isActivityEnabledForProfile(ComponentName componentName, UserHandle userHandle) {
        return this.mLauncherApps.isActivityEnabled(componentName, userHandle);
    }

    public List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(@Nullable PackageUserKey packageUserKey) {
        ArrayList arrayList = new ArrayList();
        if (packageUserKey != null && !packageUserKey.mUser.equals(Process.myUserHandle())) {
            return arrayList;
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(new Intent("android.intent.action.CREATE_SHORTCUT"), 0)) {
            if (packageUserKey == null || packageUserKey.mPackageName.equals(resolveInfo.activityInfo.packageName)) {
                arrayList.add(new ShortcutConfigActivityInfoVL(resolveInfo.activityInfo, packageManager));
            }
        }
        return arrayList;
    }
}
