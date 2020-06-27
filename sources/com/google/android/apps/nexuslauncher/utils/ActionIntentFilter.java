package com.google.android.apps.nexuslauncher.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import com.lody.virtual.client.ipc.ServiceManagerNative;

public class ActionIntentFilter {
    public static IntentFilter googleInstance(String... strArr) {
        return newInstance("com.google.android.googlequicksearchbox", strArr);
    }

    public static IntentFilter newInstance(String str, String... strArr) {
        IntentFilter intentFilter = new IntentFilter();
        for (String addAction : strArr) {
            intentFilter.addAction(addAction);
        }
        intentFilter.addDataScheme(ServiceManagerNative.PACKAGE);
        intentFilter.addDataSchemeSpecificPart(str, 0);
        return intentFilter;
    }

    public static boolean googleEnabled(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo("com.google.android.googlequicksearchbox", 0).enabled;
        } catch (NameNotFoundException unused) {
            return false;
        }
    }
}
