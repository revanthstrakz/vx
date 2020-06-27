package com.lody.virtual.server.p009pm;

import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.collection.IntArray;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.helper.utils.FileUtils.FileMode;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;
import com.lody.virtual.server.IAppManager.Stub;
import com.lody.virtual.server.accounts.VAccountManagerService;
import com.lody.virtual.server.interfaces.IAppRequestListener;
import com.lody.virtual.server.interfaces.IPackageObserver;
import com.lody.virtual.server.p008am.BroadcastSystem;
import com.lody.virtual.server.p008am.UidSystem;
import com.lody.virtual.server.p008am.VActivityManagerService;
import com.lody.virtual.server.p009pm.parser.PackageParserEx;
import com.lody.virtual.server.p009pm.parser.VPackage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/* renamed from: com.lody.virtual.server.pm.VAppManagerService */
public class VAppManagerService extends Stub {
    private static final String TAG = "VAppManagerService";
    private static final AtomicReference<VAppManagerService> sService = new AtomicReference<>();
    /* access modifiers changed from: private */
    public IAppRequestListener mAppRequestListener;
    private boolean mBooting;
    private final PackagePersistenceLayer mPersistenceLayer = new PackagePersistenceLayer(this);
    private RemoteCallbackList<IPackageObserver> mRemoteCallbackList = new RemoteCallbackList<>();
    private final UidSystem mUidSystem = new UidSystem();
    private final Set<String> mVisibleOutsidePackages = new HashSet();

    public static VAppManagerService get() {
        return (VAppManagerService) sService.get();
    }

    public static void systemReady() {
        VEnvironment.systemReady();
        VAppManagerService vAppManagerService = new VAppManagerService();
        vAppManagerService.mUidSystem.initUidList();
        sService.set(vAppManagerService);
    }

    public boolean isBooting() {
        return this.mBooting;
    }

    public void scanApps() {
        if (!this.mBooting) {
            synchronized (this) {
                this.mBooting = true;
                this.mPersistenceLayer.read();
                PrivilegeAppOptimizer.get().performOptimizeAllApps();
                this.mBooting = false;
            }
        }
    }

