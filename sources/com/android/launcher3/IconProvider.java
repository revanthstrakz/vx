package com.android.launcher3;

import android.content.pm.LauncherActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import java.util.Locale;

public class IconProvider {
    private static final boolean DBG = false;
    private static final String TAG = "IconProvider";
    protected String mSystemState;

    public IconProvider() {
        updateSystemStateString();
    }

    public void updateSystemStateString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Locale.getDefault().toString());
        sb.append(",");
        sb.append(VERSION.SDK_INT);
        this.mSystemState = sb.toString();
    }

    public String getIconSystemState(String str) {
        return this.mSystemState;
    }

    public Drawable getIcon(LauncherActivityInfo launcherActivityInfo, int i, boolean z) {
        return launcherActivityInfo.getIcon(i);
    }
}
