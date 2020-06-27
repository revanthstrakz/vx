package p013io.virtualapp.home.repo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.android.launcher3.IconCache;
import com.lody.virtual.GmsSupport;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.DeviceUtil;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import org.jdeferred.Promise;
import p013io.virtualapp.abs.p014ui.VUiKit;
import p013io.virtualapp.home.models.AppData;
import p013io.virtualapp.home.models.AppInfo;
import p013io.virtualapp.home.models.AppInfoLite;
import p013io.virtualapp.home.models.MultiplePackageAppData;
import p013io.virtualapp.home.models.PackageAppData;

/* renamed from: io.virtualapp.home.repo.AppRepository */
public class AppRepository implements AppDataSource {
    private static final Collator COLLATOR = Collator.getInstance(Locale.CHINA);
    private static final int MAX_SCAN_DEPTH = 2;
    private static final List<String> SCAN_PATH_LIST = Arrays.asList(new String[]{IconCache.EMPTY_CLASS_NAME, "wandoujia/app", "tencent/tassistant/apk", "BaiduAsa9103056", "360Download", "pp/downloader", "pp/downloader/apk", "pp/downloader/silent/apk"});
    private Context mContext;

    public AppRepository(Context context) {
        this.mContext = context;
    }

    private static boolean isSystemApplication(PackageInfo packageInfo) {
        if ((packageInfo.applicationInfo.flags & 1) == 0 || GmsSupport.isGmsFamilyPackage(packageInfo.packageName)) {
            return false;
        }
        return true;
    }

    public Promise<List<AppData>, Throwable, Void> getVirtualApps() {
        return VUiKit.defer().when((Callable) new Callable() {
            public final Object call() {
                return AppRepository.lambda$getVirtualApps$78(AppRepository.this);
            }
        });
    }

    public static /* synthetic */ List lambda$getVirtualApps$78(AppRepository appRepository) throws Exception {
        int[] installedUsers;
        List<InstalledAppInfo> installedApps = VirtualCore.get().getInstalledApps(0);
        ArrayList arrayList = new ArrayList();
        for (InstalledAppInfo installedAppInfo : installedApps) {
            if (VirtualCore.get().isPackageLaunchable(installedAppInfo.packageName)) {
                PackageAppData packageAppData = new PackageAppData(appRepository.mContext, installedAppInfo);
                if (VirtualCore.get().isAppInstalledAsUser(0, installedAppInfo.packageName)) {
                    arrayList.add(packageAppData);
                }
                for (int i : installedAppInfo.getInstalledUsers()) {
                    if (i != 0) {
                        arrayList.add(new MultiplePackageAppData(packageAppData, i));
                    }
                }
            }
        }
        return arrayList;
    }

    public Promise<List<AppInfo>, Throwable, Void> getInstalledApps(Context context) {
        return VUiKit.defer().when((Callable) new Callable(context) {
            private final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final Object call() {
                return AppRepository.this.convertPackageInfoToAppData(this.f$1, this.f$1.getPackageManager().getInstalledPackages(128), true);
            }
        });
    }

    public Promise<List<AppInfo>, Throwable, Void> getStorageApps(Context context, File file) {
        return VUiKit.defer().when((Callable) new Callable(context, file) {
            private final /* synthetic */ Context f$1;
            private final /* synthetic */ File f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final Object call() {
                return AppRepository.this.convertPackageInfoToAppData(this.f$1, AppRepository.this.findAndParseApkRecursively(this.f$1, this.f$2, null, 0), false);
            }
        });
    }

