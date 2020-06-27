package p011de.robv.android.xposed;

import android.os.SELinux;
import p011de.robv.android.xposed.services.BaseService;
import p011de.robv.android.xposed.services.BinderService;
import p011de.robv.android.xposed.services.DirectAccessService;
import p011de.robv.android.xposed.services.ZygoteService;

/* renamed from: de.robv.android.xposed.SELinuxHelper */
public final class SELinuxHelper {
    private static boolean sIsSELinuxEnabled = false;
    private static BaseService sServiceAppDataFile;

    private SELinuxHelper() {
    }

    public static boolean isSELinuxEnabled() {
        return sIsSELinuxEnabled;
    }

    public static boolean isSELinuxEnforced() {
        return sIsSELinuxEnabled && SELinux.isSELinuxEnforced();
    }

    public static String getContext() {
        if (sIsSELinuxEnabled) {
            return SELinux.getContext();
        }
        return null;
    }

    public static BaseService getAppDataFileService() {
        if (sServiceAppDataFile != null) {
            return sServiceAppDataFile;
        }
        throw new UnsupportedOperationException();
    }

    static void initOnce() {
        try {
            sIsSELinuxEnabled = SELinux.isSELinuxEnabled();
        } catch (NoClassDefFoundError unused) {
        }
    }

    static void initForProcess(String str) {
        if (!sIsSELinuxEnabled) {
            sServiceAppDataFile = new DirectAccessService();
        } else if (str == null) {
            sServiceAppDataFile = new ZygoteService();
        } else if (str.equals("android")) {
            sServiceAppDataFile = BinderService.getService(0);
        } else {
            sServiceAppDataFile = new DirectAccessService();
        }
    }
}
