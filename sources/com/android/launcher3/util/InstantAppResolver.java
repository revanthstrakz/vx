package com.android.launcher3.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.android.launcher3.C0622R;
import com.android.launcher3.Utilities;
import java.util.Collections;
import java.util.List;

public class InstantAppResolver {
    public boolean isInstantApp(ApplicationInfo applicationInfo) {
        return false;
    }

    public static InstantAppResolver newInstance(Context context) {
        return (InstantAppResolver) Utilities.getOverrideObject(InstantAppResolver.class, context, C0622R.string.instant_app_resolver_class);
    }

    public List<ApplicationInfo> getInstantApps() {
        return Collections.emptyList();
    }
}
