package com.android.launcher3.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat;
import com.android.launcher3.util.PackageUserKey;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.core.VirtualCore.PackageObserver;
import com.lody.virtual.client.ipc.VPackageManager;
import com.lody.virtual.p007os.VUserInfo;
import com.lody.virtual.p007os.VUserManager;
import com.lody.virtual.remote.InstalledAppInfo;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LauncherAppsCompatForVA extends LauncherAppsCompatVL {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String TAG = "LauncherAppsCompatForVA";
    private PackageObserver mPackageObserver;
    private final VirtualCore mVirtualCore = VirtualCore.get();
    private boolean showSystemApp = isLauncherEnable(this.mVirtualCore.getContext());

    public boolean isActivityEnabledForProfile(ComponentName componentName, UserHandle userHandle) {
        return true;
    }

    LauncherAppsCompatForVA() {
        super(VirtualCore.get().getContext());
    }

    private boolean isLauncherEnable(Context context) {
        if (context == null) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null && packageManager.getComponentEnabledSetting(new ComponentName(context.getPackageName(), "vxp.launcher")) == 1) {
            return true;
        }
        return false;
    }

    public List<LauncherActivityInfo> getActivityList(String str, UserHandle userHandle) {
        int userId = UserManagerCompat.toUserId(userHandle);
        ArrayList arrayList = new ArrayList();
        if (this.showSystemApp) {
            try {
                arrayList.addAll(super.getActivityList(str, userHandle));
            } catch (Throwable th) {
                Log.w(TAG, "add super failed", th);
            }
        }
        if (str == null) {
            for (InstalledAppInfo installedAppInfo : this.mVirtualCore.getInstalledAppsAsUser(userId, 0)) {
                arrayList.addAll(getActivityListForPackage(installedAppInfo.packageName));
            }
        } else {
            arrayList.addAll(getActivityListForPackage(str));
        }
        return arrayList;
    }

    public LauncherActivityInfo resolveActivity(Intent intent, UserHandle userHandle) {
        List list;
        Context context = this.mVirtualCore.getContext();
        int userId = UserManagerCompat.toUserId(userHandle);
        VPackageManager vPackageManager = VPackageManager.get();
        try {
            list = vPackageManager.queryIntentActivities(intent, intent.resolveType(context), 0, userId);
        } catch (Throwable th) {
            th.printStackTrace();
            list = null;
        }
        if (list == null || list.size() <= 0) {
            intent.removeCategory("android.intent.category.INFO");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(intent.getPackage());
            list = vPackageManager.queryIntentActivities(intent, intent.resolveType(context), 0, userId);
        }
        if (list != null && list.size() > 0) {
            try {
                return makeLauncherActivityInfo(context, (ResolveInfo) list.get(0), Process.myUserHandle());
            } catch (Throwable unused) {
                return null;
            }
        } else if (!this.showSystemApp) {
            return null;
        } else {
            try {
                return super.resolveActivity(intent, userHandle);
            } catch (Throwable unused2) {
                return null;
            }
        }
    }

    public void startActivityForProfile(ComponentName componentName, UserHandle userHandle, Rect rect, Bundle bundle) {
        if (this.showSystemApp) {
            try {
                super.startActivityForProfile(componentName, userHandle, rect, bundle);
            } catch (Throwable th) {
                Log.e(TAG, "startActivityForProfile", th);
            }
        }
    }

    public ApplicationInfo getApplicationInfo(String str, int i, UserHandle userHandle) {
        InstalledAppInfo installedAppInfo = this.mVirtualCore.getInstalledAppInfo(str, i);
        if (installedAppInfo != null) {
            return installedAppInfo.getApplicationInfo(0);
        }
        if (!this.showSystemApp) {
            return null;
        }
        try {
            return super.getApplicationInfo(str, i, userHandle);
        } catch (Throwable th) {
            Log.e(TAG, "getApplicationInfo", th);
            return null;
        }
    }

    public void showAppDetailsForProfile(ComponentName componentName, UserHandle userHandle, Rect rect, Bundle bundle) {
        if (this.showSystemApp) {
            try {
                super.showAppDetailsForProfile(componentName, userHandle, rect, bundle);
            } catch (Throwable th) {
                Log.e(TAG, "showAppDetailsForProfile", th);
            }
        }
    }

    public void addOnAppsChangedCallback(final OnAppsChangedCallbackCompat onAppsChangedCallbackCompat) {
        this.mPackageObserver = new PackageObserver() {
            public void onPackageInstalled(String str) throws RemoteException {
                onAppsChangedCallbackCompat.onPackageAdded(str, UserManagerCompat.fromUserId(0));
            }

            public void onPackageUninstalled(String str) throws RemoteException {
                onAppsChangedCallbackCompat.onPackageRemoved(str, UserManagerCompat.fromUserId(0));
            }

            public void onPackageInstalledAsUser(int i, String str) throws RemoteException {
                onAppsChangedCallbackCompat.onPackageAdded(str, UserManagerCompat.fromUserId(i));
            }

            public void onPackageUninstalledAsUser(int i, String str) throws RemoteException {
                onAppsChangedCallbackCompat.onPackageRemoved(str, UserManagerCompat.fromUserId(i));
            }
        };
        try {
            this.mVirtualCore.registerObserver(this.mPackageObserver);
        } catch (Throwable unused) {
            new Handler().postDelayed(new Runnable() {
                public final void run() {
                    LauncherAppsCompatForVA.lambda$addOnAppsChangedCallback$10(LauncherAppsCompatForVA.this);
                }
            }, 1000);
        }
        if (this.showSystemApp) {
            try {
                super.addOnAppsChangedCallback(onAppsChangedCallbackCompat);
            } catch (Throwable unused2) {
            }
        }
    }

    public static /* synthetic */ void lambda$addOnAppsChangedCallback$10(LauncherAppsCompatForVA launcherAppsCompatForVA) {
        try {
            launcherAppsCompatForVA.mVirtualCore.registerObserver(launcherAppsCompatForVA.mPackageObserver);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void removeOnAppsChangedCallback(OnAppsChangedCallbackCompat onAppsChangedCallbackCompat) {
        if (this.mPackageObserver != null) {
            this.mVirtualCore.unregisterObserver(this.mPackageObserver);
        }
        if (this.showSystemApp) {
            try {
                super.removeOnAppsChangedCallback(onAppsChangedCallbackCompat);
            } catch (Throwable unused) {
            }
        }
    }

    public boolean isPackageEnabledForProfile(String str, UserHandle userHandle) {
        if (this.mVirtualCore.isAppInstalled(str)) {
            return true;
        }
        if (!this.showSystemApp) {
            return false;
        }
        try {
            return super.isPackageEnabledForProfile(str, userHandle);
        } catch (Throwable unused) {
            return false;
        }
    }

    public List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(@Nullable PackageUserKey packageUserKey) {
        try {
            return super.getCustomShortcutActivityList(packageUserKey);
        } catch (Throwable unused) {
            return Collections.emptyList();
        }
    }

    private List<LauncherActivityInfo> getActivityListForPackage(String str) {
        ArrayList arrayList = new ArrayList();
        for (VUserInfo vUserInfo : VUserManager.get().getUsers()) {
            arrayList.addAll(getActivityListForPackageAsUser(str, vUserInfo.f180id));
        }
        return arrayList;
    }

    private List<LauncherActivityInfo> getActivityListForPackageAsUser(String str, int i) {
        ArrayList arrayList = new ArrayList();
        Context context = this.mVirtualCore.getContext();
        VPackageManager vPackageManager = VPackageManager.get();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.INFO");
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = vPackageManager.queryIntentActivities(intent, intent.resolveType(context), 0, i);
        if (queryIntentActivities == null || queryIntentActivities.size() <= 0) {
            intent.removeCategory("android.intent.category.INFO");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(str);
            queryIntentActivities = vPackageManager.queryIntentActivities(intent, intent.resolveType(context), 0, i);
        }
        if (queryIntentActivities == null || queryIntentActivities.size() == 0) {
            return arrayList;
        }
        ResolveInfo resolveInfo = (ResolveInfo) queryIntentActivities.get(0);
        Iterator it = queryIntentActivities.iterator();
        while (it.hasNext()) {
            ResolveInfo resolveInfo2 = (ResolveInfo) it.next();
            if (resolveInfo2.activityInfo.targetActivity != null) {
                it.remove();
            } else if (!resolveInfo2.activityInfo.enabled) {
                it.remove();
            }
        }
        if (queryIntentActivities.size() == 0) {
            queryIntentActivities.add(resolveInfo);
        }
        for (ResolveInfo makeLauncherActivityInfo : queryIntentActivities) {
            try {
                arrayList.add(makeLauncherActivityInfo(context, makeLauncherActivityInfo, UserManagerCompat.fromUserId(i)));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return arrayList;
    }

    private static LauncherActivityInfo makeLauncherActivityInfo(Context context, ResolveInfo resolveInfo, UserHandle userHandle) {
        try {
            if (VERSION.SDK_INT >= 24) {
                Constructor declaredConstructor = LauncherActivityInfo.class.getDeclaredConstructor(new Class[]{Context.class, ActivityInfo.class, UserHandle.class});
                declaredConstructor.setAccessible(true);
                return (LauncherActivityInfo) declaredConstructor.newInstance(new Object[]{context, resolveInfo.activityInfo, userHandle});
            } else if (VERSION.SDK_INT >= 21) {
                Constructor declaredConstructor2 = LauncherActivityInfo.class.getDeclaredConstructor(new Class[]{Context.class, ResolveInfo.class, UserHandle.class, Long.TYPE});
                declaredConstructor2.setAccessible(true);
                return (LauncherActivityInfo) declaredConstructor2.newInstance(new Object[]{context, resolveInfo, userHandle, Long.valueOf(System.currentTimeMillis())});
            } else {
                throw new RuntimeException("can not construct launcher activity info");
            }
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }
}
