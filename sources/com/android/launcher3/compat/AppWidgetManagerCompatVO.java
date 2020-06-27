package com.android.launcher3.compat;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.support.annotation.Nullable;
import com.android.launcher3.util.PackageUserKey;
import java.util.List;

class AppWidgetManagerCompatVO extends AppWidgetManagerCompatVL {
    AppWidgetManagerCompatVO(Context context) {
        super(context);
    }

    public List<AppWidgetProviderInfo> getAllProviders(@Nullable PackageUserKey packageUserKey) {
        if (packageUserKey == null) {
            return super.getAllProviders(null);
        }
        return this.mAppWidgetManager.getInstalledProvidersForPackage(packageUserKey.mPackageName, packageUserKey.mUser);
    }
}
