package com.android.launcher3;

public interface LauncherProviderChangeListener {
    void onAppWidgetHostReset();

    void onExtractedColorsChanged();

    void onLauncherProviderChanged();
}
