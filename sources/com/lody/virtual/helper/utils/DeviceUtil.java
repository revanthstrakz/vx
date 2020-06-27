package com.lody.virtual.helper.utils;

import android.os.Build;
import android.os.Build.VERSION;

public class DeviceUtil {
    public static boolean isMeizuBelowN() {
        if (VERSION.SDK_INT > 23) {
            return false;
        }
        return Build.DISPLAY.toLowerCase().contains("flyme");
    }

    public static boolean isSamsung() {
        return "samsung".equalsIgnoreCase(Build.BRAND) || "samsung".equalsIgnoreCase(Build.MANUFACTURER);
    }
}
