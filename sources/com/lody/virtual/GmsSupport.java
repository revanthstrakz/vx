package com.lody.virtual;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import com.lody.virtual.client.core.VirtualCore;
import java.util.Arrays;
import java.util.List;

public class GmsSupport {
    public static final List<String> GOOGLE_APP = Arrays.asList(new String[]{"com.android.vending", "com.google.android.play.games", "com.google.android.wearable.app", "com.google.android.wearable.app.cn"});
    public static final List<String> GOOGLE_SERVICE = Arrays.asList(new String[]{"com.google.android.gsf", "com.google.android.gms", "com.google.android.gsf.login", "com.google.android.backuptransport", "com.google.android.backup", "com.google.android.configupdater", "com.google.android.syncadapters.contacts", "com.google.android.feedback", "com.google.android.onetimeinitializer", "com.google.android.partnersetup", "com.google.android.setupwizard", "com.google.android.syncadapters.calendar"});

    public static boolean isGmsFamilyPackage(String str) {
        return str.equals("com.android.vending") || str.equals("com.google.android.gms");
    }

    public static boolean isGoogleFrameworkInstalled() {
        return VirtualCore.get().isAppInstalled("com.google.android.gms");
    }

    public static boolean isOutsideGoogleFrameworkExist() {
        return VirtualCore.get().isOutsideInstalled("com.google.android.gms");
    }

    private static void installPackages(List<String> list, int i) {
        VirtualCore virtualCore = VirtualCore.get();
        for (String str : list) {
            if (!virtualCore.isAppInstalledAsUser(i, str)) {
                ApplicationInfo applicationInfo = null;
                try {
                    applicationInfo = VirtualCore.get().getUnHookPackageManager().getApplicationInfo(str, 0);
                } catch (NameNotFoundException unused) {
                }
                if (!(applicationInfo == null || applicationInfo.sourceDir == null)) {
                    if (i == 0) {
                        virtualCore.installPackage(applicationInfo.sourceDir, 32);
                    } else {
                        virtualCore.installPackageAsUser(i, str);
                    }
                }
            }
        }
    }

    public static void installGApps(int i) {
        installPackages(GOOGLE_SERVICE, i);
        installPackages(GOOGLE_APP, i);
    }

    public static void installGoogleService(int i) {
        installPackages(GOOGLE_SERVICE, i);
    }

    public static void installGoogleApp(int i) {
        installPackages(GOOGLE_APP, i);
    }
}
