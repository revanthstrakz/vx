package p013io.virtualapp.sys;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.widget.Toast;
import com.lody.virtual.GmsSupport;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.DeviceUtil;
import com.lody.virtual.p007os.VUserManager;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;
import io.va.exposed.R;
import java.io.IOException;
import java.util.ArrayList;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import p013io.virtualapp.VCommends;
import p013io.virtualapp.XApp;
import p013io.virtualapp.abs.p014ui.VUiKit;
import p013io.virtualapp.home.models.AppData;
import p013io.virtualapp.home.models.AppInfoLite;
import p013io.virtualapp.home.models.MultiplePackageAppData;
import p013io.virtualapp.home.models.PackageAppData;
import p013io.virtualapp.home.repo.PackageAppDataStorage;
import p013io.virtualapp.sys.Installd.UpdateListener;

/* renamed from: io.virtualapp.sys.Installd */
public class Installd {

    /* renamed from: io.virtualapp.sys.Installd$UpdateListener */
    public interface UpdateListener {
        void fail(String str);

        void update(AppData appData);
    }

    public static void addApp(AppInfoLite appInfoLite, UpdateListener updateListener) {
        AnonymousClass1AddResult r0 = new Object() {
            /* access modifiers changed from: private */
            public PackageAppData appData;
            /* access modifiers changed from: private */
            public boolean justEnableHidden;
            /* access modifiers changed from: private */
            public int userId;
        };
        VUiKit.defer().when((Runnable) new Runnable(r0, updateListener) {
            private final /* synthetic */ AnonymousClass1AddResult f$1;
            private final /* synthetic */ UpdateListener f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                Installd.lambda$addApp$102(AppInfoLite.this, this.f$1, this.f$2);
            }
        }).then((DoneCallback<D>) new DoneCallback(appInfoLite) {
            private final /* synthetic */ AppInfoLite f$1;

            {
                this.f$1 = r2;
            }

            public final void onDone(Object obj) {
                Installd.lambda$addApp$103(AnonymousClass1AddResult.this, this.f$1, (Void) obj);
            }
        }).done(new DoneCallback(updateListener, appInfoLite) {
            private final /* synthetic */ UpdateListener f$1;
            private final /* synthetic */ AppInfoLite f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onDone(Object obj) {
                Installd.lambda$addApp$104(AnonymousClass1AddResult.this, this.f$1, this.f$2, (Void) obj);
            }
        }).fail(new FailCallback() {
            public final void onFail(Object obj) {
                Installd.lambda$addApp$105(UpdateListener.this, (Throwable) obj);
            }
        });
    }

    static /* synthetic */ void lambda$addApp$102(AppInfoLite appInfoLite, AnonymousClass1AddResult r6, UpdateListener updateListener) {
        PackageInfo packageInfo;
        int i = 0;
        InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(appInfoLite.packageName, 0);
        r6.justEnableHidden = installedAppInfo != null;
        if (appInfoLite.disableMultiVersion) {
            r6.justEnableHidden = false;
        }
        if (r6.justEnableHidden) {
            int[] installedUsers = installedAppInfo.getInstalledUsers();
            int length = installedUsers.length;
            while (true) {
                if (i >= installedUsers.length) {
                    break;
                } else if (installedUsers[i] != i) {
                    length = i;
                    break;
                } else {
                    i++;
                }
            }
            r6.userId = length;
            if (VUserManager.get().getUserInfo(length) == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Space ");
                sb.append(length + 1);
                if (VUserManager.get().createUser(sb.toString(), 2) == null) {
                    throw new IllegalStateException();
                }
            }
            if (!VirtualCore.get().installPackageAsUser(length, appInfoLite.packageName)) {
                throw new IllegalStateException();
            }
            return;
        }
        try {
            packageInfo = XApp.getApp().getPackageManager().getPackageArchiveInfo(appInfoLite.path, 0);
            try {
                packageInfo.applicationInfo.sourceDir = appInfoLite.path;
                packageInfo.applicationInfo.publicSourceDir = appInfoLite.path;
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            packageInfo = null;
        }
        if (packageInfo != null) {
            PackageAppData acquire = PackageAppDataStorage.get().acquire(packageInfo.applicationInfo);
            r6.appData = acquire;
            acquire.isInstalling = true;
            acquire.isFirstOpen = false;
            if (updateListener != null) {
                updateListener.update(acquire);
            }
        }
        InstallResult addVirtualApp = addVirtualApp(appInfoLite);
        if (!addVirtualApp.isSuccess) {
            r6.appData;
            throw new IllegalStateException(addVirtualApp.error);
        }
    }

    static /* synthetic */ void lambda$addApp$103(AnonymousClass1AddResult r0, AppInfoLite appInfoLite, Void voidR) {
        if (r0.appData == null) {
            r0.appData = PackageAppDataStorage.get().acquire(appInfoLite.packageName);
        }
    }

    static /* synthetic */ void lambda$addApp$104(AnonymousClass1AddResult r3, UpdateListener updateListener, AppInfoLite appInfoLite, Void voidR) {
        if (!(r3.justEnableHidden && r3.userId != 0)) {
            PackageAppData access$200 = r3.appData;
            access$200.isInstalling = false;
            access$200.isLoading = true;
            if (updateListener != null) {
                updateListener.update(access$200);
            }
            handleOptApp(access$200, appInfoLite.packageName, true, updateListener);
            return;
        }
        MultiplePackageAppData multiplePackageAppData = new MultiplePackageAppData(r3.appData, r3.userId);
        multiplePackageAppData.isInstalling = false;
        multiplePackageAppData.isLoading = true;
        if (updateListener != null) {
            updateListener.update(multiplePackageAppData);
        }
        handleOptApp(multiplePackageAppData, appInfoLite.packageName, false, updateListener);
    }

    static /* synthetic */ void lambda$addApp$105(UpdateListener updateListener, Throwable th) {
        if (updateListener != null) {
            updateListener.fail(th.getMessage());
        }
    }

    private static void handleOptApp(AppData appData, String str, boolean z, UpdateListener updateListener) {
        VUiKit.defer().when((Runnable) new Runnable(z, str) {
            private final /* synthetic */ boolean f$0;
            private final /* synthetic */ String f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                Installd.lambda$handleOptApp$106(this.f$0, this.f$1);
            }
        }).done(new DoneCallback(updateListener) {
            private final /* synthetic */ UpdateListener f$1;

            {
                this.f$1 = r2;
            }

            public final void onDone(Object obj) {
                Installd.lambda$handleOptApp$107(AppData.this, this.f$1, (Void) obj);
            }
        });
    }

    static /* synthetic */ void lambda$handleOptApp$106(boolean z, String str) {
        long currentTimeMillis = System.currentTimeMillis();
        if (z) {
            try {
                VirtualCore.get().preOpt(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
        if (currentTimeMillis2 < 1500) {
            try {
                Thread.sleep(1500 - currentTimeMillis2);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

    static /* synthetic */ void lambda$handleOptApp$107(AppData appData, UpdateListener updateListener, Void voidR) {
        if (appData instanceof PackageAppData) {
            PackageAppData packageAppData = (PackageAppData) appData;
            packageAppData.isLoading = false;
            packageAppData.isFirstOpen = true;
        } else if (appData instanceof MultiplePackageAppData) {
            MultiplePackageAppData multiplePackageAppData = (MultiplePackageAppData) appData;
            multiplePackageAppData.isLoading = false;
            multiplePackageAppData.isFirstOpen = true;
        }
        if (updateListener != null) {
            updateListener.update(appData);
        }
    }

    public static InstallResult addVirtualApp(AppInfoLite appInfoLite) {
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

    private static ArrayList<AppInfoLite> getAppInfoLiteFromPath(Context context, String str) {
        PackageInfo packageInfo;
        if (context == null) {
            return null;
        }
        try {
            packageInfo = context.getPackageManager().getPackageArchiveInfo(str, 128);
            try {
                packageInfo.applicationInfo.sourceDir = str;
                packageInfo.applicationInfo.publicSourceDir = str;
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            packageInfo = null;
        }
        if (packageInfo == null || TextUtils.equals(VirtualCore.TAICHI_PACKAGE, packageInfo.packageName)) {
            return null;
        }
        if (VirtualCore.get().getHostPkg().equals(packageInfo.packageName)) {
            Toast.makeText(VirtualCore.get().getContext(), R.string.install_self_eggs, 0).show();
            return null;
        }
        AppInfoLite appInfoLite = new AppInfoLite(packageInfo.packageName, str, false, packageInfo.applicationInfo.metaData != null && packageInfo.applicationInfo.metaData.containsKey("xposedmodule"));
        ArrayList<AppInfoLite> arrayList = new ArrayList<>();
        arrayList.add(appInfoLite);
        return arrayList;
    }

    public static void handleRequestFromFile(Context context, String str) {
        ArrayList appInfoLiteFromPath = getAppInfoLiteFromPath(context, str);
        if (appInfoLiteFromPath != null) {
            startInstallerActivity(context, appInfoLiteFromPath);
        }
    }

    public static void startInstallerActivity(Context context, ArrayList<AppInfoLite> arrayList) {
        if (context != null) {
            Intent intent = new Intent(context, InstallerActivity.class);
            intent.putParcelableArrayListExtra(VCommends.EXTRA_APP_INFO_LIST, arrayList);
            intent.addFlags(268435456);
            context.startActivity(intent);
        }
    }

    public static void addGmsSupport() {
        ApplicationInfo applicationInfo;
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(GmsSupport.GOOGLE_APP);
        arrayList.addAll(GmsSupport.GOOGLE_SERVICE);
        VirtualCore virtualCore = VirtualCore.get();
        ArrayList arrayList2 = new ArrayList();
        for (String str : arrayList) {
            if (!virtualCore.isAppInstalledAsUser(0, str)) {
                try {
                    applicationInfo = VirtualCore.get().getUnHookPackageManager().getApplicationInfo(str, 0);
                } catch (NameNotFoundException unused) {
                    applicationInfo = null;
                }
                if (!(applicationInfo == null || applicationInfo.sourceDir == null)) {
                    arrayList2.add(new AppInfoLite(applicationInfo.packageName, applicationInfo.sourceDir, false, true));
                }
            }
        }
        startInstallerActivity(VirtualCore.get().getContext(), arrayList2);
    }
}
