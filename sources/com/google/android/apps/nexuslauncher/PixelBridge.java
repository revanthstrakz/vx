package com.google.android.apps.nexuslauncher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import com.android.launcher3.C0622R;
import com.google.android.libraries.gsa.launcherclient.LauncherClient;

public class PixelBridge {
    public static boolean isInstalled(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(LauncherClient.BRIDGE_PACKAGE, 64);
            if (packageInfo.versionName.equals(context.getString(C0622R.string.bridge_version)) && isSigned(context, packageInfo.signatures)) {
                return true;
            }
        } catch (NameNotFoundException unused) {
        }
        return false;
    }

    private static boolean isSigned(Context context, Signature[] signatureArr) {
        int integer = context.getResources().getInteger(C0622R.integer.bridge_signature_hash);
        boolean z = false;
        for (Signature hashCode : signatureArr) {
            if (hashCode.hashCode() != integer) {
                return false;
            }
        }
        if (signatureArr.length > 0) {
            z = true;
        }
        return z;
    }
}