    private void cleanUpResidualFiles(PackageSetting packageSetting) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("cleanUpResidualFiles: ");
        sb.append(packageSetting.packageName);
        VLog.m91w(str, sb.toString(), new Object[0]);
        FileUtils.deleteDir(VEnvironment.getDataAppPackageDirectory(packageSetting.packageName));
    }

    /* access modifiers changed from: 0000 */
    public synchronized void loadPackage(PackageSetting packageSetting) {
        if (!loadPackageInnerLocked(packageSetting)) {
            cleanUpResidualFiles(packageSetting);
        }
    }

    private boolean loadPackageInnerLocked(PackageSetting packageSetting) {
        if (packageSetting.dependSystem && !VirtualCore.get().isOutsideInstalled(packageSetting.packageName)) {
            return false;
        }
        File packageCacheFile = VEnvironment.getPackageCacheFile(packageSetting.packageName);
        VPackage vPackage = null;
        try {
            vPackage = PackageParserEx.readPackageCache(packageSetting.packageName);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        if (vPackage == null || vPackage.packageName == null) {
            return false;
        }
        chmodPackageDictionary(packageCacheFile);
        PackageCacheManager.put(vPackage, packageSetting);
        BroadcastSystem.get().startApp(vPackage);
        return true;
    }

    public boolean isOutsidePackageVisible(String str) {
        return str != null && this.mVisibleOutsidePackages.contains(str);
    }

    public void addVisibleOutsidePackage(String str) {
        if (str != null) {
            this.mVisibleOutsidePackages.add(str);
        }
    }

    public void removeVisibleOutsidePackage(String str) {
        if (str != null) {
            this.mVisibleOutsidePackages.remove(str);
        }
    }

    public InstallResult installPackage(String str, int i) {
        return installPackage(str, i, true);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(8:66|(2:71|(1:75))(1:70)|76|77|78|79|80|81) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:78:0x012d */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x019a A[Catch:{ Throwable -> 0x002f }] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x01b8 A[SYNTHETIC, Splitter:B:112:0x01b8] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01d9 A[Catch:{ Throwable -> 0x002f }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00cd A[Catch:{ Throwable -> 0x002f }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0138  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x013b A[SYNTHETIC, Splitter:B:84:0x013b] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0145 A[Catch:{ Throwable -> 0x002f }] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0146 A[Catch:{ Throwable -> 0x002f }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x016d A[Catch:{ Throwable -> 0x002f }] */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0170 A[Catch:{ Throwable -> 0x002f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized com.lody.virtual.remote.InstallResult installPackage(java.lang.String r17, int r18, boolean r19) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            r3 = r18
            monitor-enter(r16)
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x01f1 }
            if (r2 != 0) goto L_0x0015
            java.lang.String r0 = "path = NULL"
            com.lody.virtual.remote.InstallResult r0 = com.lody.virtual.remote.InstallResult.makeFailure(r0)     // Catch:{ all -> 0x01f1 }
            monitor-exit(r16)
            return r0
        L_0x0015:
            java.io.File r6 = new java.io.File     // Catch:{ all -> 0x01f1 }
            r6.<init>(r2)     // Catch:{ all -> 0x01f1 }
            boolean r0 = r6.exists()     // Catch:{ all -> 0x01f1 }
            if (r0 == 0) goto L_0x01e9
            boolean r0 = r6.isFile()     // Catch:{ all -> 0x01f1 }
            if (r0 != 0) goto L_0x0028
            goto L_0x01e9
        L_0x0028:
            r7 = 0
            com.lody.virtual.server.pm.parser.VPackage r0 = com.lody.virtual.server.p009pm.parser.PackageParserEx.parsePackage(r6)     // Catch:{ Throwable -> 0x002f }
            r8 = r0
            goto L_0x0035
        L_0x002f:
            r0 = move-exception
            r8 = r0
            r8.printStackTrace()     // Catch:{ all -> 0x01f1 }
            r8 = r7
        L_0x0035:
            if (r8 == 0) goto L_0x01e1
            java.lang.String r0 = r8.packageName     // Catch:{ all -> 0x01f1 }
            if (r0 != 0) goto L_0x003d
            goto L_0x01e1
        L_0x003d:
            com.lody.virtual.remote.InstallResult r9 = new com.lody.virtual.remote.InstallResult     // Catch:{ all -> 0x01f1 }
            r9.<init>()     // Catch:{ all -> 0x01f1 }
            java.lang.String r0 = r8.packageName     // Catch:{ all -> 0x01f1 }
            r9.packageName = r0     // Catch:{ all -> 0x01f1 }
            java.lang.String r0 = r8.packageName     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.server.pm.parser.VPackage r0 = com.lody.virtual.server.p009pm.PackageCacheManager.get(r0)     // Catch:{ all -> 0x01f1 }
            if (r0 == 0) goto L_0x0052
            java.lang.Object r7 = r0.mExtras     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.server.pm.PackageSetting r7 = (com.lody.virtual.server.p009pm.PackageSetting) r7     // Catch:{ all -> 0x01f1 }
        L_0x0052:
            r10 = 1
            if (r0 == 0) goto L_0x006d
            r11 = r3 & 16
            if (r11 == 0) goto L_0x005d
            r9.isUpdate = r10     // Catch:{ all -> 0x01f1 }
            monitor-exit(r16)
            return r9
        L_0x005d:
            boolean r11 = r1.canUpdate(r0, r8, r3)     // Catch:{ all -> 0x01f1 }
            if (r11 != 0) goto L_0x006b
            java.lang.String r0 = "Can not update the package (such as version downrange)."
            com.lody.virtual.remote.InstallResult r0 = com.lody.virtual.remote.InstallResult.makeFailure(r0)     // Catch:{ all -> 0x01f1 }
            monitor-exit(r16)
            return r0
        L_0x006b:
            r9.isUpdate = r10     // Catch:{ all -> 0x01f1 }
        L_0x006d:
            java.lang.String r11 = r8.packageName     // Catch:{ all -> 0x01f1 }
            java.io.File r11 = com.lody.virtual.p007os.VEnvironment.getDataAppPackageDirectory(r11)     // Catch:{ all -> 0x01f1 }
            java.io.File r12 = new java.io.File     // Catch:{ all -> 0x01f1 }
            java.lang.String r13 = "lib"
            r12.<init>(r11, r13)     // Catch:{ all -> 0x01f1 }
            boolean r13 = r9.isUpdate     // Catch:{ all -> 0x01f1 }
            r14 = -1
            if (r13 == 0) goto L_0x0094
            com.lody.virtual.helper.utils.FileUtils.deleteDir(r12)     // Catch:{ all -> 0x01f1 }
            java.lang.String r13 = r8.packageName     // Catch:{ all -> 0x01f1 }
            java.io.File r13 = com.lody.virtual.p007os.VEnvironment.getOdexFile(r13)     // Catch:{ all -> 0x01f1 }
            r13.delete()     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.server.am.VActivityManagerService r13 = com.lody.virtual.server.p008am.VActivityManagerService.get()     // Catch:{ all -> 0x01f1 }
            java.lang.String r15 = r8.packageName     // Catch:{ all -> 0x01f1 }
            r13.killAppByPkg(r15, r14)     // Catch:{ all -> 0x01f1 }
        L_0x0094:
            boolean r13 = r12.exists()     // Catch:{ all -> 0x01f1 }
            if (r13 != 0) goto L_0x00a8
            boolean r13 = r12.mkdirs()     // Catch:{ all -> 0x01f1 }
            if (r13 != 0) goto L_0x00a8
            java.lang.String r0 = "Unable to create lib dir."
            com.lody.virtual.remote.InstallResult r0 = com.lody.virtual.remote.InstallResult.makeFailure(r0)     // Catch:{ all -> 0x01f1 }
            monitor-exit(r16)
            return r0
        L_0x00a8:
            r3 = r3 & 32
            r13 = 0
            if (r3 == 0) goto L_0x00bb
            com.lody.virtual.client.core.VirtualCore r3 = com.lody.virtual.client.core.VirtualCore.get()     // Catch:{ all -> 0x01f1 }
            java.lang.String r15 = r8.packageName     // Catch:{ all -> 0x01f1 }
            boolean r3 = r3.isOutsideInstalled(r15)     // Catch:{ all -> 0x01f1 }
            if (r3 == 0) goto L_0x00bb
            r3 = 1
            goto L_0x00bc
        L_0x00bb:
            r3 = 0
        L_0x00bc:
            if (r7 == 0) goto L_0x00c3
            boolean r15 = r7.dependSystem     // Catch:{ all -> 0x01f1 }
            if (r15 == 0) goto L_0x00c3
            r3 = 0
        L_0x00c3:
            java.io.File r15 = new java.io.File     // Catch:{ all -> 0x01f1 }
            r15.<init>(r2)     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.helper.compat.NativeLibraryHelperCompat.copyNativeBinaries(r15, r12)     // Catch:{ all -> 0x01f1 }
            if (r3 != 0) goto L_0x0138
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x01f1 }
            java.lang.String r15 = "base.apk"
            r2.<init>(r11, r15)     // Catch:{ all -> 0x01f1 }
            java.io.File r11 = r2.getParentFile()     // Catch:{ all -> 0x01f1 }
            boolean r15 = r11.exists()     // Catch:{ all -> 0x01f1 }
            if (r15 != 0) goto L_0x0101
            boolean r11 = r11.mkdirs()     // Catch:{ all -> 0x01f1 }
            if (r11 != 0) goto L_0x0101
            java.lang.String r11 = TAG     // Catch:{ all -> 0x01f1 }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f1 }
            r15.<init>()     // Catch:{ all -> 0x01f1 }
            java.lang.String r10 = "Warning: unable to create folder : "
            r15.append(r10)     // Catch:{ all -> 0x01f1 }
            java.lang.String r10 = r2.getPath()     // Catch:{ all -> 0x01f1 }
            r15.append(r10)     // Catch:{ all -> 0x01f1 }
            java.lang.String r10 = r15.toString()     // Catch:{ all -> 0x01f1 }
            java.lang.Object[] r15 = new java.lang.Object[r13]     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.helper.utils.VLog.m91w(r11, r10, r15)     // Catch:{ all -> 0x01f1 }
            goto L_0x0129
        L_0x0101:
            boolean r10 = r2.exists()     // Catch:{ all -> 0x01f1 }
            if (r10 == 0) goto L_0x0129
            boolean r10 = r2.delete()     // Catch:{ all -> 0x01f1 }
            if (r10 != 0) goto L_0x0129
            java.lang.String r10 = TAG     // Catch:{ all -> 0x01f1 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f1 }
            r11.<init>()     // Catch:{ all -> 0x01f1 }
            java.lang.String r15 = "Warning: unable to delete file : "
            r11.append(r15)     // Catch:{ all -> 0x01f1 }
            java.lang.String r15 = r2.getPath()     // Catch:{ all -> 0x01f1 }
            r11.append(r15)     // Catch:{ all -> 0x01f1 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x01f1 }
            java.lang.Object[] r15 = new java.lang.Object[r13]     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.helper.utils.VLog.m91w(r10, r11, r15)     // Catch:{ all -> 0x01f1 }
        L_0x0129:
            com.lody.virtual.helper.utils.FileUtils.copyFile(r6, r2)     // Catch:{ IOException -> 0x012d }
            goto L_0x0139
        L_0x012d:
            r2.delete()     // Catch:{ all -> 0x01f1 }
            java.lang.String r0 = "Unable to copy the package file."
            com.lody.virtual.remote.InstallResult r0 = com.lody.virtual.remote.InstallResult.makeFailure(r0)     // Catch:{ all -> 0x01f1 }
            monitor-exit(r16)
            return r0
        L_0x0138:
            r2 = r6
        L_0x0139:
            if (r0 == 0) goto L_0x0140
            java.lang.String r0 = r8.packageName     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.server.p009pm.PackageCacheManager.remove(r0)     // Catch:{ all -> 0x01f1 }
        L_0x0140:
            r1.chmodPackageDictionary(r2)     // Catch:{ all -> 0x01f1 }
            if (r7 == 0) goto L_0x0146
            goto L_0x014b
        L_0x0146:
            com.lody.virtual.server.pm.PackageSetting r7 = new com.lody.virtual.server.pm.PackageSetting     // Catch:{ all -> 0x01f1 }
            r7.<init>()     // Catch:{ all -> 0x01f1 }
        L_0x014b:
            r7.dependSystem = r3     // Catch:{ all -> 0x01f1 }
            java.lang.String r0 = r2.getPath()     // Catch:{ all -> 0x01f1 }
            r7.apkPath = r0     // Catch:{ all -> 0x01f1 }
            java.lang.String r0 = r12.getPath()     // Catch:{ all -> 0x01f1 }
            r7.libPath = r0     // Catch:{ all -> 0x01f1 }
            java.lang.String r0 = r8.packageName     // Catch:{ all -> 0x01f1 }
            r7.packageName = r0     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.server.am.UidSystem r0 = r1.mUidSystem     // Catch:{ all -> 0x01f1 }
            int r0 = r0.getOrCreateUid(r8)     // Catch:{ all -> 0x01f1 }
            int r0 = com.lody.virtual.p007os.VUserHandle.getAppId(r0)     // Catch:{ all -> 0x01f1 }
            r7.appId = r0     // Catch:{ all -> 0x01f1 }
            boolean r0 = r9.isUpdate     // Catch:{ all -> 0x01f1 }
            if (r0 == 0) goto L_0x0170
            r7.lastUpdateTime = r4     // Catch:{ all -> 0x01f1 }
            goto L_0x018d
        L_0x0170:
            r7.firstInstallTime = r4     // Catch:{ all -> 0x01f1 }
            r7.lastUpdateTime = r4     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.server.pm.VUserManagerService r0 = com.lody.virtual.server.p009pm.VUserManagerService.get()     // Catch:{ all -> 0x01f1 }
            int[] r0 = r0.getUserIds()     // Catch:{ all -> 0x01f1 }
            int r2 = r0.length     // Catch:{ all -> 0x01f1 }
            r4 = 0
        L_0x017e:
            if (r4 >= r2) goto L_0x018d
            r5 = r0[r4]     // Catch:{ all -> 0x01f1 }
            if (r5 != 0) goto L_0x0186
            r6 = 1
            goto L_0x0187
        L_0x0186:
            r6 = 0
        L_0x0187:
            r7.setUserState(r5, r13, r13, r6)     // Catch:{ all -> 0x01f1 }
            int r4 = r4 + 1
            goto L_0x017e
        L_0x018d:
            com.lody.virtual.server.p009pm.parser.PackageParserEx.savePackageCache(r8)     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.server.p009pm.PackageCacheManager.put(r8, r7)     // Catch:{ all -> 0x01f1 }
            com.lody.virtual.server.pm.PackagePersistenceLayer r0 = r1.mPersistenceLayer     // Catch:{ all -> 0x01f1 }
            r0.save()     // Catch:{ all -> 0x01f1 }
            if (r3 != 0) goto L_0x01d0
            boolean r0 = com.lody.virtual.client.env.VirtualRuntime.isArt()     // Catch:{ all -> 0x01f1 }
            if (r0 == 0) goto L_0x01b5
            java.lang.String r0 = r7.apkPath     // Catch:{ IOException -> 0x01b1 }
            java.lang.String r2 = r7.packageName     // Catch:{ IOException -> 0x01b1 }
            java.io.File r2 = com.lody.virtual.p007os.VEnvironment.getOdexFile(r2)     // Catch:{ IOException -> 0x01b1 }
            java.lang.String r2 = r2.getPath()     // Catch:{ IOException -> 0x01b1 }
            com.lody.virtual.helper.ArtDexOptimizer.compileDex2Oat(r0, r2)     // Catch:{ IOException -> 0x01b1 }
            r0 = 0
            goto L_0x01b6
        L_0x01b1:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x01f1 }
        L_0x01b5:
            r0 = 1
        L_0x01b6:
            if (r0 == 0) goto L_0x01d0
            java.lang.String r0 = r7.apkPath     // Catch:{ IOException -> 0x01cc }
            java.lang.String r2 = r7.packageName     // Catch:{ IOException -> 0x01cc }
            java.io.File r2 = com.lody.virtual.p007os.VEnvironment.getOdexFile(r2)     // Catch:{ IOException -> 0x01cc }
            java.lang.String r2 = r2.getPath()     // Catch:{ IOException -> 0x01cc }
            dalvik.system.DexFile r0 = dalvik.system.DexFile.loadDex(r0, r2, r13)     // Catch:{ IOException -> 0x01cc }
            r0.close()     // Catch:{ IOException -> 0x01cc }
            goto L_0x01d0
        L_0x01cc:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x01f1 }
        L_0x01d0:
            com.lody.virtual.server.am.BroadcastSystem r0 = com.lody.virtual.server.p008am.BroadcastSystem.get()     // Catch:{ all -> 0x01f1 }
            r0.startApp(r8)     // Catch:{ all -> 0x01f1 }
            if (r19 == 0) goto L_0x01dc
            r1.notifyAppInstalled(r7, r14)     // Catch:{ all -> 0x01f1 }
        L_0x01dc:
            r2 = 1
            r9.isSuccess = r2     // Catch:{ all -> 0x01f1 }
            monitor-exit(r16)
            return r9
        L_0x01e1:
            java.lang.String r0 = "Unable to parse the package."
            com.lody.virtual.remote.InstallResult r0 = com.lody.virtual.remote.InstallResult.makeFailure(r0)     // Catch:{ all -> 0x01f1 }
            monitor-exit(r16)
            return r0
        L_0x01e9:
            java.lang.String r0 = "Package File is not exist."
            com.lody.virtual.remote.InstallResult r0 = com.lody.virtual.remote.InstallResult.makeFailure(r0)     // Catch:{ all -> 0x01f1 }
            monitor-exit(r16)
            return r0
        L_0x01f1:
            r0 = move-exception
            monitor-exit(r16)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VAppManagerService.installPackage(java.lang.String, int, boolean):com.lody.virtual.remote.InstallResult");
    }

    public synchronized boolean installPackageAsUser(int i, String str) {
        if (VUserManagerService.get().exists(i)) {
            PackageSetting setting = PackageCacheManager.getSetting(str);
            if (setting != null && !setting.isInstalled(i)) {
                setting.setInstalled(i, true);
                notifyAppInstalled(setting, i);
                this.mPersistenceLayer.save();
                return true;
            }
        }
        return false;
    }

    private void chmodPackageDictionary(File file) {
        try {
            if (VERSION.SDK_INT >= 21 && !FileUtils.isSymlink(file)) {
                FileUtils.chmod(file.getParentFile().getAbsolutePath(), FileMode.MODE_755);
                FileUtils.chmod(file.getAbsolutePath(), FileMode.MODE_755);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean canUpdate(VPackage vPackage, VPackage vPackage2, int i) {
        if ((i & 8) == 0 || vPackage.mVersionCode >= vPackage2.mVersionCode) {
            return (i & 2) == 0 && (i & 4) != 0;
        }
        return true;
    }

    public synchronized boolean uninstallPackage(String str) {
        PackageSetting setting = PackageCacheManager.getSetting(str);
        if (setting == null) {
            return false;
        }
        uninstallPackageFully(setting);
        return true;
    }

    public boolean clearPackageAsUser(int i, String str) throws RemoteException {
        if (!VUserManagerService.get().exists(i)) {
            return false;
        }
        PackageSetting setting = PackageCacheManager.getSetting(str);
        if (setting == null) {
            return false;
        }
        int[] packageInstalledUsers = getPackageInstalledUsers(str);
        if (!ArrayUtils.contains(packageInstalledUsers, i)) {
            return false;
        }
        if (packageInstalledUsers.length == 1) {
            clearPackage(str);
        } else {
            VActivityManagerService.get().killAppByPkg(str, i);
            setting.setInstalled(i, false);
            this.mPersistenceLayer.save();
            FileUtils.deleteDir(VEnvironment.getDataUserPackageDirectory(i, str));
            FileUtils.deleteDir(VEnvironment.getVirtualPrivateStorageDir(i, str));
        }
        return true;
    }

    public boolean clearPackage(String str) throws RemoteException {
        int[] userIds;
        try {
            BroadcastSystem.get().stopApp(str);
            VActivityManagerService.get().killAppByPkg(str, -1);
            for (int i : VUserManagerService.get().getUserIds()) {
                FileUtils.deleteDir(VEnvironment.getDataUserPackageDirectory(i, str));
                FileUtils.deleteDir(VEnvironment.getVirtualPrivateStorageDir(i, str));
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0049, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean uninstallPackageAsUser(java.lang.String r5, int r6) {
        /*
            r4 = this;
            monitor-enter(r4)
            com.lody.virtual.server.pm.VUserManagerService r0 = com.lody.virtual.server.p009pm.VUserManagerService.get()     // Catch:{ all -> 0x004c }
            boolean r0 = r0.exists(r6)     // Catch:{ all -> 0x004c }
            r1 = 0
            if (r0 != 0) goto L_0x000e
            monitor-exit(r4)
            return r1
        L_0x000e:
            com.lody.virtual.server.pm.PackageSetting r0 = com.lody.virtual.server.p009pm.PackageCacheManager.getSetting(r5)     // Catch:{ all -> 0x004c }
            if (r0 == 0) goto L_0x004a
            int[] r2 = r4.getPackageInstalledUsers(r5)     // Catch:{ all -> 0x004c }
            boolean r3 = com.lody.virtual.helper.utils.ArrayUtils.contains(r2, r6)     // Catch:{ all -> 0x004c }
            if (r3 != 0) goto L_0x0020
            monitor-exit(r4)
            return r1
        L_0x0020:
            int r2 = r2.length     // Catch:{ all -> 0x004c }
            r3 = 1
            if (r2 != r3) goto L_0x0028
            r4.uninstallPackageFully(r0)     // Catch:{ all -> 0x004c }
            goto L_0x0048
        L_0x0028:
            com.lody.virtual.server.am.VActivityManagerService r2 = com.lody.virtual.server.p008am.VActivityManagerService.get()     // Catch:{ all -> 0x004c }
            r2.killAppByPkg(r5, r6)     // Catch:{ all -> 0x004c }
            r0.setInstalled(r6, r1)     // Catch:{ all -> 0x004c }
            r4.notifyAppUninstalled(r0, r6)     // Catch:{ all -> 0x004c }
            com.lody.virtual.server.pm.PackagePersistenceLayer r0 = r4.mPersistenceLayer     // Catch:{ all -> 0x004c }
            r0.save()     // Catch:{ all -> 0x004c }
            java.io.File r0 = com.lody.virtual.p007os.VEnvironment.getDataUserPackageDirectory(r6, r5)     // Catch:{ all -> 0x004c }
            com.lody.virtual.helper.utils.FileUtils.deleteDir(r0)     // Catch:{ all -> 0x004c }
            java.io.File r5 = com.lody.virtual.p007os.VEnvironment.getVirtualPrivateStorageDir(r6, r5)     // Catch:{ all -> 0x004c }
            com.lody.virtual.helper.utils.FileUtils.deleteDir(r5)     // Catch:{ all -> 0x004c }
        L_0x0048:
            monitor-exit(r4)
            return r3
        L_0x004a:
            monitor-exit(r4)
            return r1
        L_0x004c:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VAppManagerService.uninstallPackageAsUser(java.lang.String, int):boolean");
    }

    private void uninstallPackageFully(PackageSetting packageSetting) {
        int[] userIds;
        String str = packageSetting.packageName;
        try {
            BroadcastSystem.get().stopApp(str);
            VActivityManagerService.get().killAppByPkg(str, -1);
            VEnvironment.getPackageResourcePath(str).delete();
            FileUtils.deleteDir(VEnvironment.getDataAppPackageDirectory(str));
            VEnvironment.getOdexFile(str).delete();
            for (int i : VUserManagerService.get().getUserIds()) {
                FileUtils.deleteDir(VEnvironment.getDataUserPackageDirectory(i, str));
                FileUtils.deleteDir(VEnvironment.getVirtualPrivateStorageDir(i, str));
            }
            PackageCacheManager.remove(str);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            notifyAppUninstalled(packageSetting, -1);
            throw th;
        }
        notifyAppUninstalled(packageSetting, -1);
    }

    public int[] getPackageInstalledUsers(String str) {
        int[] userIds;
        PackageSetting setting = PackageCacheManager.getSetting(str);
        if (setting == null) {
            return new int[0];
        }
        IntArray intArray = new IntArray(5);
        for (int i : VUserManagerService.get().getUserIds()) {
            if (setting.readUserState(i).installed) {
                intArray.add(i);
            }
        }
        return intArray.getAll();
    }

    public List<InstalledAppInfo> getInstalledApps(int i) {
        ArrayList arrayList = new ArrayList(getInstalledAppCount());
        for (VPackage vPackage : PackageCacheManager.PACKAGE_CACHE.values()) {
            arrayList.add(((PackageSetting) vPackage.mExtras).getAppInfo());
        }
        return arrayList;
    }

    public List<InstalledAppInfo> getInstalledAppsAsUser(int i, int i2) {
        ArrayList arrayList = new ArrayList(getInstalledAppCount());
        for (VPackage vPackage : PackageCacheManager.PACKAGE_CACHE.values()) {
            PackageSetting packageSetting = (PackageSetting) vPackage.mExtras;
            boolean isInstalled = packageSetting.isInstalled(i);
            if ((i2 & 1) == 0 && packageSetting.isHidden(i)) {
                isInstalled = false;
            }
            if (isInstalled) {
                arrayList.add(packageSetting.getAppInfo());
            }
        }
        return arrayList;
    }

    public int getInstalledAppCount() {
        return PackageCacheManager.PACKAGE_CACHE.size();
    }

    public boolean isAppInstalled(String str) {
        return str != null && PackageCacheManager.PACKAGE_CACHE.containsKey(str);
    }

    public boolean isAppInstalledAsUser(int i, String str) {
        if (str == null || !VUserManagerService.get().exists(i)) {
            return false;
        }
        PackageSetting setting = PackageCacheManager.getSetting(str);
        if (setting == null) {
            return false;
        }
        return setting.isInstalled(i);
    }

    private void notifyAppInstalled(PackageSetting packageSetting, int i) {
        String str = packageSetting.packageName;
        int beginBroadcast = this.mRemoteCallbackList.beginBroadcast();
        while (true) {
            int i2 = beginBroadcast - 1;
            if (beginBroadcast > 0) {
                if (i == -1) {
                    try {
                        sendInstalledBroadcast(str);
                        ((IPackageObserver) this.mRemoteCallbackList.getBroadcastItem(i2)).onPackageInstalled(str);
                        ((IPackageObserver) this.mRemoteCallbackList.getBroadcastItem(i2)).onPackageInstalledAsUser(0, str);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    ((IPackageObserver) this.mRemoteCallbackList.getBroadcastItem(i2)).onPackageInstalledAsUser(i, str);
                }
                beginBroadcast = i2;
            } else {
                this.mRemoteCallbackList.finishBroadcast();
                VAccountManagerService.get().refreshAuthenticatorCache(null);
                return;
            }
        }
    }

    private void notifyAppUninstalled(PackageSetting packageSetting, int i) {
        String str = packageSetting.packageName;
        int beginBroadcast = this.mRemoteCallbackList.beginBroadcast();
        while (true) {
            int i2 = beginBroadcast - 1;
            if (beginBroadcast > 0) {
                if (i == -1) {
                    try {
                        sendUninstalledBroadcast(str);
                        ((IPackageObserver) this.mRemoteCallbackList.getBroadcastItem(i2)).onPackageUninstalled(str);
                        ((IPackageObserver) this.mRemoteCallbackList.getBroadcastItem(i2)).onPackageUninstalledAsUser(0, str);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    ((IPackageObserver) this.mRemoteCallbackList.getBroadcastItem(i2)).onPackageUninstalledAsUser(i, str);
                }
                beginBroadcast = i2;
            } else {
                this.mRemoteCallbackList.finishBroadcast();
                VAccountManagerService.get().refreshAuthenticatorCache(null);
                return;
            }
        }
    }

    private void sendInstalledBroadcast(String str) {
        Intent intent = new Intent("android.intent.action.PACKAGE_ADDED");
        StringBuilder sb = new StringBuilder();
        sb.append("package:");
        sb.append(str);
        intent.setData(Uri.parse(sb.toString()));
        VActivityManagerService.get().sendBroadcastAsUser(intent, VUserHandle.ALL);
    }

    private void sendUninstalledBroadcast(String str) {
        Intent intent = new Intent("android.intent.action.PACKAGE_REMOVED");
        StringBuilder sb = new StringBuilder();
        sb.append("package:");
        sb.append(str);
        intent.setData(Uri.parse(sb.toString()));
        VActivityManagerService.get().sendBroadcastAsUser(intent, VUserHandle.ALL);
    }

    public void registerObserver(IPackageObserver iPackageObserver) {
        try {
            this.mRemoteCallbackList.register(iPackageObserver);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void unregisterObserver(IPackageObserver iPackageObserver) {
        try {
            this.mRemoteCallbackList.unregister(iPackageObserver);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public IAppRequestListener getAppRequestListener() {
        return this.mAppRequestListener;
    }

    public void setAppRequestListener(final IAppRequestListener iAppRequestListener) {
        this.mAppRequestListener = iAppRequestListener;
        if (iAppRequestListener != null) {
            try {
                iAppRequestListener.asBinder().linkToDeath(new DeathRecipient() {
                    public void binderDied() {
                        iAppRequestListener.asBinder().unlinkToDeath(this, 0);
                        VAppManagerService.this.mAppRequestListener = null;
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearAppRequestListener() {
        this.mAppRequestListener = null;
    }

    public InstalledAppInfo getInstalledAppInfo(String str, int i) {
        synchronized (PackageCacheManager.class) {
            if (str != null) {
                try {
                    PackageSetting setting = PackageCacheManager.getSetting(str);
                    if (setting != null) {
                        InstalledAppInfo appInfo = setting.getAppInfo();
                        return appInfo;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            return null;
        }
    }

    public boolean isPackageLaunched(int i, String str) {
        PackageSetting setting = PackageCacheManager.getSetting(str);
        return setting != null && setting.isLaunched(i);
    }

    public void setPackageHidden(int i, String str, boolean z) {
        PackageSetting setting = PackageCacheManager.getSetting(str);
        if (setting != null && VUserManagerService.get().exists(i)) {
            setting.setHidden(i, z);
            this.mPersistenceLayer.save();
        }
    }

    public int getAppId(String str) {
        PackageSetting setting = PackageCacheManager.getSetting(str);
        if (setting != null) {
            return setting.appId;
        }
        return -1;
    }

    /* access modifiers changed from: 0000 */
    public void restoreFactoryState() {
        VLog.m91w(TAG, "Warning: Restore the factory state...", new Object[0]);
        VEnvironment.getDalvikCacheDirectory().delete();
        VEnvironment.getUserSystemDirectory().delete();
        VEnvironment.getDataAppDirectory().delete();
    }

    public void savePersistenceData() {
        this.mPersistenceLayer.save();
    }
}
