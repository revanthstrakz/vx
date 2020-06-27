package com.taobao.android.dexposed.utility;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

public class NougatPolicy {
    private static final String TAG = "NougatPolicy";

    private static class TraceLogger {
        private TraceLogger() {
        }

        /* renamed from: i */
        static void m99i(String str, String str2) {
            Log.i(str, str2);
        }

        /* renamed from: e */
        static void m97e(String str, String str2) {
            Log.i(str, str2);
        }

        /* renamed from: e */
        static void m98e(String str, String str2, Throwable th) {
            Log.i(str, str2, th);
        }
    }

    public static boolean fullCompile(Context context) {
        try {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            Object packageManagerBinderProxy = getPackageManagerBinderProxy();
            if (packageManagerBinderProxy == null) {
                TraceLogger.m97e(TAG, "can not found package service");
                return false;
            }
            boolean booleanValue = ((Boolean) packageManagerBinderProxy.getClass().getDeclaredMethod("performDexOptMode", new Class[]{String.class, Boolean.TYPE, String.class, Boolean.TYPE}).invoke(packageManagerBinderProxy, new Object[]{context.getPackageName(), Boolean.valueOf(false), "speed", Boolean.valueOf(true)})).booleanValue();
            long elapsedRealtime2 = SystemClock.elapsedRealtime() - elapsedRealtime;
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("full Compile cost: ");
            sb.append(elapsedRealtime2);
            sb.append(" result:");
            sb.append(booleanValue);
            Log.i(str, sb.toString());
            return booleanValue;
        } catch (Throwable th) {
            TraceLogger.m98e(TAG, "fullCompile failed:", th);
            return false;
        }
    }

    public static boolean clearCompileData(Context context) {
        try {
            Object packageManagerBinderProxy = getPackageManagerBinderProxy();
            return ((Boolean) packageManagerBinderProxy.getClass().getDeclaredMethod("performDexOpt", new Class[]{String.class, Boolean.TYPE, Integer.TYPE, Boolean.TYPE}).invoke(packageManagerBinderProxy, new Object[]{context.getPackageName(), Boolean.valueOf(false), Integer.valueOf(2), Boolean.valueOf(true)})).booleanValue();
        } catch (Throwable th) {
            TraceLogger.m98e(TAG, "clear compile data failed", th);
            return false;
        }
    }

    private static Object getPackageManagerBinderProxy() throws Exception {
        return Class.forName("android.app.ActivityThread").getDeclaredMethod("getPackageManager", new Class[0]).invoke(null, new Object[0]);
    }
}
