package com.taobao.android.dexposed.utility;

import android.util.Log;
import p015me.weishu.epic.art.method.ArtMethod;

public class Runtime {
    private static final String TAG = "Runtime";
    private static volatile boolean g64 = false;
    private static volatile boolean isArt = System.getProperty("java.vm.version").startsWith("2");
    private static volatile Boolean isThumb;

    static {
        try {
            g64 = ((Boolean) Class.forName("dalvik.system.VMRuntime").getDeclaredMethod("is64Bit", new Class[0]).invoke(Class.forName("dalvik.system.VMRuntime").getDeclaredMethod("getRuntime", new Class[0]).invoke(null, new Object[0]), new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e(TAG, "get is64Bit failed, default not 64bit!", e);
            g64 = false;
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("is64Bit: ");
        sb.append(g64);
        sb.append(", isArt: ");
        sb.append(isArt);
        Log.i(str, sb.toString());
    }

    public static boolean is64Bit() {
        return g64;
    }

    public static boolean isArt() {
        return isArt;
    }

    public static boolean isThumb2() {
        if (isThumb != null) {
            return isThumb.booleanValue();
        }
        boolean z = false;
        try {
            long entryPointFromQuickCompiledCode = ArtMethod.m105of(String.class.getDeclaredMethod("hashCode", new Class[0])).getEntryPointFromQuickCompiledCode();
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("isThumb2, entry: ");
            sb.append(Long.toHexString(entryPointFromQuickCompiledCode));
            Logger.m96w(str, sb.toString());
            if ((entryPointFromQuickCompiledCode & 1) == 1) {
                z = true;
            }
            isThumb = Boolean.valueOf(z);
            return isThumb.booleanValue();
        } catch (Throwable th) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("isThumb2, error: ");
            sb2.append(th);
            Logger.m96w(str2, sb2.toString());
            return true;
        }
    }
}
