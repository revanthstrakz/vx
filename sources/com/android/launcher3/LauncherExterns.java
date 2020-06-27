package com.android.launcher3;

import android.content.SharedPreferences;
import com.android.launcher3.Launcher.LauncherOverlay;

public interface LauncherExterns {
    void clearTypedText();

    SharedPreferences getSharedPrefs();

    boolean setLauncherCallbacks(LauncherCallbacks launcherCallbacks);

    void setLauncherOverlay(LauncherOverlay launcherOverlay);
}
