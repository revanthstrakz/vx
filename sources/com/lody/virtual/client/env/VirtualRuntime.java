package com.lody.virtual.client.env;

import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import mirror.android.ddm.DdmHandleAppName;
import mirror.android.ddm.DdmHandleAppNameJBMR1;
import mirror.android.p017os.Process;

public class VirtualRuntime {
    private static String sInitialPackageName;
    private static String sProcessName;
    private static final Handler sUIHandler = new Handler(Looper.getMainLooper());

    public static Handler getUIHandler() {
        return sUIHandler;
    }

    public static String getProcessName() {
        return sProcessName;
    }

    public static String getInitialPackageName() {
        return sInitialPackageName;
    }

    public static void setupRuntime(String str, ApplicationInfo applicationInfo) {
        if (sProcessName == null) {
            sInitialPackageName = applicationInfo.packageName;
            sProcessName = str;
            Process.setArgV0.call(str);
            if (VERSION.SDK_INT >= 17) {
                DdmHandleAppNameJBMR1.setAppName.call(str, Integer.valueOf(0));
            } else {
                DdmHandleAppName.setAppName.call(str);
            }
        }
    }

    public static <T> T crash(RemoteException remoteException) throws RuntimeException {
        remoteException.printStackTrace();
        if (VirtualCore.get().isVAppProcess()) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        throw new DeadServerException((Throwable) remoteException);
    }

    public static boolean isArt() {
        return System.getProperty("java.vm.version").startsWith("2");
    }
}
