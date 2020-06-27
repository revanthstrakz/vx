package com.lody.virtual.helper.utils;

import android.os.Bundle;
import android.util.Log;
import java.util.Set;

public class VLog {
    public static boolean OPEN_LOG = true;

    /* renamed from: i */
    public static void m89i(String str, String str2, Object... objArr) {
        if (OPEN_LOG) {
            Log.i(str, String.format(str2, objArr));
        }
    }

    /* renamed from: d */
    public static void m86d(String str, String str2, Object... objArr) {
        if (OPEN_LOG) {
            Log.d(str, String.format(str2, objArr));
        }
    }

    /* renamed from: w */
    public static void m91w(String str, String str2, Object... objArr) {
        if (OPEN_LOG) {
            Log.w(str, String.format(str2, objArr));
        }
    }

    /* renamed from: e */
    public static void m87e(String str, String str2, Object... objArr) {
        if (OPEN_LOG) {
            Log.e(str, String.format(str2, objArr));
        }
    }

    /* renamed from: v */
    public static void m90v(String str, String str2, Object... objArr) {
        if (OPEN_LOG) {
            Log.v(str, String.format(str2, objArr));
        }
    }

    public static String toString(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        if (Reflect.m80on((Object) bundle).get("mParcelledData") == null) {
            return bundle.toString();
        }
        Set<String> keySet = bundle.keySet();
        StringBuilder sb = new StringBuilder("Bundle[");
        if (keySet != null) {
            for (String str : keySet) {
                sb.append(str);
                sb.append("=");
                sb.append(bundle.get(str));
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static String getStackTraceString(Throwable th) {
        return Log.getStackTraceString(th);
    }

    public static void printStackTrace(String str) {
        Log.e(str, getStackTraceString(new Exception()));
    }

    /* renamed from: e */
    public static void m88e(String str, Throwable th) {
        Log.e(str, getStackTraceString(th));
    }
}
