package com.android.launcher3.compat;

import android.content.Context;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageInstaller.SessionCallback;
import android.content.pm.PackageInstaller.SessionInfo;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.launcher3.IconCache;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.compat.PackageInstallerCompat.PackageInstallInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PackageInstallerCompatVL extends PackageInstallerCompat {
    private static final boolean DEBUG = false;
    final SparseArray<String> mActiveSessions = new SparseArray<>();
    private final Context mAppContext;
    private final IconCache mCache;
    private final SessionCallback mCallback = new SessionCallback() {
        public void onActiveChanged(int i, boolean z) {
        }

        public void onCreated(int i) {
            pushSessionDisplayToLauncher(i);
        }

        public void onFinished(int i, boolean z) {
            String str = (String) PackageInstallerCompatVL.this.mActiveSessions.get(i);
            PackageInstallerCompatVL.this.mActiveSessions.remove(i);
            if (str != null) {
                PackageInstallerCompatVL.this.sendUpdate(PackageInstallInfo.fromState(z ? 0 : 2, str));
            }
        }

        public void onProgressChanged(int i, float f) {
            SessionInfo access$000 = PackageInstallerCompatVL.this.verify(PackageInstallerCompatVL.this.mInstaller.getSessionInfo(i));
            if (access$000 != null && access$000.getAppPackageName() != null) {
                PackageInstallerCompatVL.this.sendUpdate(PackageInstallInfo.fromInstallingState(access$000));
            }
        }

        public void onBadgingChanged(int i) {
            pushSessionDisplayToLauncher(i);
        }

        private SessionInfo pushSessionDisplayToLauncher(int i) {
            SessionInfo access$000 = PackageInstallerCompatVL.this.verify(PackageInstallerCompatVL.this.mInstaller.getSessionInfo(i));
            if (access$000 == null || access$000.getAppPackageName() == null) {
                return null;
            }
            PackageInstallerCompatVL.this.mActiveSessions.put(i, access$000.getAppPackageName());
            PackageInstallerCompatVL.this.addSessionInfoToCache(access$000, Process.myUserHandle());
            LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
            if (instanceNoCreate != null) {
                instanceNoCreate.getModel().updateSessionDisplayInfo(access$000.getAppPackageName());
            }
            return access$000;
        }
    };
    final PackageInstaller mInstaller;
    private final HashMap<String, Boolean> mSessionVerifiedMap = new HashMap<>();
    private final Handler mWorker;

    PackageInstallerCompatVL(Context context) {
        this.mAppContext = context.getApplicationContext();
        this.mInstaller = context.getPackageManager().getPackageInstaller();
        this.mCache = LauncherAppState.getInstance(context).getIconCache();
        this.mWorker = new Handler(LauncherModel.getWorkerLooper());
        this.mInstaller.registerSessionCallback(this.mCallback, this.mWorker);
    }

    public HashMap<String, Integer> updateAndGetActiveSessionCache() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        UserHandle myUserHandle = Process.myUserHandle();
        for (SessionInfo sessionInfo : getAllVerifiedSessions()) {
            addSessionInfoToCache(sessionInfo, myUserHandle);
            if (sessionInfo.getAppPackageName() != null) {
                hashMap.put(sessionInfo.getAppPackageName(), Integer.valueOf((int) (sessionInfo.getProgress() * 100.0f)));
                this.mActiveSessions.put(sessionInfo.getSessionId(), sessionInfo.getAppPackageName());
            }
        }
        return hashMap;
    }

    /* access modifiers changed from: 0000 */
    public void addSessionInfoToCache(SessionInfo sessionInfo, UserHandle userHandle) {
        String appPackageName = sessionInfo.getAppPackageName();
        if (appPackageName != null) {
            this.mCache.cachePackageInstallInfo(appPackageName, userHandle, sessionInfo.getAppIcon(), sessionInfo.getAppLabel());
        }
    }

    public void onStop() {
        this.mInstaller.unregisterSessionCallback(this.mCallback);
    }

    /* access modifiers changed from: 0000 */
    public void sendUpdate(PackageInstallInfo packageInstallInfo) {
        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
        if (instanceNoCreate != null) {
            instanceNoCreate.getModel().setPackageState(packageInstallInfo);
        }
    }

    /* access modifiers changed from: private */
    public SessionInfo verify(SessionInfo sessionInfo) {
        if (sessionInfo == null || sessionInfo.getInstallerPackageName() == null || TextUtils.isEmpty(sessionInfo.getAppPackageName())) {
            return null;
        }
        String installerPackageName = sessionInfo.getInstallerPackageName();
        synchronized (this.mSessionVerifiedMap) {
            if (!this.mSessionVerifiedMap.containsKey(installerPackageName)) {
                boolean z = true;
                if (LauncherAppsCompat.getInstance(this.mAppContext).getApplicationInfo(installerPackageName, 1, Process.myUserHandle()) == null) {
                    z = false;
                }
                this.mSessionVerifiedMap.put(installerPackageName, Boolean.valueOf(z));
            }
        }
        if (!((Boolean) this.mSessionVerifiedMap.get(installerPackageName)).booleanValue()) {
            sessionInfo = null;
        }
        return sessionInfo;
    }

    public List<SessionInfo> getAllVerifiedSessions() {
        ArrayList arrayList = new ArrayList(this.mInstaller.getAllSessions());
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (verify((SessionInfo) it.next()) == null) {
                it.remove();
            }
        }
        return arrayList;
    }
}
