package com.getkeepsafe.relinker;

import android.os.Build;
import android.os.Build.VERSION;
import com.getkeepsafe.relinker.ReLinker.LibraryLoader;

final class SystemLibraryLoader implements LibraryLoader {
    SystemLibraryLoader() {
    }

    public void loadLibrary(String str) {
        System.loadLibrary(str);
    }

    public void loadPath(String str) {
        System.load(str);
    }

    public String mapLibraryName(String str) {
        if (!str.startsWith("lib") || !str.endsWith(".so")) {
            return System.mapLibraryName(str);
        }
        return str;
    }

    public String unmapLibraryName(String str) {
        return str.substring(3, str.length() - 3);
    }

    public String[] supportedAbis() {
        if (VERSION.SDK_INT >= 21 && Build.SUPPORTED_ABIS.length > 0) {
            return Build.SUPPORTED_ABIS;
        }
        if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
            return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        return new String[]{Build.CPU_ABI};
    }
}
