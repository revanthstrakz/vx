package com.lody.virtual.client;

import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Process;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.natives.NativeMethods;
import com.lody.virtual.helper.compat.BuildCompat;
import com.lody.virtual.helper.utils.DeviceUtil;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.InstalledAppInfo;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeEngine {
    private static final String TAG = "NativeEngine";
    private static final String VESCAPE = "/6decacfa7aad11e8a718985aebe4663a";
    private static Map<String, InstalledAppInfo> sDexOverrideMap = null;
    private static boolean sFlag = false;

    public static native void disableJit(int i);

    private static native void nativeEnableIORedirect(String str, int i, int i2);

    private static native String nativeGetRedirectedPath(String str);

    private static native void nativeIOForbid(String str);

    private static native void nativeIORedirect(String str, String str2);

    private static native void nativeIOWhitelist(String str);

    private static native void nativeLaunchEngine(Object[] objArr, String str, boolean z, int i, int i2);

    private static native void nativeMark();

    private static native String nativeReverseRedirectedPath(String str);

    static {
        try {
            System.loadLibrary("va++");
        } catch (Throwable th) {
            VLog.m87e(TAG, VLog.getStackTraceString(th), new Object[0]);
        }
        NativeMethods.init();
    }

    public static void startDexOverride() {
        List<InstalledAppInfo> installedApps = VirtualCore.get().getInstalledApps(0);
        sDexOverrideMap = new HashMap(installedApps.size());
        for (InstalledAppInfo installedAppInfo : installedApps) {
            try {
                sDexOverrideMap.put(new File(installedAppInfo.apkPath).getCanonicalPath(), installedAppInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getRedirectedPath(String str) {
        try {
            return nativeGetRedirectedPath(str);
        } catch (Throwable th) {
            VLog.m87e(TAG, VLog.getStackTraceString(th), new Object[0]);
            return str;
        }
    }

    public static String resverseRedirectedPath(String str) {
        try {
            return nativeReverseRedirectedPath(str);
        } catch (Throwable th) {
            VLog.m87e(TAG, VLog.getStackTraceString(th), new Object[0]);
            return str;
        }
    }

    public static void redirectDirectory(String str, String str2) {
        if (!str.endsWith("/")) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("/");
            str = sb.toString();
        }
        if (!str2.endsWith("/")) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2);
            sb2.append("/");
            str2 = sb2.toString();
        }
        try {
            nativeIORedirect(str, str2);
        } catch (Throwable th) {
            VLog.m87e(TAG, VLog.getStackTraceString(th), new Object[0]);
        }
    }

    public static String getEscapePath(String str) {
        if (str == null) {
            return null;
        }
        File file = new File(str);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return new File(VESCAPE, str).getAbsolutePath();
    }

    public static void redirectFile(String str, String str2) {
        if (str.endsWith("/")) {
            str = str.substring(0, str.length() - 1);
        }
        if (str2.endsWith("/")) {
            str2 = str2.substring(0, str2.length() - 1);
        }
        try {
            nativeIORedirect(str, str2);
        } catch (Throwable th) {
            VLog.m87e(TAG, VLog.getStackTraceString(th), new Object[0]);
        }
    }

    public static void whitelist(String str, boolean z) {
        if (z && !str.endsWith("/")) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("/");
            str = sb.toString();
        } else if (!z && str.endsWith("/")) {
            str = str.substring(0, str.length() - 1);
        }
        try {
            nativeIOWhitelist(str);
        } catch (Throwable th) {
            VLog.m87e(TAG, VLog.getStackTraceString(th), new Object[0]);
        }
    }

    public static void forbid(String str) {
        if (!str.endsWith("/")) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("/");
            str = sb.toString();
        }
        try {
            nativeIOForbid(str);
        } catch (Throwable th) {
            VLog.m87e(TAG, VLog.getStackTraceString(th), new Object[0]);
        }
    }

    public static void enableIORedirect() {
        try {
            String format = String.format("/data/data/%s/lib/libva++.so", new Object[]{VirtualCore.get().getHostPkg()});
            if (new File(format).exists()) {
                redirectDirectory(VESCAPE, "/");
                nativeEnableIORedirect(format, VERSION.SDK_INT, BuildCompat.getPreviewSDKInt());
                return;
            }
            throw new RuntimeException("io redirect failed.");
        } catch (Throwable th) {
            VLog.m87e(TAG, VLog.getStackTraceString(th), new Object[0]);
        }
    }

    static void launchEngine() {
        if (!sFlag) {
            try {
                nativeLaunchEngine(new Method[]{NativeMethods.gOpenDexFileNative, NativeMethods.gCameraNativeSetup, NativeMethods.gAudioRecordNativeCheckPermission}, VirtualCore.get().getHostPkg(), VirtualRuntime.isArt(), VERSION.SDK_INT, NativeMethods.gCameraMethodType);
            } catch (Throwable th) {
                VLog.m87e(TAG, VLog.getStackTraceString(th), new Object[0]);
            }
            sFlag = true;
        }
    }

    public static void onKillProcess(int i, int i2) {
        VLog.m87e(TAG, "killProcess: pid = %d, signal = %d.", Integer.valueOf(i), Integer.valueOf(i2));
        if (i == Process.myPid()) {
            VLog.m87e(TAG, VLog.getStackTraceString(new Throwable()), new Object[0]);
        }
    }

    public static int onGetCallingUid(int i) {
        int callingPid = Binder.getCallingPid();
        if (callingPid == Process.myPid()) {
            return VClientImpl.get().getBaseVUid();
        }
        if (callingPid == VirtualCore.get().getSystemPid()) {
            return 1000;
        }
        int uidByPid = VActivityManager.get().getUidByPid(callingPid);
        if (uidByPid != -1) {
            return VUserHandle.getAppId(uidByPid);
        }
        VLog.m91w(TAG, String.format("Unknown uid: %s", new Object[]{Integer.valueOf(callingPid)}), new Object[0]);
        return VClientImpl.get().getBaseVUid();
    }

    public static void onOpenDexFileNative(String[] strArr) {
        String str = strArr[0];
        VLog.m86d(TAG, "DexOrJarPath = %s, OutputPath = %s.", str, strArr[1]);
        try {
            InstalledAppInfo installedAppInfo = (InstalledAppInfo) sDexOverrideMap.get(new File(str).getCanonicalPath());
            if ((installedAppInfo != null && !installedAppInfo.dependSystem) || (installedAppInfo != null && DeviceUtil.isMeizuBelowN() && strArr[1] == null)) {
                strArr[1] = installedAppInfo.getOdexFile().getPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int onGetUid(int i) {
        return VClientImpl.get().getBaseVUid();
    }
}
