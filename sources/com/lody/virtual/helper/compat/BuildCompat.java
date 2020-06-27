package com.lody.virtual.helper.compat;

import android.os.Build.VERSION;

public class BuildCompat {
    public static int getPreviewSDKInt() {
        if (VERSION.SDK_INT >= 23) {
            try {
                return VERSION.PREVIEW_SDK_INT;
            } catch (Throwable unused) {
            }
        }
        return 0;
    }

    public static boolean isOreo() {
        return (VERSION.SDK_INT == 25 && getPreviewSDKInt() > 0) || VERSION.SDK_INT > 25;
    }

    public static boolean isQ() {
        return (VERSION.SDK_INT == 28 && getPreviewSDKInt() > 0) || VERSION.SDK_INT >= 29;
    }
}