    private List<PackageInfo> findAndParseApkRecursively(Context context, File file, List<PackageInfo> list, int i) {
        PackageInfo packageInfo;
        if (list == null) {
            list = new ArrayList<>();
        }
        if (i > 2) {
            return list;
        }
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return Collections.emptyList();
        }
        for (File file2 : listFiles) {
            if (file2.isDirectory()) {
                list.addAll(findAndParseApkRecursively(context, file2, new ArrayList(), i + 1));
            }
            if (file2.isFile() && file2.getName().toLowerCase().endsWith(".apk")) {
                try {
                    packageInfo = context.getPackageManager().getPackageArchiveInfo(file2.getAbsolutePath(), 128);
                    try {
                        packageInfo.applicationInfo.sourceDir = file2.getAbsolutePath();
                        packageInfo.applicationInfo.publicSourceDir = file2.getAbsolutePath();
                    } catch (Exception unused) {
                    }
                } catch (Exception unused2) {
                    packageInfo = null;
                }
                if (packageInfo != null) {
                    list.add(packageInfo);
                }
            }
        }
        return list;
    }

    private List<PackageInfo> findAndParseAPKs(Context context, File file, List<String> list) {
        PackageInfo packageInfo;
        ArrayList arrayList = new ArrayList();
        if (list == null) {
            return arrayList;
        }
        for (String file2 : list) {
            File[] listFiles = new File(file, file2).listFiles();
            if (listFiles != null) {
                for (File file3 : listFiles) {
                    if (file3.getName().toLowerCase().endsWith(".apk")) {
                        try {
                            packageInfo = context.getPackageManager().getPackageArchiveInfo(file3.getAbsolutePath(), 0);
                            try {
                                packageInfo.applicationInfo.sourceDir = file3.getAbsolutePath();
                                packageInfo.applicationInfo.publicSourceDir = file3.getAbsolutePath();
                            } catch (Exception unused) {
                            }
                        } catch (Exception unused2) {
                            packageInfo = null;
                        }
                        if (packageInfo != null) {
                            arrayList.add(packageInfo);
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public List<AppInfo> convertPackageInfoToAppData(Context context, List<PackageInfo> list, boolean z) {
        PackageManager packageManager = context.getPackageManager();
        ArrayList arrayList = new ArrayList(list.size());
        String hostPkg = VirtualCore.get().getHostPkg();
        for (PackageInfo packageInfo : list) {
            if (!hostPkg.equals(packageInfo.packageName) && !VirtualCore.TAICHI_PACKAGE.equals(packageInfo.packageName) && !isSystemApplication(packageInfo)) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                String str = applicationInfo.publicSourceDir != null ? applicationInfo.publicSourceDir : applicationInfo.sourceDir;
                if (str != null) {
                    AppInfo appInfo = new AppInfo();
                    appInfo.packageName = packageInfo.packageName;
                    appInfo.fastOpen = z;
                    appInfo.path = str;
                    appInfo.icon = null;
                    appInfo.name = applicationInfo.loadLabel(packageManager);
                    appInfo.version = packageInfo.versionName;
                    InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(packageInfo.packageName, 0);
                    if (installedAppInfo != null) {
                        appInfo.cloneCount = installedAppInfo.getInstalledUsers().length;
                    }
                    if (applicationInfo.metaData != null && applicationInfo.metaData.containsKey("xposedmodule")) {
                        appInfo.disableMultiVersion = true;
                        appInfo.cloneCount = 0;
                    }
                    arrayList.add(appInfo);
                }
            }
        }
        Collections.sort(arrayList, $$Lambda$AppRepository$ABZKCAg8YdxMv9NeImuyk4usNY.INSTANCE);
        return arrayList;
    }

    public InstallResult addVirtualApp(AppInfoLite appInfoLite) {
        appInfoLite.fastOpen = false;
        if (DeviceUtil.isMeizuBelowN()) {
            appInfoLite.fastOpen = true;
        }
        int i = appInfoLite.fastOpen ? 104 : 72;
        if (appInfoLite.disableMultiVersion) {
            i |= 4;
        }
        return VirtualCore.get().installPackage(appInfoLite.path, i);
    }

    public boolean removeVirtualApp(String str, int i) {
        return VirtualCore.get().uninstallPackageAsUser(str, i);
    }
}
