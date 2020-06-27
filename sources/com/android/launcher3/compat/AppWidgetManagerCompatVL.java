package com.android.launcher3.compat;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.annotation.Nullable;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageUserKey;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class AppWidgetManagerCompatVL extends AppWidgetManagerCompat {
    private final UserManager mUserManager;

    AppWidgetManagerCompatVL(Context context) {
        super(context);
        this.mUserManager = (UserManager) context.getSystemService(ServiceManagerNative.USER);
    }

    public List<AppWidgetProviderInfo> getAllProviders(@Nullable PackageUserKey packageUserKey) {
        if (packageUserKey == null) {
            ArrayList arrayList = new ArrayList();
            for (UserHandle installedProvidersForProfile : this.mUserManager.getUserProfiles()) {
                arrayList.addAll(this.mAppWidgetManager.getInstalledProvidersForProfile(installedProvidersForProfile));
            }
            return arrayList;
        }
        try {
            ArrayList arrayList2 = new ArrayList(this.mAppWidgetManager.getInstalledProvidersForProfile(packageUserKey.mUser));
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                if (!((AppWidgetProviderInfo) it.next()).provider.getPackageName().equals(packageUserKey.mPackageName)) {
                    it.remove();
                }
            }
            return arrayList2;
        } catch (Throwable th) {
            th.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean bindAppWidgetIdIfAllowed(int i, AppWidgetProviderInfo appWidgetProviderInfo, Bundle bundle) {
        return this.mAppWidgetManager.bindAppWidgetIdIfAllowed(i, appWidgetProviderInfo.getProfile(), appWidgetProviderInfo.provider, bundle);
    }

    public LauncherAppWidgetProviderInfo findProvider(ComponentName componentName, UserHandle userHandle) {
        for (AppWidgetProviderInfo appWidgetProviderInfo : getAllProviders(new PackageUserKey(componentName.getPackageName(), userHandle))) {
            if (appWidgetProviderInfo.provider.equals(componentName)) {
                return LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, appWidgetProviderInfo);
            }
        }
        return null;
    }

    public HashMap<ComponentKey, AppWidgetProviderInfo> getAllProvidersMap() {
        HashMap<ComponentKey, AppWidgetProviderInfo> hashMap = new HashMap<>();
        for (UserHandle userHandle : this.mUserManager.getUserProfiles()) {
            for (AppWidgetProviderInfo appWidgetProviderInfo : this.mAppWidgetManager.getInstalledProvidersForProfile(userHandle)) {
                hashMap.put(new ComponentKey(appWidgetProviderInfo.provider, userHandle), appWidgetProviderInfo);
            }
        }
        return hashMap;
    }
}
