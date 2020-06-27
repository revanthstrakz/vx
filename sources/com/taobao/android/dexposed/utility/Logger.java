package com.taobao.android.dexposed.utility;

import android.util.Log;

public class Logger {
    private static final boolean DEBUG = false;
    public static final String preFix = "epic.";

    /* renamed from: d */
    public static void m92d(String str, String str2) {
    }

    /* renamed from: e */
    public static void m93e(String str, String str2) {
    }

    /* renamed from: e */
    public static void m94e(String str, String str2, Throwable th) {
    }

    /* renamed from: i */
    public static void m95i(String str, String str2) {
    }

    /* renamed from: w */
    public static void m96w(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(preFix);
        sb.append(str);
        Log.w(sb.toString(), str2);
    }
}
