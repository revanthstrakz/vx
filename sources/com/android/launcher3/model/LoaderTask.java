package com.android.launcher3.model;

import android.appwidget.AppWidgetProviderInfo;
import android.content.pm.LauncherActivityInfo;
import android.os.UserHandle;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.AppInfo;
import com.android.launcher3.IconCache;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.util.LooperIdleLock;
import com.android.launcher3.util.ManagedProfileHeuristic;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;

public class LoaderTask implements Runnable {
    private static final boolean DEBUG_LOADERS = false;
    private static final String TAG = "LoaderTask";
    private final LauncherAppState mApp;
    private final AppWidgetManagerCompat mAppWidgetManager = AppWidgetManagerCompat.getInstance(this.mApp.getContext());
    private final AllAppsList mBgAllAppsList;
    private final BgDataModel mBgDataModel;
    private final IconCache mIconCache = this.mApp.getIconCache();
    private final LauncherAppsCompat mLauncherApps = LauncherAppsCompat.getInstance(this.mApp.getContext());
    private final PackageInstallerCompat mPackageInstaller = PackageInstallerCompat.getInstance(this.mApp.getContext());
    private final LoaderResults mResults;
    private final DeepShortcutManager mShortcutManager = DeepShortcutManager.getInstance(this.mApp.getContext());
    private boolean mStopped;
    private final UserManagerCompat mUserManager = UserManagerCompat.getInstance(this.mApp.getContext());

    public LoaderTask(LauncherAppState launcherAppState, AllAppsList allAppsList, BgDataModel bgDataModel, LoaderResults loaderResults) {
        this.mApp = launcherAppState;
        this.mBgAllAppsList = allAppsList;
        this.mBgDataModel = bgDataModel;
        this.mResults = loaderResults;
    }

    /* access modifiers changed from: protected */
    public synchronized void waitForIdle() {
        LooperIdleLock newIdleLock = this.mResults.newIdleLock(this);
        while (!this.mStopped) {
            if (!newIdleLock.awaitLocked(1000)) {
                break;
            }
        }
    }

    private synchronized void verifyNotStopped() throws CancellationException {
        if (this.mStopped) {
            throw new CancellationException("Loader stopped");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        loadWorkspace();
        verifyNotStopped();
        r4.mResults.bindWorkspace();
        waitForIdle();
        verifyNotStopped();
        loadAllApps();
        verifyNotStopped();
        r4.mResults.bindAllApps();
        verifyNotStopped();
        updateIconCache();
        waitForIdle();
        verifyNotStopped();
        loadDeepShortcuts();
        verifyNotStopped();
        r4.mResults.bindDeepShortcuts();
        waitForIdle();
        verifyNotStopped();
        r4.mBgDataModel.widgetsModel.update(r4.mApp, null);
        verifyNotStopped();
        r4.mResults.bindWidgets();
        r0.commit();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0060, code lost:
        if (r0 == null) goto L_0x007b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0066, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x006a, code lost:
        if (r0 != null) goto L_0x006c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x006c, code lost:
        if (r1 != null) goto L_0x006e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0072, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r1.addSuppressed(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0077, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        r0 = r4.mApp.getModel().beginLoader(r4);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.mStopped     // Catch:{ all -> 0x007c }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r4)     // Catch:{ all -> 0x007c }
            return
        L_0x0007:
            monitor-exit(r4)     // Catch:{ all -> 0x007c }
            com.android.launcher3.LauncherAppState r0 = r4.mApp     // Catch:{ CancellationException -> 0x007b }
            com.android.launcher3.LauncherModel r0 = r0.getModel()     // Catch:{ CancellationException -> 0x007b }
            com.android.launcher3.LauncherModel$LoaderTransaction r0 = r0.beginLoader(r4)     // Catch:{ CancellationException -> 0x007b }
            r1 = 0
            r4.loadWorkspace()     // Catch:{ Throwable -> 0x0068 }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x0068 }
            com.android.launcher3.model.LoaderResults r2 = r4.mResults     // Catch:{ Throwable -> 0x0068 }
            r2.bindWorkspace()     // Catch:{ Throwable -> 0x0068 }
            r4.waitForIdle()     // Catch:{ Throwable -> 0x0068 }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x0068 }
            r4.loadAllApps()     // Catch:{ Throwable -> 0x0068 }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x0068 }
            com.android.launcher3.model.LoaderResults r2 = r4.mResults     // Catch:{ Throwable -> 0x0068 }
            r2.bindAllApps()     // Catch:{ Throwable -> 0x0068 }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x0068 }
            r4.updateIconCache()     // Catch:{ Throwable -> 0x0068 }
            r4.waitForIdle()     // Catch:{ Throwable -> 0x0068 }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x0068 }
            r4.loadDeepShortcuts()     // Catch:{ Throwable -> 0x0068 }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x0068 }
            com.android.launcher3.model.LoaderResults r2 = r4.mResults     // Catch:{ Throwable -> 0x0068 }
            r2.bindDeepShortcuts()     // Catch:{ Throwable -> 0x0068 }
            r4.waitForIdle()     // Catch:{ Throwable -> 0x0068 }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x0068 }
            com.android.launcher3.model.BgDataModel r2 = r4.mBgDataModel     // Catch:{ Throwable -> 0x0068 }
            com.android.launcher3.model.WidgetsModel r2 = r2.widgetsModel     // Catch:{ Throwable -> 0x0068 }
            com.android.launcher3.LauncherAppState r3 = r4.mApp     // Catch:{ Throwable -> 0x0068 }
            r2.update(r3, r1)     // Catch:{ Throwable -> 0x0068 }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x0068 }
            com.android.launcher3.model.LoaderResults r2 = r4.mResults     // Catch:{ Throwable -> 0x0068 }
            r2.bindWidgets()     // Catch:{ Throwable -> 0x0068 }
            r0.commit()     // Catch:{ Throwable -> 0x0068 }
            if (r0 == 0) goto L_0x007b
            r0.close()     // Catch:{ CancellationException -> 0x007b }
            goto L_0x007b
        L_0x0066:
            r2 = move-exception
            goto L_0x006a
        L_0x0068:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0066 }
        L_0x006a:
            if (r0 == 0) goto L_0x007a
            if (r1 == 0) goto L_0x0077
            r0.close()     // Catch:{ Throwable -> 0x0072 }
            goto L_0x007a
        L_0x0072:
            r0 = move-exception
            r1.addSuppressed(r0)     // Catch:{ CancellationException -> 0x007b }
            goto L_0x007a
        L_0x0077:
            r0.close()     // Catch:{ CancellationException -> 0x007b }
        L_0x007a:
            throw r2     // Catch:{ CancellationException -> 0x007b }
        L_0x007b:
            return
        L_0x007c:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x007c }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.LoaderTask.run():void");
    }

    public synchronized void stopLocked() {
        this.mStopped = true;
        notify();
    }

