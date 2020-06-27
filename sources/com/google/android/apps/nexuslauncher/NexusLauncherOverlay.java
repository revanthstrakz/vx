package com.google.android.apps.nexuslauncher;

import com.android.launcher3.Launcher;
import com.android.launcher3.Launcher.LauncherOverlay;
import com.android.launcher3.Launcher.LauncherOverlayCallbacks;
import com.android.launcher3.Utilities;
import com.google.android.libraries.gsa.launcherclient.ISerializableScrollCallback;
import com.google.android.libraries.gsa.launcherclient.LauncherClient;

public class NexusLauncherOverlay implements LauncherOverlay, ISerializableScrollCallback {
    static final String PREF_PERSIST_FLAGS = "pref_persistent_flags";
    boolean mAttached = false;
    private LauncherClient mClient;
    private int mFlags;
    boolean mFlagsChanged = false;
    final Launcher mLauncher;
    private LauncherOverlayCallbacks mOverlayCallbacks;

    public NexusLauncherOverlay(Launcher launcher) {
        this.mLauncher = launcher;
        this.mFlags = Utilities.getDevicePrefs(launcher).getInt(PREF_PERSIST_FLAGS, 0);
    }

    public void setClient(LauncherClient launcherClient) {
        this.mClient = launcherClient;
    }

    public void setPersistentFlags(int i) {
        int i2 = i & 24;
        if (i2 != this.mFlags) {
            this.mFlagsChanged = true;
            this.mFlags = i2;
            Utilities.getDevicePrefs(this.mLauncher).edit().putInt(PREF_PERSIST_FLAGS, i2).apply();
        }
    }

    public void onServiceStateChanged(boolean z) {
        if (z != this.mAttached) {
            this.mAttached = z;
            this.mLauncher.setLauncherOverlay(z ? this : null);
        }
    }

    public void onOverlayScrollChanged(float f) {
        if (this.mOverlayCallbacks != null) {
            this.mOverlayCallbacks.onScrollChanged(f);
        }
    }

    public void onScrollChange(float f, boolean z) {
        this.mClient.setScroll(f);
    }

    public void onScrollInteractionBegin() {
        this.mClient.startScroll();
    }

    public void onScrollInteractionEnd() {
        this.mClient.endScroll();
    }

    public void setOverlayCallbacks(LauncherOverlayCallbacks launcherOverlayCallbacks) {
        this.mOverlayCallbacks = launcherOverlayCallbacks;
    }
}
