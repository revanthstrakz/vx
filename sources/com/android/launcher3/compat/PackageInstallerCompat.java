package com.android.launcher3.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInstaller.SessionInfo;
import android.support.annotation.NonNull;
import java.util.HashMap;
import java.util.List;

public abstract class PackageInstallerCompat {
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_INSTALLED = 0;
    public static final int STATUS_INSTALLING = 1;
    private static PackageInstallerCompat sInstance;
    private static final Object sInstanceLock = new Object();

    public static final class PackageInstallInfo {
        public final ComponentName componentName;
        public final String packageName;
        public final int progress;
        public final int state;

        private PackageInstallInfo(@NonNull SessionInfo sessionInfo) {
            this.state = 1;
            this.packageName = sessionInfo.getAppPackageName();
            this.componentName = new ComponentName(this.packageName, "");
            this.progress = (int) (sessionInfo.getProgress() * 100.0f);
        }

        public PackageInstallInfo(String str, int i, int i2) {
            this.state = i;
            this.packageName = str;
            this.componentName = new ComponentName(str, "");
            this.progress = i2;
        }

        public static PackageInstallInfo fromInstallingState(SessionInfo sessionInfo) {
            return new PackageInstallInfo(sessionInfo);
        }

        public static PackageInstallInfo fromState(int i, String str) {
            return new PackageInstallInfo(str, i, 0);
        }
    }

    public abstract List<SessionInfo> getAllVerifiedSessions();

    public abstract void onStop();

    public abstract HashMap<String, Integer> updateAndGetActiveSessionCache();

    public static PackageInstallerCompat getInstance(Context context) {
        PackageInstallerCompat packageInstallerCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new PackageInstallerCompatVL(context);
            }
            packageInstallerCompat = sInstance;
        }
        return packageInstallerCompat;
    }
}