    /* JADX WARNING: type inference failed for: r2v1, types: [android.content.Context] */
    /* JADX WARNING: type inference failed for: r8v3, types: [int] */
    /* JADX WARNING: type inference failed for: r22v0 */
    /* JADX WARNING: type inference failed for: r22v1 */
    /* JADX WARNING: type inference failed for: r8v4 */
    /* JADX WARNING: type inference failed for: r6v8, types: [android.content.Context] */
    /* JADX WARNING: type inference failed for: r22v2 */
    /* JADX WARNING: type inference failed for: r8v10 */
    /* JADX WARNING: type inference failed for: r41v0 */
    /* JADX WARNING: type inference failed for: r6v10 */
    /* JADX WARNING: type inference failed for: r41v1 */
    /* JADX WARNING: type inference failed for: r6v11 */
    /* JADX WARNING: type inference failed for: r41v2 */
    /* JADX WARNING: type inference failed for: r6v12 */
    /* JADX WARNING: type inference failed for: r41v3 */
    /* JADX WARNING: type inference failed for: r41v4 */
    /* JADX WARNING: type inference failed for: r6v13 */
    /* JADX WARNING: type inference failed for: r22v3 */
    /* JADX WARNING: type inference failed for: r8v12 */
    /* JADX WARNING: type inference failed for: r41v5 */
    /* JADX WARNING: type inference failed for: r8v13 */
    /* JADX WARNING: type inference failed for: r41v6 */
    /* JADX WARNING: type inference failed for: r6v16 */
    /* JADX WARNING: type inference failed for: r41v7 */
    /* JADX WARNING: type inference failed for: r6v17 */
    /* JADX WARNING: type inference failed for: r6v18 */
    /* JADX WARNING: type inference failed for: r41v8 */
    /* JADX WARNING: type inference failed for: r6v19 */
    /* JADX WARNING: type inference failed for: r6v20 */
    /* JADX WARNING: type inference failed for: r41v9 */
    /* JADX WARNING: type inference failed for: r6v21 */
    /* JADX WARNING: type inference failed for: r41v10 */
    /* JADX WARNING: type inference failed for: r6v22 */
    /* JADX WARNING: type inference failed for: r41v11 */
    /* JADX WARNING: type inference failed for: r6v23 */
    /* JADX WARNING: type inference failed for: r41v12 */
    /* JADX WARNING: type inference failed for: r6v24 */
    /* JADX WARNING: type inference failed for: r41v13 */
    /* JADX WARNING: type inference failed for: r6v25 */
    /* JADX WARNING: type inference failed for: r41v14 */
    /* JADX WARNING: type inference failed for: r6v26 */
    /* JADX WARNING: type inference failed for: r41v15 */
    /* JADX WARNING: type inference failed for: r6v27 */
    /* JADX WARNING: type inference failed for: r41v16 */
    /* JADX WARNING: type inference failed for: r6v28 */
    /* JADX WARNING: type inference failed for: r41v17 */
    /* JADX WARNING: type inference failed for: r6v29, types: [android.content.Context] */
    /* JADX WARNING: type inference failed for: r41v18 */
    /* JADX WARNING: type inference failed for: r6v30 */
    /* JADX WARNING: type inference failed for: r41v19 */
    /* JADX WARNING: type inference failed for: r6v31 */
    /* JADX WARNING: type inference failed for: r41v20 */
    /* JADX WARNING: type inference failed for: r6v32 */
    /* JADX WARNING: type inference failed for: r41v21 */
    /* JADX WARNING: type inference failed for: r6v33 */
    /* JADX WARNING: type inference failed for: r41v22 */
    /* JADX WARNING: type inference failed for: r6v34 */
    /* JADX WARNING: type inference failed for: r41v23 */
    /* JADX WARNING: type inference failed for: r41v24 */
    /* JADX WARNING: type inference failed for: r6v35 */
    /* JADX WARNING: type inference failed for: r6v36 */
    /* JADX WARNING: type inference failed for: r6v37 */
    /* JADX WARNING: type inference failed for: r6v38, types: [android.content.Context] */
    /* JADX WARNING: type inference failed for: r6v39 */
    /* JADX WARNING: type inference failed for: r41v25 */
    /* JADX WARNING: type inference failed for: r6v40 */
    /* JADX WARNING: type inference failed for: r41v26 */
    /* JADX WARNING: type inference failed for: r6v41 */
    /* JADX WARNING: type inference failed for: r41v27 */
    /* JADX WARNING: type inference failed for: r6v42 */
    /* JADX WARNING: type inference failed for: r41v28 */
    /* JADX WARNING: type inference failed for: r6v43 */
    /* JADX WARNING: type inference failed for: r41v29 */
    /* JADX WARNING: type inference failed for: r41v30 */
    /* JADX WARNING: type inference failed for: r6v44 */
    /* JADX WARNING: type inference failed for: r41v31 */
    /* JADX WARNING: type inference failed for: r6v45 */
    /* JADX WARNING: type inference failed for: r41v32 */
    /* JADX WARNING: type inference failed for: r6v46 */
    /* JADX WARNING: type inference failed for: r41v33 */
    /* JADX WARNING: type inference failed for: r41v34 */
    /* JADX WARNING: type inference failed for: r37v0 */
    /* JADX WARNING: type inference failed for: r6v47 */
    /* JADX WARNING: type inference failed for: r41v35 */
    /* JADX WARNING: type inference failed for: r37v1 */
    /* JADX WARNING: type inference failed for: r6v48 */
    /* JADX WARNING: type inference failed for: r6v52, types: [int] */
    /* JADX WARNING: type inference failed for: r41v36 */
    /* JADX WARNING: type inference failed for: r6v53 */
    /* JADX WARNING: type inference failed for: r41v37 */
    /* JADX WARNING: type inference failed for: r6v54 */
    /* JADX WARNING: type inference failed for: r41v38 */
    /* JADX WARNING: type inference failed for: r6v55 */
    /* JADX WARNING: type inference failed for: r41v39 */
    /* JADX WARNING: type inference failed for: r6v58 */
    /* JADX WARNING: type inference failed for: r41v40 */
    /* JADX WARNING: type inference failed for: r6v59 */
    /* JADX WARNING: type inference failed for: r41v41 */
    /* JADX WARNING: type inference failed for: r37v2 */
    /* JADX WARNING: type inference failed for: r6v60 */
    /* JADX WARNING: type inference failed for: r41v42 */
    /* JADX WARNING: type inference failed for: r37v3 */
    /* JADX WARNING: type inference failed for: r6v61 */
    /* JADX WARNING: type inference failed for: r37v4 */
    /* JADX WARNING: type inference failed for: r8v37 */
    /* JADX WARNING: type inference failed for: r37v5 */
    /* JADX WARNING: type inference failed for: r6v62 */
    /* JADX WARNING: type inference failed for: r37v6 */
    /* JADX WARNING: type inference failed for: r6v63 */
    /* JADX WARNING: type inference failed for: r6v64 */
    /* JADX WARNING: type inference failed for: r37v7 */
    /* JADX WARNING: type inference failed for: r41v43 */
    /* JADX WARNING: type inference failed for: r37v8 */
    /* JADX WARNING: type inference failed for: r41v44 */
    /* JADX WARNING: type inference failed for: r6v75 */
    /* JADX WARNING: type inference failed for: r41v45 */
    /* JADX WARNING: type inference failed for: r6v78 */
    /* JADX WARNING: type inference failed for: r41v46 */
    /* JADX WARNING: type inference failed for: r6v79 */
    /* JADX WARNING: type inference failed for: r41v47 */
    /* JADX WARNING: type inference failed for: r41v48 */
    /* JADX WARNING: type inference failed for: r8v49 */
    /* JADX WARNING: type inference failed for: r22v4 */
    /* JADX WARNING: type inference failed for: r22v5 */
    /* JADX WARNING: type inference failed for: r22v6 */
    /* JADX WARNING: type inference failed for: r8v50 */
    /* JADX WARNING: type inference failed for: r41v49 */
    /* JADX WARNING: type inference failed for: r6v82 */
    /* JADX WARNING: type inference failed for: r41v50 */
    /* JADX WARNING: type inference failed for: r6v83 */
    /* JADX WARNING: type inference failed for: r41v51 */
    /* JADX WARNING: type inference failed for: r41v52 */
    /* JADX WARNING: type inference failed for: r6v84 */
    /* JADX WARNING: type inference failed for: r41v53 */
    /* JADX WARNING: type inference failed for: r6v85 */
    /* JADX WARNING: type inference failed for: r6v86 */
    /* JADX WARNING: type inference failed for: r6v87 */
    /* JADX WARNING: type inference failed for: r6v88 */
    /* JADX WARNING: type inference failed for: r6v89 */
    /* JADX WARNING: type inference failed for: r6v90 */
    /* JADX WARNING: type inference failed for: r6v91 */
    /* JADX WARNING: type inference failed for: r6v92 */
    /* JADX WARNING: type inference failed for: r41v54 */
    /* JADX WARNING: type inference failed for: r6v93 */
    /* JADX WARNING: type inference failed for: r41v55 */
    /* JADX WARNING: type inference failed for: r6v94 */
    /* JADX WARNING: type inference failed for: r41v56 */
    /* JADX WARNING: type inference failed for: r6v95 */
    /* JADX WARNING: type inference failed for: r41v57 */
    /* JADX WARNING: type inference failed for: r6v96 */
    /* JADX WARNING: type inference failed for: r41v58 */
    /* JADX WARNING: type inference failed for: r6v97 */
    /* JADX WARNING: type inference failed for: r41v59 */
    /* JADX WARNING: type inference failed for: r41v60 */
    /* JADX WARNING: type inference failed for: r41v61 */
    /* JADX WARNING: type inference failed for: r6v98 */
    /* JADX WARNING: type inference failed for: r6v99 */
    /* JADX WARNING: type inference failed for: r6v100 */
    /* JADX WARNING: type inference failed for: r41v62 */
    /* JADX WARNING: type inference failed for: r6v101 */
    /* JADX WARNING: type inference failed for: r41v63 */
    /* JADX WARNING: type inference failed for: r41v64 */
    /* JADX WARNING: type inference failed for: r41v65 */
    /* JADX WARNING: type inference failed for: r6v102 */
    /* JADX WARNING: type inference failed for: r6v103 */
    /* JADX WARNING: type inference failed for: r6v104 */
    /* JADX WARNING: type inference failed for: r41v66 */
    /* JADX WARNING: type inference failed for: r6v105 */
    /* JADX WARNING: type inference failed for: r41v67 */
    /* JADX WARNING: type inference failed for: r6v106 */
    /* JADX WARNING: type inference failed for: r41v68 */
    /* JADX WARNING: type inference failed for: r41v69 */
    /* JADX WARNING: type inference failed for: r41v70 */
    /* JADX WARNING: type inference failed for: r41v71 */
    /* JADX WARNING: type inference failed for: r41v72 */
    /* JADX WARNING: type inference failed for: r41v73 */
    /* JADX WARNING: type inference failed for: r41v74 */
    /* JADX WARNING: type inference failed for: r41v75 */
    /* JADX WARNING: type inference failed for: r41v76 */
    /* JADX WARNING: type inference failed for: r41v77 */
    /* JADX WARNING: type inference failed for: r41v78 */
    /* JADX WARNING: type inference failed for: r41v79 */
    /* JADX WARNING: type inference failed for: r41v80 */
    /* JADX WARNING: type inference failed for: r6v107 */
    /* JADX WARNING: type inference failed for: r6v108 */
    /* JADX WARNING: type inference failed for: r6v109 */
    /* JADX WARNING: type inference failed for: r6v110 */
    /* JADX WARNING: type inference failed for: r6v111 */
    /* JADX WARNING: type inference failed for: r41v81 */
    /* JADX WARNING: type inference failed for: r6v112 */
    /* JADX WARNING: type inference failed for: r41v82 */
    /* JADX WARNING: type inference failed for: r6v113 */
    /* JADX WARNING: type inference failed for: r41v83 */
    /* JADX WARNING: type inference failed for: r6v114 */
    /* JADX WARNING: type inference failed for: r37v9 */
    /* JADX WARNING: type inference failed for: r6v115 */
    /* JADX WARNING: type inference failed for: r6v116 */
    /* JADX WARNING: type inference failed for: r41v84 */
    /* JADX WARNING: type inference failed for: r6v117 */
    /* JADX WARNING: type inference failed for: r41v85 */
    /* JADX WARNING: type inference failed for: r37v10 */
    /* JADX WARNING: type inference failed for: r6v118 */
    /* JADX WARNING: type inference failed for: r37v11 */
    /* JADX WARNING: type inference failed for: r37v12 */
    /* JADX WARNING: type inference failed for: r37v13 */
    /* JADX WARNING: type inference failed for: r37v14 */
    /* JADX WARNING: type inference failed for: r37v15 */
    /* JADX WARNING: type inference failed for: r37v16 */
    /* JADX WARNING: type inference failed for: r37v17 */
    /* JADX WARNING: type inference failed for: r41v86 */
    /* JADX WARNING: type inference failed for: r6v119 */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x086f, code lost:
        r12 = r33;
        r41 = r41;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x01b5, code lost:
        r41 = r41;
        r6 = r6;
     */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r22v2
      assigns: []
      uses: []
      mth insns count: 1182
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x05c1  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x060d A[SYNTHETIC, Splitter:B:276:0x060d] */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x0673 A[SYNTHETIC, Splitter:B:298:0x0673] */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x067f  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x06ad  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x06b8 A[SYNTHETIC, Splitter:B:322:0x06b8] */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x06cb A[SYNTHETIC, Splitter:B:327:0x06cb] */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x07e5 A[SYNTHETIC, Splitter:B:406:0x07e5] */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0832 A[SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 68 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadWorkspace() {
        /*
            r45 = this;
            r1 = r45
            com.android.launcher3.LauncherAppState r2 = r1.mApp
            android.content.Context r2 = r2.getContext()
            android.content.ContentResolver r9 = r2.getContentResolver()
            com.android.launcher3.util.PackageManagerHelper r10 = new com.android.launcher3.util.PackageManagerHelper
            r10.<init>(r2)
            boolean r11 = r10.isSafeMode()
            boolean r12 = com.android.launcher3.Utilities.isBootCompleted()
            com.android.launcher3.util.MultiHashMap r13 = new com.android.launcher3.util.MultiHashMap
            r13.<init>()
            com.android.launcher3.provider.ImportDataTask.performImportIfPossible(r2)     // Catch:{ Exception -> 0x0023 }
            r3 = 0
            goto L_0x0024
        L_0x0023:
            r3 = 1
        L_0x0024:
            if (r3 != 0) goto L_0x0031
            boolean r4 = com.android.launcher3.model.GridSizeMigrationTask.ENABLED
            if (r4 == 0) goto L_0x0031
            boolean r4 = com.android.launcher3.model.GridSizeMigrationTask.migrateGridIfNeeded(r2)
            if (r4 != 0) goto L_0x0031
            r3 = 1
        L_0x0031:
            if (r3 == 0) goto L_0x003f
            java.lang.String r3 = "LoaderTask"
            java.lang.String r4 = "loadWorkspace: resetting launcher database"
            android.util.Log.d(r3, r4)
            java.lang.String r3 = "create_empty_db"
            com.android.launcher3.LauncherSettings.Settings.call(r9, r3)
        L_0x003f:
            java.lang.String r3 = "LoaderTask"
            java.lang.String r4 = "loadWorkspace: loading default favorites"
            android.util.Log.d(r3, r4)
            java.lang.String r3 = "load_default_favorites"
            com.android.launcher3.LauncherSettings.Settings.call(r9, r3)
            com.android.launcher3.model.BgDataModel r8 = r1.mBgDataModel
            monitor-enter(r8)
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ all -> 0x0a22 }
            r3.clear()     // Catch:{ all -> 0x0a22 }
            com.android.launcher3.compat.PackageInstallerCompat r3 = r1.mPackageInstaller     // Catch:{ all -> 0x0a22 }
            java.util.HashMap r7 = r3.updateAndGetActiveSessionCache()     // Catch:{ all -> 0x0a22 }
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ all -> 0x0a22 }
            java.util.ArrayList<java.lang.Long> r3 = r3.workspaceScreens     // Catch:{ all -> 0x0a22 }
            java.util.ArrayList r4 = com.android.launcher3.LauncherModel.loadWorkspaceScreensDb(r2)     // Catch:{ all -> 0x0a22 }
            r3.addAll(r4)     // Catch:{ all -> 0x0a22 }
            java.util.HashMap r6 = new java.util.HashMap     // Catch:{ all -> 0x0a22 }
            r6.<init>()     // Catch:{ all -> 0x0a22 }
            com.android.launcher3.model.LoaderCursor r5 = new com.android.launcher3.model.LoaderCursor     // Catch:{ all -> 0x0a22 }
            android.net.Uri r4 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI     // Catch:{ all -> 0x0a22 }
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r3 = r9
            r14 = r5
            r5 = r16
            r15 = r6
            r6 = r17
            r20 = r7
            r7 = r18
            r16 = r8
            r8 = r19
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.LauncherAppState r4 = r1.mApp     // Catch:{ all -> 0x0a28 }
            r14.<init>(r3, r4)     // Catch:{ all -> 0x0a28 }
            java.lang.String r3 = "appWidgetId"
            int r3 = r14.getColumnIndexOrThrow(r3)     // Catch:{ all -> 0x0a1c }
            java.lang.String r4 = "appWidgetProvider"
            int r4 = r14.getColumnIndexOrThrow(r4)     // Catch:{ all -> 0x0a1c }
            java.lang.String r5 = "spanX"
            int r5 = r14.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0a1c }
            java.lang.String r6 = "spanY"
            int r6 = r14.getColumnIndexOrThrow(r6)     // Catch:{ all -> 0x0a1c }
            java.lang.String r7 = "rank"
            int r7 = r14.getColumnIndexOrThrow(r7)     // Catch:{ all -> 0x0a1c }
            java.lang.String r8 = "options"
            int r8 = r14.getColumnIndexOrThrow(r8)     // Catch:{ all -> 0x0a1c }
            r21 = r9
            android.util.LongSparseArray<android.os.UserHandle> r9 = r14.allUsers     // Catch:{ all -> 0x0a1c }
            r22 = r2
            android.util.LongSparseArray r2 = new android.util.LongSparseArray     // Catch:{ all -> 0x0a1c }
            r2.<init>()     // Catch:{ all -> 0x0a1c }
            r23 = r7
            android.util.LongSparseArray r7 = new android.util.LongSparseArray     // Catch:{ all -> 0x0a1c }
            r7.<init>()     // Catch:{ all -> 0x0a1c }
            r24 = r13
            com.android.launcher3.compat.UserManagerCompat r13 = r1.mUserManager     // Catch:{ all -> 0x0a1c }
            java.util.List r13 = r13.getUserProfiles()     // Catch:{ all -> 0x0a1c }
            java.util.Iterator r13 = r13.iterator()     // Catch:{ all -> 0x0a1c }
        L_0x00cf:
            boolean r17 = r13.hasNext()     // Catch:{ all -> 0x0a1c }
            r25 = r12
            if (r17 == 0) goto L_0x0147
            java.lang.Object r17 = r13.next()     // Catch:{ all -> 0x0a1c }
            r12 = r17
            android.os.UserHandle r12 = (android.os.UserHandle) r12     // Catch:{ all -> 0x0a1c }
            r26 = r13
            com.android.launcher3.compat.UserManagerCompat r13 = r1.mUserManager     // Catch:{ all -> 0x0a1c }
            r27 = r5
            r28 = r6
            long r5 = r13.getSerialNumberForUser(r12)     // Catch:{ all -> 0x0a1c }
            r9.put(r5, r12)     // Catch:{ all -> 0x0a1c }
            com.android.launcher3.compat.UserManagerCompat r13 = r1.mUserManager     // Catch:{ all -> 0x0a1c }
            boolean r13 = r13.isQuietModeEnabled(r12)     // Catch:{ all -> 0x0a1c }
            java.lang.Boolean r13 = java.lang.Boolean.valueOf(r13)     // Catch:{ all -> 0x0a1c }
            r2.put(r5, r13)     // Catch:{ all -> 0x0a1c }
            com.android.launcher3.compat.UserManagerCompat r13 = r1.mUserManager     // Catch:{ all -> 0x0a1c }
            boolean r13 = r13.isUserUnlocked(r12)     // Catch:{ all -> 0x0a1c }
            if (r13 == 0) goto L_0x0131
            r29 = r9
            com.android.launcher3.shortcuts.DeepShortcutManager r9 = r1.mShortcutManager     // Catch:{ all -> 0x0a1c }
            r30 = r13
            r13 = 0
            java.util.List r9 = r9.queryForPinnedShortcuts(r13, r12)     // Catch:{ all -> 0x0a1c }
            com.android.launcher3.shortcuts.DeepShortcutManager r12 = r1.mShortcutManager     // Catch:{ all -> 0x0a1c }
            boolean r12 = r12.wasLastCallSuccess()     // Catch:{ all -> 0x0a1c }
            if (r12 == 0) goto L_0x012e
            java.util.Iterator r9 = r9.iterator()     // Catch:{ all -> 0x0a1c }
        L_0x011a:
            boolean r12 = r9.hasNext()     // Catch:{ all -> 0x0a1c }
            if (r12 == 0) goto L_0x0135
            java.lang.Object r12 = r9.next()     // Catch:{ all -> 0x0a1c }
            com.android.launcher3.shortcuts.ShortcutInfoCompat r12 = (com.android.launcher3.shortcuts.ShortcutInfoCompat) r12     // Catch:{ all -> 0x0a1c }
            com.android.launcher3.shortcuts.ShortcutKey r13 = com.android.launcher3.shortcuts.ShortcutKey.fromInfo(r12)     // Catch:{ all -> 0x0a1c }
            r15.put(r13, r12)     // Catch:{ all -> 0x0a1c }
            goto L_0x011a
        L_0x012e:
            r30 = 0
            goto L_0x0135
        L_0x0131:
            r29 = r9
            r30 = r13
        L_0x0135:
            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r30)     // Catch:{ all -> 0x0a1c }
            r7.put(r5, r9)     // Catch:{ all -> 0x0a1c }
            r12 = r25
            r13 = r26
            r5 = r27
            r6 = r28
            r9 = r29
            goto L_0x00cf
        L_0x0147:
            r27 = r5
            r28 = r6
            com.android.launcher3.folder.FolderIconPreviewVerifier r5 = new com.android.launcher3.folder.FolderIconPreviewVerifier     // Catch:{ all -> 0x0a1c }
            com.android.launcher3.LauncherAppState r6 = r1.mApp     // Catch:{ all -> 0x0a1c }
            com.android.launcher3.InvariantDeviceProfile r6 = r6.getInvariantDeviceProfile()     // Catch:{ all -> 0x0a1c }
            r5.<init>(r6)     // Catch:{ all -> 0x0a1c }
            r12 = 0
        L_0x0157:
            boolean r6 = r1.mStopped     // Catch:{ all -> 0x0a1c }
            if (r6 != 0) goto L_0x08b2
            boolean r6 = r14.moveToNext()     // Catch:{ all -> 0x0a1c }
            if (r6 == 0) goto L_0x08b2
            android.os.UserHandle r6 = r14.user     // Catch:{ Exception -> 0x088c }
            if (r6 != 0) goto L_0x0195
            java.lang.String r6 = "User has been deleted"
            r14.markDeleted(r6)     // Catch:{ Exception -> 0x0186 }
            r38 = r2
            r31 = r3
            r32 = r4
            r13 = r7
            r41 = r8
            r33 = r12
            r7 = r20
            r9 = r23
            r4 = r24
            r39 = r27
            r40 = r28
            r44 = r15
            r15 = r5
            r5 = r44
            goto L_0x0700
        L_0x0186:
            r0 = move-exception
            r38 = r2
            r31 = r3
            r32 = r4
            r43 = r5
            r42 = r7
            r41 = r8
            goto L_0x089b
        L_0x0195:
            int r6 = r14.itemType     // Catch:{ Exception -> 0x088c }
            switch(r6) {
                case 0: goto L_0x047e;
                case 1: goto L_0x047e;
                case 2: goto L_0x0413;
                case 3: goto L_0x019a;
                case 4: goto L_0x01b8;
                case 5: goto L_0x01b8;
                case 6: goto L_0x047e;
                default: goto L_0x019a;
            }
        L_0x019a:
            r38 = r2
            r31 = r3
            r32 = r4
            r43 = r5
            r42 = r7
            r41 = r8
            r33 = r12
            r5 = r15
            r7 = r20
            r6 = r22
            r9 = r23
            r4 = r24
            r39 = r27
            r40 = r28
        L_0x01b5:
            r8 = 1
            goto L_0x086f
        L_0x01b8:
            int r6 = r14.itemType     // Catch:{ Exception -> 0x03f3 }
            r9 = 5
            if (r6 != r9) goto L_0x01bf
            r6 = 1
            goto L_0x01c0
        L_0x01bf:
            r6 = 0
        L_0x01c0:
            int r9 = r14.getInt(r3)     // Catch:{ Exception -> 0x03f3 }
            java.lang.String r13 = r14.getString(r4)     // Catch:{ Exception -> 0x03f3 }
            r31 = r3
            android.content.ComponentName r3 = android.content.ComponentName.unflattenFromString(r13)     // Catch:{ Exception -> 0x03f1 }
            r32 = r4
            r4 = 1
            boolean r17 = r14.hasRestoreFlag(r4)     // Catch:{ Exception -> 0x03ef }
            r17 = r17 ^ 1
            r4 = 2
            boolean r4 = r14.hasRestoreFlag(r4)     // Catch:{ Exception -> 0x03ef }
            r18 = 1
            r4 = r4 ^ 1
            if (r12 != 0) goto L_0x0205
            r33 = r12
            com.android.launcher3.compat.AppWidgetManagerCompat r12 = r1.mAppWidgetManager     // Catch:{ Exception -> 0x01eb }
            java.util.HashMap r12 = r12.getAllProvidersMap()     // Catch:{ Exception -> 0x01eb }
            goto L_0x0207
        L_0x01eb:
            r0 = move-exception
            r38 = r2
            r43 = r5
            r42 = r7
            r41 = r8
            r5 = r15
            r7 = r20
            r6 = r22
            r9 = r23
            r4 = r24
            r39 = r27
            r40 = r28
            r12 = r33
            goto L_0x08a8
        L_0x0205:
            r33 = r12
        L_0x0207:
            r34 = r15
            com.android.launcher3.util.ComponentKey r15 = new com.android.launcher3.util.ComponentKey     // Catch:{ Exception -> 0x03d7 }
            r35 = r7
            android.content.ComponentName r7 = android.content.ComponentName.unflattenFromString(r13)     // Catch:{ Exception -> 0x03d5 }
            r36 = r5
            android.os.UserHandle r5 = r14.user     // Catch:{ Exception -> 0x03c2 }
            r15.<init>(r7, r5)     // Catch:{ Exception -> 0x03c2 }
            java.lang.Object r5 = r12.get(r15)     // Catch:{ Exception -> 0x03c2 }
            android.appwidget.AppWidgetProviderInfo r5 = (android.appwidget.AppWidgetProviderInfo) r5     // Catch:{ Exception -> 0x03c2 }
            boolean r7 = isValidProvider(r5)     // Catch:{ Exception -> 0x03c2 }
            if (r11 != 0) goto L_0x0248
            if (r6 != 0) goto L_0x0248
            if (r4 == 0) goto L_0x0248
            if (r7 != 0) goto L_0x0248
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0267 }
            r3.<init>()     // Catch:{ Exception -> 0x0267 }
            java.lang.String r4 = "Deleting widget that isn't installed anymore: "
            r3.append(r4)     // Catch:{ Exception -> 0x0267 }
            r3.append(r5)     // Catch:{ Exception -> 0x0267 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0267 }
            r14.markDeleted(r3)     // Catch:{ Exception -> 0x0267 }
            r37 = r8
            r7 = r20
            r4 = r27
            r5 = r28
            goto L_0x037d
        L_0x0248:
            if (r7 == 0) goto L_0x0270
            com.android.launcher3.LauncherAppWidgetInfo r3 = new com.android.launcher3.LauncherAppWidgetInfo     // Catch:{ Exception -> 0x0267 }
            android.content.ComponentName r5 = r5.provider     // Catch:{ Exception -> 0x0267 }
            r3.<init>(r9, r5)     // Catch:{ Exception -> 0x0267 }
            int r5 = r14.restoreFlag     // Catch:{ Exception -> 0x0267 }
            r5 = r5 & -9
            r5 = r5 & -3
            if (r4 != 0) goto L_0x025d
            if (r17 == 0) goto L_0x025d
            r5 = r5 | 4
        L_0x025d:
            r3.restoreStatus = r5     // Catch:{ Exception -> 0x0267 }
            r37 = r8
            r7 = r20
        L_0x0263:
            r4 = 32
            goto L_0x02ea
        L_0x0267:
            r0 = move-exception
            r38 = r2
            r41 = r8
            r7 = r20
            goto L_0x03c9
        L_0x0270:
            java.lang.String r4 = "LoaderTask"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03c2 }
            r5.<init>()     // Catch:{ Exception -> 0x03c2 }
            java.lang.String r7 = "Widget restore pending id="
            r5.append(r7)     // Catch:{ Exception -> 0x03c2 }
            r37 = r8
            long r7 = r14.f66id     // Catch:{ Exception -> 0x03b1 }
            r5.append(r7)     // Catch:{ Exception -> 0x03b1 }
            java.lang.String r7 = " appWidgetId="
            r5.append(r7)     // Catch:{ Exception -> 0x03b1 }
            r5.append(r9)     // Catch:{ Exception -> 0x03b1 }
            java.lang.String r7 = " status ="
            r5.append(r7)     // Catch:{ Exception -> 0x03b1 }
            int r7 = r14.restoreFlag     // Catch:{ Exception -> 0x03b1 }
            r5.append(r7)     // Catch:{ Exception -> 0x03b1 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x03b1 }
            android.util.Log.v(r4, r5)     // Catch:{ Exception -> 0x03b1 }
            com.android.launcher3.LauncherAppWidgetInfo r4 = new com.android.launcher3.LauncherAppWidgetInfo     // Catch:{ Exception -> 0x03b1 }
            r4.<init>(r9, r3)     // Catch:{ Exception -> 0x03b1 }
            int r5 = r14.restoreFlag     // Catch:{ Exception -> 0x03b1 }
            r4.restoreStatus = r5     // Catch:{ Exception -> 0x03b1 }
            java.lang.String r5 = r3.getPackageName()     // Catch:{ Exception -> 0x03b1 }
            r7 = r20
            java.lang.Object r5 = r7.get(r5)     // Catch:{ Exception -> 0x03af }
            java.lang.Integer r5 = (java.lang.Integer) r5     // Catch:{ Exception -> 0x03af }
            r8 = 8
            boolean r9 = r14.hasRestoreFlag(r8)     // Catch:{ Exception -> 0x03af }
            if (r9 == 0) goto L_0x02ba
            goto L_0x02dd
        L_0x02ba:
            if (r5 == 0) goto L_0x02c2
            int r3 = r4.restoreStatus     // Catch:{ Exception -> 0x03af }
            r3 = r3 | r8
            r4.restoreStatus = r3     // Catch:{ Exception -> 0x03af }
            goto L_0x02dd
        L_0x02c2:
            if (r11 != 0) goto L_0x02dd
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03af }
            r4.<init>()     // Catch:{ Exception -> 0x03af }
            java.lang.String r5 = "Unrestored widget removed: "
            r4.append(r5)     // Catch:{ Exception -> 0x03af }
            r4.append(r3)     // Catch:{ Exception -> 0x03af }
            java.lang.String r3 = r4.toString()     // Catch:{ Exception -> 0x03af }
            r14.markDeleted(r3)     // Catch:{ Exception -> 0x03af }
            r4 = r27
            r5 = r28
            goto L_0x0318
        L_0x02dd:
            if (r5 != 0) goto L_0x02e1
            r3 = 0
            goto L_0x02e5
        L_0x02e1:
            int r3 = r5.intValue()     // Catch:{ Exception -> 0x03af }
        L_0x02e5:
            r4.installProgress = r3     // Catch:{ Exception -> 0x03af }
            r3 = r4
            goto L_0x0263
        L_0x02ea:
            boolean r4 = r3.hasRestoreFlag(r4)     // Catch:{ Exception -> 0x03af }
            if (r4 == 0) goto L_0x02f6
            android.content.Intent r4 = r14.parseIntent()     // Catch:{ Exception -> 0x03af }
            r3.bindOptions = r4     // Catch:{ Exception -> 0x03af }
        L_0x02f6:
            r14.applyCommonProperties(r3)     // Catch:{ Exception -> 0x03af }
            r4 = r27
            int r5 = r14.getInt(r4)     // Catch:{ Exception -> 0x03a3 }
            r3.spanX = r5     // Catch:{ Exception -> 0x03a3 }
            r5 = r28
            int r8 = r14.getInt(r5)     // Catch:{ Exception -> 0x0394 }
            r3.spanY = r8     // Catch:{ Exception -> 0x0394 }
            android.os.UserHandle r8 = r14.user     // Catch:{ Exception -> 0x0394 }
            r3.user = r8     // Catch:{ Exception -> 0x0394 }
            boolean r8 = r14.isOnWorkspaceOrHotseat()     // Catch:{ Exception -> 0x0394 }
            if (r8 != 0) goto L_0x032c
            java.lang.String r3 = "Widget found where container != CONTAINER_DESKTOP nor CONTAINER_HOTSEAT - ignoring!"
            r14.markDeleted(r3)     // Catch:{ Exception -> 0x0394 }
        L_0x0318:
            r27 = r4
            r28 = r5
            r20 = r7
            r3 = r31
            r4 = r32
            r15 = r34
            r7 = r35
            r5 = r36
            r8 = r37
            goto L_0x0157
        L_0x032c:
            if (r6 != 0) goto L_0x0359
            android.content.ComponentName r6 = r3.providerName     // Catch:{ Exception -> 0x0394 }
            java.lang.String r6 = r6.flattenToString()     // Catch:{ Exception -> 0x0394 }
            boolean r8 = r6.equals(r13)     // Catch:{ Exception -> 0x0394 }
            if (r8 == 0) goto L_0x0340
            int r8 = r3.restoreStatus     // Catch:{ Exception -> 0x0394 }
            int r9 = r14.restoreFlag     // Catch:{ Exception -> 0x0394 }
            if (r8 == r9) goto L_0x0359
        L_0x0340:
            com.android.launcher3.util.ContentWriter r8 = r14.updater()     // Catch:{ Exception -> 0x0394 }
            java.lang.String r9 = "appWidgetProvider"
            com.android.launcher3.util.ContentWriter r6 = r8.put(r9, r6)     // Catch:{ Exception -> 0x0394 }
            java.lang.String r8 = "restored"
            int r9 = r3.restoreStatus     // Catch:{ Exception -> 0x0394 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0394 }
            com.android.launcher3.util.ContentWriter r6 = r6.put(r8, r9)     // Catch:{ Exception -> 0x0394 }
            r6.commit()     // Catch:{ Exception -> 0x0394 }
        L_0x0359:
            int r6 = r3.restoreStatus     // Catch:{ Exception -> 0x0394 }
            if (r6 == 0) goto L_0x0378
            android.content.ComponentName r6 = r3.providerName     // Catch:{ Exception -> 0x0394 }
            java.lang.String r6 = r6.getPackageName()     // Catch:{ Exception -> 0x0394 }
            com.android.launcher3.model.PackageItemInfo r8 = new com.android.launcher3.model.PackageItemInfo     // Catch:{ Exception -> 0x0394 }
            r8.<init>(r6)     // Catch:{ Exception -> 0x0394 }
            r3.pendingItemInfo = r8     // Catch:{ Exception -> 0x0394 }
            com.android.launcher3.model.PackageItemInfo r6 = r3.pendingItemInfo     // Catch:{ Exception -> 0x0394 }
            android.os.UserHandle r8 = r3.user     // Catch:{ Exception -> 0x0394 }
            r6.user = r8     // Catch:{ Exception -> 0x0394 }
            com.android.launcher3.IconCache r6 = r1.mIconCache     // Catch:{ Exception -> 0x0394 }
            com.android.launcher3.model.PackageItemInfo r8 = r3.pendingItemInfo     // Catch:{ Exception -> 0x0394 }
            r9 = 0
            r6.getTitleAndIconForApp(r8, r9)     // Catch:{ Exception -> 0x0394 }
        L_0x0378:
            com.android.launcher3.model.BgDataModel r6 = r1.mBgDataModel     // Catch:{ Exception -> 0x0394 }
            r14.checkAndAddItem(r3, r6)     // Catch:{ Exception -> 0x0394 }
        L_0x037d:
            r38 = r2
            r39 = r4
            r40 = r5
            r6 = r22
            r9 = r23
            r4 = r24
            r5 = r34
            r42 = r35
            r43 = r36
            r41 = r37
            r8 = 1
            goto L_0x0871
        L_0x0394:
            r0 = move-exception
            r38 = r2
            r39 = r4
            r40 = r5
            r6 = r22
            r9 = r23
            r4 = r24
            goto L_0x0474
        L_0x03a3:
            r0 = move-exception
            r38 = r2
            r39 = r4
            r6 = r22
            r9 = r23
            r4 = r24
            goto L_0x03be
        L_0x03af:
            r0 = move-exception
            goto L_0x03b4
        L_0x03b1:
            r0 = move-exception
            r7 = r20
        L_0x03b4:
            r38 = r2
            r6 = r22
            r9 = r23
            r4 = r24
            r39 = r27
        L_0x03be:
            r40 = r28
            goto L_0x0474
        L_0x03c2:
            r0 = move-exception
            r7 = r20
            r38 = r2
            r41 = r8
        L_0x03c9:
            r6 = r22
            r9 = r23
            r4 = r24
            r39 = r27
            r40 = r28
            goto L_0x04c1
        L_0x03d5:
            r0 = move-exception
            goto L_0x03da
        L_0x03d7:
            r0 = move-exception
            r35 = r7
        L_0x03da:
            r7 = r20
            r38 = r2
            r43 = r5
            r41 = r8
            r6 = r22
            r9 = r23
            r4 = r24
            r39 = r27
            r40 = r28
            r5 = r34
            goto L_0x040f
        L_0x03ef:
            r0 = move-exception
            goto L_0x03f8
        L_0x03f1:
            r0 = move-exception
            goto L_0x03f6
        L_0x03f3:
            r0 = move-exception
            r31 = r3
        L_0x03f6:
            r32 = r4
        L_0x03f8:
            r35 = r7
            r33 = r12
            r7 = r20
            r38 = r2
            r43 = r5
            r41 = r8
            r5 = r15
            r6 = r22
            r9 = r23
            r4 = r24
            r39 = r27
            r40 = r28
        L_0x040f:
            r42 = r35
            goto L_0x08a8
        L_0x0413:
            r31 = r3
            r32 = r4
            r36 = r5
            r35 = r7
            r37 = r8
            r33 = r12
            r34 = r15
            r7 = r20
            r4 = r27
            r5 = r28
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ Exception -> 0x0465 }
            long r8 = r14.f66id     // Catch:{ Exception -> 0x0465 }
            com.android.launcher3.FolderInfo r3 = r3.findOrMakeFolder(r8)     // Catch:{ Exception -> 0x0465 }
            r14.applyCommonProperties(r3)     // Catch:{ Exception -> 0x0465 }
            int r6 = r14.titleIndex     // Catch:{ Exception -> 0x0465 }
            java.lang.String r6 = r14.getString(r6)     // Catch:{ Exception -> 0x0465 }
            r3.title = r6     // Catch:{ Exception -> 0x0465 }
            r6 = 1
            r3.spanX = r6     // Catch:{ Exception -> 0x0465 }
            r3.spanY = r6     // Catch:{ Exception -> 0x0465 }
            r6 = r37
            int r8 = r14.getInt(r6)     // Catch:{ Exception -> 0x04b0 }
            r3.options = r8     // Catch:{ Exception -> 0x04b0 }
            r14.markRestored()     // Catch:{ Exception -> 0x04b0 }
            com.android.launcher3.model.BgDataModel r8 = r1.mBgDataModel     // Catch:{ Exception -> 0x04b0 }
            r14.checkAndAddItem(r3, r8)     // Catch:{ Exception -> 0x04b0 }
            r38 = r2
            r39 = r4
            r40 = r5
            r41 = r6
            r6 = r22
            r9 = r23
            r4 = r24
            r5 = r34
            r42 = r35
            r43 = r36
            goto L_0x01b5
        L_0x0465:
            r0 = move-exception
            r38 = r2
            r39 = r4
            r40 = r5
            r6 = r22
            r9 = r23
            r4 = r24
            r12 = r33
        L_0x0474:
            r5 = r34
            r42 = r35
            r43 = r36
            r41 = r37
            goto L_0x08a8
        L_0x047e:
            r31 = r3
            r32 = r4
            r36 = r5
            r35 = r7
            r6 = r8
            r33 = r12
            r34 = r15
            r7 = r20
            r4 = r27
            r5 = r28
            android.content.Intent r3 = r14.parseIntent()     // Catch:{ Exception -> 0x0855 }
            if (r3 != 0) goto L_0x04c9
            java.lang.String r3 = "Invalid or null intent"
            r14.markDeleted(r3)     // Catch:{ Exception -> 0x04b0 }
        L_0x049c:
            r38 = r2
            r39 = r4
        L_0x04a0:
            r40 = r5
            r41 = r6
            r9 = r23
            r4 = r24
        L_0x04a8:
            r5 = r34
            r13 = r35
            r15 = r36
            goto L_0x0700
        L_0x04b0:
            r0 = move-exception
            r38 = r2
            r39 = r4
            r40 = r5
            r41 = r6
            r6 = r22
            r9 = r23
            r4 = r24
            r12 = r33
        L_0x04c1:
            r5 = r34
            r42 = r35
            r43 = r36
            goto L_0x08a8
        L_0x04c9:
            long r8 = r14.serialNumber     // Catch:{ Exception -> 0x0855 }
            java.lang.Object r8 = r2.get(r8)     // Catch:{ Exception -> 0x0855 }
            java.lang.Boolean r8 = (java.lang.Boolean) r8     // Catch:{ Exception -> 0x0855 }
            boolean r8 = r8.booleanValue()     // Catch:{ Exception -> 0x0855 }
            if (r8 == 0) goto L_0x04da
            r8 = 8
            goto L_0x04db
        L_0x04da:
            r8 = 0
        L_0x04db:
            android.content.ComponentName r9 = r3.getComponent()     // Catch:{ Exception -> 0x0855 }
            if (r9 != 0) goto L_0x04e6
            java.lang.String r12 = r3.getPackage()     // Catch:{ Exception -> 0x04b0 }
            goto L_0x04ea
        L_0x04e6:
            java.lang.String r12 = r9.getPackageName()     // Catch:{ Exception -> 0x0855 }
        L_0x04ea:
            android.os.UserHandle r13 = android.os.Process.myUserHandle()     // Catch:{ Exception -> 0x0855 }
            android.os.UserHandle r15 = r14.user     // Catch:{ Exception -> 0x0855 }
            boolean r13 = r13.equals(r15)     // Catch:{ Exception -> 0x0855 }
            if (r13 != 0) goto L_0x050b
            int r13 = r14.itemType     // Catch:{ Exception -> 0x04b0 }
            r15 = 1
            if (r13 != r15) goto L_0x0501
            java.lang.String r3 = "Legacy shortcuts are only allowed for default user"
            r14.markDeleted(r3)     // Catch:{ Exception -> 0x04b0 }
            goto L_0x049c
        L_0x0501:
            int r13 = r14.restoreFlag     // Catch:{ Exception -> 0x04b0 }
            if (r13 == 0) goto L_0x050b
            java.lang.String r3 = "Restore from managed profile not supported"
            r14.markDeleted(r3)     // Catch:{ Exception -> 0x04b0 }
            goto L_0x049c
        L_0x050b:
            boolean r13 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x0855 }
            if (r13 == 0) goto L_0x051c
            int r13 = r14.itemType     // Catch:{ Exception -> 0x04b0 }
            r15 = 1
            if (r13 == r15) goto L_0x051c
            java.lang.String r3 = "Only legacy shortcuts can have null package"
            r14.markDeleted(r3)     // Catch:{ Exception -> 0x04b0 }
            goto L_0x049c
        L_0x051c:
            boolean r13 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x0855 }
            if (r13 != 0) goto L_0x052f
            com.android.launcher3.compat.LauncherAppsCompat r13 = r1.mLauncherApps     // Catch:{ Exception -> 0x04b0 }
            android.os.UserHandle r15 = r14.user     // Catch:{ Exception -> 0x04b0 }
            boolean r13 = r13.isPackageEnabledForProfile(r12, r15)     // Catch:{ Exception -> 0x04b0 }
            if (r13 == 0) goto L_0x052d
            goto L_0x052f
        L_0x052d:
            r13 = 0
            goto L_0x0530
        L_0x052f:
            r13 = 1
        L_0x0530:
            if (r9 == 0) goto L_0x05b1
            if (r13 == 0) goto L_0x05b1
            com.android.launcher3.compat.LauncherAppsCompat r15 = r1.mLauncherApps     // Catch:{ Exception -> 0x0596 }
            r38 = r2
            android.os.UserHandle r2 = r14.user     // Catch:{ Exception -> 0x0594 }
            boolean r2 = r15.isActivityEnabledForProfile(r9, r2)     // Catch:{ Exception -> 0x0594 }
            if (r2 == 0) goto L_0x054a
            r14.markRestored()     // Catch:{ Exception -> 0x0545 }
            goto L_0x05b3
        L_0x0545:
            r0 = move-exception
            r2 = r0
            r39 = r4
            goto L_0x059c
        L_0x054a:
            r2 = 2
            boolean r2 = r14.hasRestoreFlag(r2)     // Catch:{ Exception -> 0x0594 }
            if (r2 == 0) goto L_0x057c
            android.os.UserHandle r2 = r14.user     // Catch:{ Exception -> 0x0594 }
            android.content.Intent r3 = r10.getAppLaunchIntent(r12, r2)     // Catch:{ Exception -> 0x0594 }
            if (r3 == 0) goto L_0x0573
            r2 = 0
            r14.restoreFlag = r2     // Catch:{ Exception -> 0x0594 }
            com.android.launcher3.util.ContentWriter r9 = r14.updater()     // Catch:{ Exception -> 0x0594 }
            java.lang.String r15 = "intent"
            r39 = r4
            java.lang.String r4 = r3.toUri(r2)     // Catch:{ Exception -> 0x060b }
            com.android.launcher3.util.ContentWriter r2 = r9.put(r15, r4)     // Catch:{ Exception -> 0x060b }
            r2.commit()     // Catch:{ Exception -> 0x060b }
            r3.getComponent()     // Catch:{ Exception -> 0x060b }
            goto L_0x05b5
        L_0x0573:
            r39 = r4
            java.lang.String r2 = "Unable to find a launch target"
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x060b }
            goto L_0x04a0
        L_0x057c:
            r39 = r4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x060b }
            r2.<init>()     // Catch:{ Exception -> 0x060b }
            java.lang.String r3 = "Invalid component removed: "
            r2.append(r3)     // Catch:{ Exception -> 0x060b }
            r2.append(r9)     // Catch:{ Exception -> 0x060b }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x060b }
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x060b }
            goto L_0x04a0
        L_0x0594:
            r0 = move-exception
            goto L_0x0599
        L_0x0596:
            r0 = move-exception
            r38 = r2
        L_0x0599:
            r39 = r4
        L_0x059b:
            r2 = r0
        L_0x059c:
            r40 = r5
            r41 = r6
            r6 = r22
            r9 = r23
            r4 = r24
        L_0x05a6:
            r12 = r33
            r5 = r34
            r42 = r35
            r43 = r36
        L_0x05ae:
            r8 = 1
            goto L_0x08aa
        L_0x05b1:
            r38 = r2
        L_0x05b3:
            r39 = r4
        L_0x05b5:
            boolean r2 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x0853 }
            if (r2 != 0) goto L_0x0667
            if (r13 != 0) goto L_0x0667
            int r2 = r14.restoreFlag     // Catch:{ Exception -> 0x0659 }
            if (r2 == 0) goto L_0x060d
            java.lang.String r2 = "LoaderTask"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x060b }
            r4.<init>()     // Catch:{ Exception -> 0x060b }
            java.lang.String r9 = "package not yet restored: "
            r4.append(r9)     // Catch:{ Exception -> 0x060b }
            r4.append(r12)     // Catch:{ Exception -> 0x060b }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x060b }
            com.android.launcher3.logging.FileLog.m11d(r2, r4)     // Catch:{ Exception -> 0x060b }
            r2 = 8
            boolean r4 = r14.hasRestoreFlag(r2)     // Catch:{ Exception -> 0x060b }
            if (r4 == 0) goto L_0x05e1
            goto L_0x0667
        L_0x05e1:
            boolean r4 = r7.containsKey(r12)     // Catch:{ Exception -> 0x060b }
            if (r4 == 0) goto L_0x05f5
            int r4 = r14.restoreFlag     // Catch:{ Exception -> 0x060b }
            r2 = r2 | r4
            r14.restoreFlag = r2     // Catch:{ Exception -> 0x060b }
            com.android.launcher3.util.ContentWriter r2 = r14.updater()     // Catch:{ Exception -> 0x060b }
            r2.commit()     // Catch:{ Exception -> 0x060b }
            goto L_0x0667
        L_0x05f5:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x060b }
            r2.<init>()     // Catch:{ Exception -> 0x060b }
            java.lang.String r3 = "Unrestored app removed: "
            r2.append(r3)     // Catch:{ Exception -> 0x060b }
            r2.append(r12)     // Catch:{ Exception -> 0x060b }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x060b }
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x060b }
            goto L_0x04a0
        L_0x060b:
            r0 = move-exception
            goto L_0x059b
        L_0x060d:
            android.os.UserHandle r2 = r14.user     // Catch:{ Exception -> 0x0659 }
            boolean r2 = r10.isAppOnSdcard(r12, r2)     // Catch:{ Exception -> 0x0659 }
            if (r2 == 0) goto L_0x061b
            r8 = r8 | 2
            r4 = r24
        L_0x0619:
            r2 = 1
            goto L_0x066a
        L_0x061b:
            if (r25 != 0) goto L_0x063b
            java.lang.String r2 = "LoaderTask"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0659 }
            r4.<init>()     // Catch:{ Exception -> 0x0659 }
            java.lang.String r9 = "Missing pkg, will check later: "
            r4.append(r9)     // Catch:{ Exception -> 0x0659 }
            r4.append(r12)     // Catch:{ Exception -> 0x0659 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0659 }
            android.util.Log.d(r2, r4)     // Catch:{ Exception -> 0x0659 }
            android.os.UserHandle r2 = r14.user     // Catch:{ Exception -> 0x0659 }
            r4 = r24
            r4.addToList(r2, r12)     // Catch:{ Exception -> 0x0677 }
            goto L_0x0619
        L_0x063b:
            r4 = r24
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0677 }
            r2.<init>()     // Catch:{ Exception -> 0x0677 }
            java.lang.String r3 = "Invalid package removed: "
            r2.append(r3)     // Catch:{ Exception -> 0x0677 }
            r2.append(r12)     // Catch:{ Exception -> 0x0677 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0677 }
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x0677 }
            r40 = r5
            r41 = r6
            r9 = r23
            goto L_0x04a8
        L_0x0659:
            r0 = move-exception
            r4 = r24
        L_0x065c:
            r2 = r0
            r40 = r5
            r41 = r6
            r6 = r22
            r9 = r23
            goto L_0x05a6
        L_0x0667:
            r4 = r24
            r2 = 0
        L_0x066a:
            int r9 = r14.restoreFlag     // Catch:{ Exception -> 0x0849 }
            r9 = r9 & 16
            if (r9 == 0) goto L_0x0671
            r13 = 0
        L_0x0671:
            if (r13 == 0) goto L_0x0679
            r14.markRestored()     // Catch:{ Exception -> 0x0677 }
            goto L_0x0679
        L_0x0677:
            r0 = move-exception
            goto L_0x065c
        L_0x0679:
            boolean r9 = r14.isOnWorkspaceOrHotseat()     // Catch:{ Exception -> 0x0849 }
            if (r9 != 0) goto L_0x06ad
            r9 = r23
            int r13 = r14.getInt(r9)     // Catch:{ Exception -> 0x06a3 }
            r15 = r36
            boolean r13 = r15.isItemInPreview(r13)     // Catch:{ Exception -> 0x0691 }
            if (r13 != 0) goto L_0x06b1
            r40 = r5
            r13 = 1
            goto L_0x06b4
        L_0x0691:
            r0 = move-exception
            r2 = r0
            r40 = r5
        L_0x0695:
            r41 = r6
            r43 = r15
            r6 = r22
            r12 = r33
            r5 = r34
            r42 = r35
            goto L_0x05ae
        L_0x06a3:
            r0 = move-exception
            r2 = r0
            r40 = r5
            r41 = r6
            r6 = r22
            goto L_0x05a6
        L_0x06ad:
            r9 = r23
            r15 = r36
        L_0x06b1:
            r40 = r5
            r13 = 0
        L_0x06b4:
            int r5 = r14.restoreFlag     // Catch:{ Exception -> 0x083d }
            if (r5 == 0) goto L_0x06cb
            com.android.launcher3.ShortcutInfo r2 = r14.getRestoredItemInfo(r3)     // Catch:{ Exception -> 0x06c8 }
        L_0x06bc:
            r41 = r6
            r43 = r15
            r6 = r22
            r5 = r34
            r42 = r35
            goto L_0x07e3
        L_0x06c8:
            r0 = move-exception
            r2 = r0
            goto L_0x0695
        L_0x06cb:
            int r5 = r14.itemType     // Catch:{ Exception -> 0x083d }
            if (r5 != 0) goto L_0x06d4
            com.android.launcher3.ShortcutInfo r2 = r14.getAppShortcutInfo(r3, r2, r13)     // Catch:{ Exception -> 0x06c8 }
            goto L_0x06bc
        L_0x06d4:
            int r2 = r14.itemType     // Catch:{ Exception -> 0x083d }
            r5 = 6
            if (r2 != r5) goto L_0x0799
            android.os.UserHandle r2 = r14.user     // Catch:{ Exception -> 0x0789 }
            com.android.launcher3.shortcuts.ShortcutKey r2 = com.android.launcher3.shortcuts.ShortcutKey.fromIntent(r3, r2)     // Catch:{ Exception -> 0x0789 }
            r41 = r6
            long r5 = r14.serialNumber     // Catch:{ Exception -> 0x0787 }
            r13 = r35
            java.lang.Object r5 = r13.get(r5)     // Catch:{ Exception -> 0x077d }
            java.lang.Boolean r5 = (java.lang.Boolean) r5     // Catch:{ Exception -> 0x077d }
            boolean r5 = r5.booleanValue()     // Catch:{ Exception -> 0x077d }
            if (r5 == 0) goto L_0x0768
            r5 = r34
            java.lang.Object r2 = r5.get(r2)     // Catch:{ Exception -> 0x0760 }
            com.android.launcher3.shortcuts.ShortcutInfoCompat r2 = (com.android.launcher3.shortcuts.ShortcutInfoCompat) r2     // Catch:{ Exception -> 0x0760 }
            if (r2 != 0) goto L_0x0726
            java.lang.String r2 = "Pinned shortcut not found"
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x071c }
        L_0x0700:
            r24 = r4
            r20 = r7
            r23 = r9
            r7 = r13
            r3 = r31
            r4 = r32
            r12 = r33
            r2 = r38
            r27 = r39
            r28 = r40
            r8 = r41
            r44 = r15
            r15 = r5
            r5 = r44
            goto L_0x0157
        L_0x071c:
            r0 = move-exception
            r2 = r0
            r42 = r13
            r43 = r15
            r6 = r22
            goto L_0x0795
        L_0x0726:
            com.android.launcher3.ShortcutInfo r3 = new com.android.launcher3.ShortcutInfo     // Catch:{ Exception -> 0x0760 }
            r6 = r22
            r3.<init>(r2, r6)     // Catch:{ Exception -> 0x075a }
            r42 = r13
            com.android.launcher3.model.LoaderTask$1 r13 = new com.android.launcher3.model.LoaderTask$1     // Catch:{ Exception -> 0x0758 }
            r13.<init>(r14, r3)     // Catch:{ Exception -> 0x0758 }
            r43 = r15
            r15 = 1
            android.graphics.Bitmap r13 = com.android.launcher3.graphics.LauncherIcons.createShortcutIcon(r2, r6, r15, r13)     // Catch:{ Exception -> 0x07b8 }
            r3.iconBitmap = r13     // Catch:{ Exception -> 0x07b8 }
            java.lang.String r2 = r2.getPackage()     // Catch:{ Exception -> 0x07b8 }
            android.os.UserHandle r13 = r3.user     // Catch:{ Exception -> 0x07b8 }
            boolean r2 = r10.isAppSuspended(r2, r13)     // Catch:{ Exception -> 0x07b8 }
            if (r2 == 0) goto L_0x074f
            int r2 = r3.isDisabled     // Catch:{ Exception -> 0x07b8 }
            r2 = r2 | 4
            r3.isDisabled = r2     // Catch:{ Exception -> 0x07b8 }
        L_0x074f:
            android.content.Intent r2 = r3.intent     // Catch:{ Exception -> 0x07b8 }
            r44 = r3
            r3 = r2
            r2 = r44
            goto L_0x07e3
        L_0x0758:
            r0 = move-exception
            goto L_0x075d
        L_0x075a:
            r0 = move-exception
            r42 = r13
        L_0x075d:
            r43 = r15
            goto L_0x0794
        L_0x0760:
            r0 = move-exception
            r42 = r13
            r43 = r15
            r6 = r22
            goto L_0x0794
        L_0x0768:
            r42 = r13
            r43 = r15
            r6 = r22
            r5 = r34
            com.android.launcher3.ShortcutInfo r2 = r14.loadSimpleShortcut()     // Catch:{ Exception -> 0x07b8 }
            int r13 = r2.isDisabled     // Catch:{ Exception -> 0x07b8 }
            r15 = 32
            r13 = r13 | r15
            r2.isDisabled = r13     // Catch:{ Exception -> 0x07b8 }
            goto L_0x07e3
        L_0x077d:
            r0 = move-exception
            r42 = r13
            r43 = r15
            r6 = r22
            r5 = r34
            goto L_0x0794
        L_0x0787:
            r0 = move-exception
            goto L_0x078c
        L_0x0789:
            r0 = move-exception
            r41 = r6
        L_0x078c:
            r43 = r15
            r6 = r22
            r5 = r34
            r42 = r35
        L_0x0794:
            r2 = r0
        L_0x0795:
            r12 = r33
            goto L_0x05ae
        L_0x0799:
            r41 = r6
            r43 = r15
            r6 = r22
            r5 = r34
            r42 = r35
            com.android.launcher3.ShortcutInfo r2 = r14.loadSimpleShortcut()     // Catch:{ Exception -> 0x0830 }
            boolean r13 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x0830 }
            if (r13 != 0) goto L_0x07ba
            android.os.UserHandle r13 = r14.user     // Catch:{ Exception -> 0x07b8 }
            boolean r13 = r10.isAppSuspended(r12, r13)     // Catch:{ Exception -> 0x07b8 }
            if (r13 == 0) goto L_0x07ba
            r8 = r8 | 4
            goto L_0x07ba
        L_0x07b8:
            r0 = move-exception
            goto L_0x0794
        L_0x07ba:
            java.lang.String r13 = r3.getAction()     // Catch:{ Exception -> 0x0830 }
            if (r13 == 0) goto L_0x07e3
            java.util.Set r13 = r3.getCategories()     // Catch:{ Exception -> 0x07b8 }
            if (r13 == 0) goto L_0x07e3
            java.lang.String r13 = r3.getAction()     // Catch:{ Exception -> 0x07b8 }
            java.lang.String r15 = "android.intent.action.MAIN"
            boolean r13 = r13.equals(r15)     // Catch:{ Exception -> 0x07b8 }
            if (r13 == 0) goto L_0x07e3
            java.util.Set r13 = r3.getCategories()     // Catch:{ Exception -> 0x07b8 }
            java.lang.String r15 = "android.intent.category.LAUNCHER"
            boolean r13 = r13.contains(r15)     // Catch:{ Exception -> 0x07b8 }
            if (r13 == 0) goto L_0x07e3
            r13 = 270532608(0x10200000, float:3.1554436E-29)
            r3.addFlags(r13)     // Catch:{ Exception -> 0x07b8 }
        L_0x07e3:
            if (r2 == 0) goto L_0x0832
            r14.applyCommonProperties(r2)     // Catch:{ Exception -> 0x0830 }
            r2.intent = r3     // Catch:{ Exception -> 0x0830 }
            int r13 = r14.getInt(r9)     // Catch:{ Exception -> 0x0830 }
            r2.rank = r13     // Catch:{ Exception -> 0x0830 }
            r13 = 1
            r2.spanX = r13     // Catch:{ Exception -> 0x0830 }
            r2.spanY = r13     // Catch:{ Exception -> 0x0830 }
            int r13 = r2.isDisabled     // Catch:{ Exception -> 0x0830 }
            r8 = r8 | r13
            r2.isDisabled = r8     // Catch:{ Exception -> 0x0830 }
            if (r11 == 0) goto L_0x0809
            boolean r3 = com.android.launcher3.Utilities.isSystemApp(r6, r3)     // Catch:{ Exception -> 0x0830 }
            if (r3 != 0) goto L_0x0809
            int r3 = r2.isDisabled     // Catch:{ Exception -> 0x0830 }
            r8 = 1
            r3 = r3 | r8
            r2.isDisabled = r3     // Catch:{ Exception -> 0x083b }
            goto L_0x080a
        L_0x0809:
            r8 = 1
        L_0x080a:
            int r3 = r14.restoreFlag     // Catch:{ Exception -> 0x083b }
            if (r3 == 0) goto L_0x082a
            boolean r3 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x083b }
            if (r3 != 0) goto L_0x082a
            java.lang.Object r3 = r7.get(r12)     // Catch:{ Exception -> 0x083b }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ Exception -> 0x083b }
            if (r3 == 0) goto L_0x0824
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x083b }
            r2.setInstallProgress(r3)     // Catch:{ Exception -> 0x083b }
            goto L_0x082a
        L_0x0824:
            int r3 = r2.status     // Catch:{ Exception -> 0x083b }
            r3 = r3 & -5
            r2.status = r3     // Catch:{ Exception -> 0x083b }
        L_0x082a:
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ Exception -> 0x083b }
            r14.checkAndAddItem(r2, r3)     // Catch:{ Exception -> 0x083b }
            goto L_0x086f
        L_0x0830:
            r0 = move-exception
            goto L_0x086a
        L_0x0832:
            r8 = 1
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x083b }
            java.lang.String r3 = "Unexpected null ShortcutInfo"
            r2.<init>(r3)     // Catch:{ Exception -> 0x083b }
            throw r2     // Catch:{ Exception -> 0x083b }
        L_0x083b:
            r0 = move-exception
            goto L_0x086b
        L_0x083d:
            r0 = move-exception
            r41 = r6
            r43 = r15
            r6 = r22
            r5 = r34
            r42 = r35
            goto L_0x086a
        L_0x0849:
            r0 = move-exception
            r40 = r5
            r41 = r6
            r6 = r22
            r9 = r23
            goto L_0x0864
        L_0x0853:
            r0 = move-exception
            goto L_0x085a
        L_0x0855:
            r0 = move-exception
            r38 = r2
            r39 = r4
        L_0x085a:
            r40 = r5
            r41 = r6
            r6 = r22
            r9 = r23
            r4 = r24
        L_0x0864:
            r5 = r34
            r42 = r35
            r43 = r36
        L_0x086a:
            r8 = 1
        L_0x086b:
            r2 = r0
            r12 = r33
            goto L_0x08aa
        L_0x086f:
            r12 = r33
        L_0x0871:
            r24 = r4
            r15 = r5
            r22 = r6
            r20 = r7
            r23 = r9
            r3 = r31
            r4 = r32
            r2 = r38
            r27 = r39
            r28 = r40
            r8 = r41
            r7 = r42
            r5 = r43
            goto L_0x0157
        L_0x088c:
            r0 = move-exception
            r38 = r2
            r31 = r3
            r32 = r4
            r43 = r5
            r42 = r7
            r41 = r8
            r33 = r12
        L_0x089b:
            r5 = r15
            r7 = r20
            r6 = r22
            r9 = r23
            r4 = r24
            r39 = r27
            r40 = r28
        L_0x08a8:
            r8 = 1
            r2 = r0
        L_0x08aa:
            java.lang.String r3 = "LoaderTask"
            java.lang.String r13 = "Desktop items loading interrupted"
            android.util.Log.e(r3, r13, r2)     // Catch:{ all -> 0x0a1c }
            goto L_0x0871
        L_0x08b2:
            r5 = r15
            r6 = r22
            r4 = r24
            com.android.launcher3.Utilities.closeSilently(r14)     // Catch:{ all -> 0x0a28 }
            boolean r2 = r1.mStopped     // Catch:{ all -> 0x0a28 }
            if (r2 == 0) goto L_0x08c5
            com.android.launcher3.model.BgDataModel r2 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            r2.clear()     // Catch:{ all -> 0x0a28 }
            monitor-exit(r16)     // Catch:{ all -> 0x0a28 }
            return
        L_0x08c5:
            boolean r2 = r14.commitDeleted()     // Catch:{ all -> 0x0a28 }
            if (r2 == 0) goto L_0x0912
            java.lang.String r2 = "delete_empty_folders"
            r3 = r21
            android.os.Bundle r2 = com.android.launcher3.LauncherSettings.Settings.call(r3, r2)     // Catch:{ all -> 0x0a28 }
            java.lang.String r7 = "value"
            java.io.Serializable r2 = r2.getSerializable(r7)     // Catch:{ all -> 0x0a28 }
            java.util.ArrayList r2 = (java.util.ArrayList) r2     // Catch:{ all -> 0x0a28 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x0a28 }
        L_0x08df:
            boolean r7 = r2.hasNext()     // Catch:{ all -> 0x0a28 }
            if (r7 == 0) goto L_0x090d
            java.lang.Object r7 = r2.next()     // Catch:{ all -> 0x0a28 }
            java.lang.Long r7 = (java.lang.Long) r7     // Catch:{ all -> 0x0a28 }
            long r7 = r7.longValue()     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.model.BgDataModel r9 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            java.util.ArrayList<com.android.launcher3.ItemInfo> r9 = r9.workspaceItems     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.model.BgDataModel r10 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r10 = r10.folders     // Catch:{ all -> 0x0a28 }
            java.lang.Object r10 = r10.get(r7)     // Catch:{ all -> 0x0a28 }
            r9.remove(r10)     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.model.BgDataModel r9 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r9 = r9.folders     // Catch:{ all -> 0x0a28 }
            r9.remove(r7)     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.model.BgDataModel r9 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r9 = r9.itemsIdMap     // Catch:{ all -> 0x0a28 }
            r9.remove(r7)     // Catch:{ all -> 0x0a28 }
            goto L_0x08df
        L_0x090d:
            java.lang.String r2 = "remove_ghost_widgets"
            com.android.launcher3.LauncherSettings.Settings.call(r3, r2)     // Catch:{ all -> 0x0a28 }
        L_0x0912:
            boolean r2 = com.android.launcher3.Utilities.ATLEAST_NOUGAT_MR1     // Catch:{ all -> 0x0a28 }
            if (r2 == 0) goto L_0x094a
            java.util.HashSet r2 = com.android.launcher3.InstallShortcutReceiver.getPendingShortcuts(r6)     // Catch:{ all -> 0x0a28 }
            java.util.Set r3 = r5.keySet()     // Catch:{ all -> 0x0a28 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0a28 }
        L_0x0922:
            boolean r5 = r3.hasNext()     // Catch:{ all -> 0x0a28 }
            if (r5 == 0) goto L_0x094a
            java.lang.Object r5 = r3.next()     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.shortcuts.ShortcutKey r5 = (com.android.launcher3.shortcuts.ShortcutKey) r5     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.model.BgDataModel r7 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, android.util.MutableInt> r7 = r7.pinnedShortcutCounts     // Catch:{ all -> 0x0a28 }
            java.lang.Object r7 = r7.get(r5)     // Catch:{ all -> 0x0a28 }
            android.util.MutableInt r7 = (android.util.MutableInt) r7     // Catch:{ all -> 0x0a28 }
            if (r7 == 0) goto L_0x093e
            int r7 = r7.value     // Catch:{ all -> 0x0a28 }
            if (r7 != 0) goto L_0x0922
        L_0x093e:
            boolean r7 = r2.contains(r5)     // Catch:{ all -> 0x0a28 }
            if (r7 != 0) goto L_0x0922
            com.android.launcher3.shortcuts.DeepShortcutManager r7 = r1.mShortcutManager     // Catch:{ all -> 0x0a28 }
            r7.unpinShortcut(r5)     // Catch:{ all -> 0x0a28 }
            goto L_0x0922
        L_0x094a:
            com.android.launcher3.folder.FolderIconPreviewVerifier r2 = new com.android.launcher3.folder.FolderIconPreviewVerifier     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.LauncherAppState r3 = r1.mApp     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.InvariantDeviceProfile r3 = r3.getInvariantDeviceProfile()     // Catch:{ all -> 0x0a28 }
            r2.<init>(r3)     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r3 = r3.folders     // Catch:{ all -> 0x0a28 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0a28 }
        L_0x095d:
            boolean r5 = r3.hasNext()     // Catch:{ all -> 0x0a28 }
            if (r5 == 0) goto L_0x09a7
            java.lang.Object r5 = r3.next()     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.FolderInfo r5 = (com.android.launcher3.FolderInfo) r5     // Catch:{ all -> 0x0a28 }
            java.util.ArrayList<com.android.launcher3.ShortcutInfo> r7 = r5.contents     // Catch:{ all -> 0x0a28 }
            java.util.Comparator<com.android.launcher3.ItemInfo> r8 = com.android.launcher3.folder.Folder.ITEM_POS_COMPARATOR     // Catch:{ all -> 0x0a28 }
            java.util.Collections.sort(r7, r8)     // Catch:{ all -> 0x0a28 }
            r2.setFolderInfo(r5)     // Catch:{ all -> 0x0a28 }
            java.util.ArrayList<com.android.launcher3.ShortcutInfo> r5 = r5.contents     // Catch:{ all -> 0x0a28 }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x0a28 }
            r7 = 0
        L_0x097a:
            boolean r8 = r5.hasNext()     // Catch:{ all -> 0x0a28 }
            if (r8 == 0) goto L_0x09a5
            java.lang.Object r8 = r5.next()     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.ShortcutInfo r8 = (com.android.launcher3.ShortcutInfo) r8     // Catch:{ all -> 0x0a28 }
            boolean r9 = r8.usingLowResIcon     // Catch:{ all -> 0x0a28 }
            if (r9 == 0) goto L_0x099f
            int r9 = r8.itemType     // Catch:{ all -> 0x0a28 }
            if (r9 != 0) goto L_0x099f
            int r9 = r8.rank     // Catch:{ all -> 0x0a28 }
            boolean r9 = r2.isItemInPreview(r9)     // Catch:{ all -> 0x0a28 }
            if (r9 == 0) goto L_0x099f
            com.android.launcher3.IconCache r9 = r1.mIconCache     // Catch:{ all -> 0x0a28 }
            r10 = 0
            r9.getTitleAndIcon(r8, r10)     // Catch:{ all -> 0x0a28 }
            int r7 = r7 + 1
            goto L_0x09a0
        L_0x099f:
            r10 = 0
        L_0x09a0:
            int r8 = com.android.launcher3.folder.FolderIcon.NUM_ITEMS_IN_PREVIEW     // Catch:{ all -> 0x0a28 }
            if (r7 < r8) goto L_0x097a
            goto L_0x095d
        L_0x09a5:
            r10 = 0
            goto L_0x095d
        L_0x09a7:
            r14.commitRestoredItems()     // Catch:{ all -> 0x0a28 }
            if (r25 != 0) goto L_0x09cd
            boolean r2 = r4.isEmpty()     // Catch:{ all -> 0x0a28 }
            if (r2 != 0) goto L_0x09cd
            com.android.launcher3.model.SdCardAvailableReceiver r2 = new com.android.launcher3.model.SdCardAvailableReceiver     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.LauncherAppState r3 = r1.mApp     // Catch:{ all -> 0x0a28 }
            r2.<init>(r3, r4)     // Catch:{ all -> 0x0a28 }
            android.content.IntentFilter r3 = new android.content.IntentFilter     // Catch:{ all -> 0x0a28 }
            java.lang.String r4 = "android.intent.action.BOOT_COMPLETED"
            r3.<init>(r4)     // Catch:{ all -> 0x0a28 }
            android.os.Handler r4 = new android.os.Handler     // Catch:{ all -> 0x0a28 }
            android.os.Looper r5 = com.android.launcher3.LauncherModel.getWorkerLooper()     // Catch:{ all -> 0x0a28 }
            r4.<init>(r5)     // Catch:{ all -> 0x0a28 }
            r5 = 0
            r6.registerReceiver(r2, r3, r5, r4)     // Catch:{ all -> 0x0a28 }
        L_0x09cd:
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            java.util.ArrayList<java.lang.Long> r3 = r3.workspaceScreens     // Catch:{ all -> 0x0a28 }
            r2.<init>(r3)     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r3 = r3.itemsIdMap     // Catch:{ all -> 0x0a28 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0a28 }
        L_0x09de:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x0a28 }
            if (r4 == 0) goto L_0x0a06
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.ItemInfo r4 = (com.android.launcher3.ItemInfo) r4     // Catch:{ all -> 0x0a28 }
            long r7 = r4.screenId     // Catch:{ all -> 0x0a28 }
            long r4 = r4.container     // Catch:{ all -> 0x0a28 }
            r9 = -100
            int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r4 != 0) goto L_0x09de
            java.lang.Long r4 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0a28 }
            boolean r4 = r2.contains(r4)     // Catch:{ all -> 0x0a28 }
            if (r4 == 0) goto L_0x09de
            java.lang.Long r4 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0a28 }
            r2.remove(r4)     // Catch:{ all -> 0x0a28 }
            goto L_0x09de
        L_0x0a06:
            int r3 = r2.size()     // Catch:{ all -> 0x0a28 }
            if (r3 == 0) goto L_0x0a1a
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            java.util.ArrayList<java.lang.Long> r3 = r3.workspaceScreens     // Catch:{ all -> 0x0a28 }
            r3.removeAll(r2)     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.model.BgDataModel r2 = r1.mBgDataModel     // Catch:{ all -> 0x0a28 }
            java.util.ArrayList<java.lang.Long> r2 = r2.workspaceScreens     // Catch:{ all -> 0x0a28 }
            com.android.launcher3.LauncherModel.updateWorkspaceScreenOrder(r6, r2)     // Catch:{ all -> 0x0a28 }
        L_0x0a1a:
            monitor-exit(r16)     // Catch:{ all -> 0x0a28 }
            return
        L_0x0a1c:
            r0 = move-exception
            r2 = r0
            com.android.launcher3.Utilities.closeSilently(r14)     // Catch:{ all -> 0x0a28 }
            throw r2     // Catch:{ all -> 0x0a28 }
        L_0x0a22:
            r0 = move-exception
            r16 = r8
        L_0x0a25:
            r2 = r0
            monitor-exit(r16)     // Catch:{ all -> 0x0a28 }
            throw r2
        L_0x0a28:
            r0 = move-exception
            goto L_0x0a25
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.LoaderTask.loadWorkspace():void");
    }

    private void updateIconCache() {
        HashSet hashSet = new HashSet();
        synchronized (this.mBgDataModel) {
            Iterator it = this.mBgDataModel.itemsIdMap.iterator();
            while (it.hasNext()) {
                ItemInfo itemInfo = (ItemInfo) it.next();
                if (itemInfo instanceof ShortcutInfo) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                    if (shortcutInfo.isPromise() && shortcutInfo.getTargetComponent() != null) {
                        hashSet.add(shortcutInfo.getTargetComponent().getPackageName());
                    }
                } else if (itemInfo instanceof LauncherAppWidgetInfo) {
                    LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
                    if (launcherAppWidgetInfo.hasRestoreFlag(2)) {
                        hashSet.add(launcherAppWidgetInfo.providerName.getPackageName());
                    }
                }
            }
        }
        this.mIconCache.updateDbIcons(hashSet);
    }

    private void loadAllApps() {
        List<UserHandle> userProfiles = this.mUserManager.getUserProfiles();
        this.mBgAllAppsList.clear();
        for (UserHandle userHandle : userProfiles) {
            List activityList = this.mLauncherApps.getActivityList(null, userHandle);
            if (activityList != null && !activityList.isEmpty()) {
                boolean isQuietModeEnabled = this.mUserManager.isQuietModeEnabled(userHandle);
                for (int i = 0; i < activityList.size(); i++) {
                    LauncherActivityInfo launcherActivityInfo = (LauncherActivityInfo) activityList.get(i);
                    this.mBgAllAppsList.add(new AppInfo(launcherActivityInfo, userHandle, isQuietModeEnabled), launcherActivityInfo);
                }
                ManagedProfileHeuristic.onAllAppsLoaded(this.mApp.getContext(), activityList, userHandle);
            }
        }
        this.mBgAllAppsList.added = new ArrayList<>();
    }

    private void loadDeepShortcuts() {
        this.mBgDataModel.deepShortcutMap.clear();
        this.mBgDataModel.hasShortcutHostPermission = this.mShortcutManager.hasHostPermission();
        if (this.mBgDataModel.hasShortcutHostPermission) {
            for (UserHandle userHandle : this.mUserManager.getUserProfiles()) {
                if (this.mUserManager.isUserUnlocked(userHandle)) {
                    this.mBgDataModel.updateDeepShortcutMap(null, userHandle, this.mShortcutManager.queryForAllShortcuts(userHandle));
                }
            }
        }
    }

    public static boolean isValidProvider(AppWidgetProviderInfo appWidgetProviderInfo) {
        return (appWidgetProviderInfo == null || appWidgetProviderInfo.provider == null || appWidgetProviderInfo.provider.getPackageName() == null) ? false : true;
    }
}
