package p013io.virtualapp.home.repo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.InstalledAppInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import p013io.virtualapp.XApp;
import p013io.virtualapp.abs.Callback;
import p013io.virtualapp.abs.p014ui.VUiKit;
import p013io.virtualapp.home.models.PackageAppData;

/* renamed from: io.virtualapp.home.repo.PackageAppDataStorage */
public class PackageAppDataStorage {
    private static final PackageAppDataStorage STORAGE = new PackageAppDataStorage();
    private final Map<String, PackageAppData> packageDataMap = new HashMap();

    public static PackageAppDataStorage get() {
        return STORAGE;
    }

    public PackageAppData acquire(String str) {
        PackageAppData packageAppData;
        synchronized (this.packageDataMap) {
            packageAppData = (PackageAppData) this.packageDataMap.get(str);
            if (packageAppData == null) {
                packageAppData = loadAppData(str);
            }
        }
        return packageAppData;
    }

    public void acquire(String str, Callback<PackageAppData> callback) {
        Promise when = VUiKit.defer().when((Callable) new Callable(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final Object call() {
                return PackageAppDataStorage.this.acquire(this.f$1);
            }
        });
        callback.getClass();
        when.done(new DoneCallback() {
            public final void onDone(Object obj) {
                Callback.this.callback((PackageAppData) obj);
            }
        });
    }

    private PackageAppData loadAppData(String str) {
        InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(str, 0);
        if (installedAppInfo == null) {
            return null;
        }
        PackageAppData packageAppData = new PackageAppData((Context) XApp.getApp(), installedAppInfo);
        synchronized (this.packageDataMap) {
            this.packageDataMap.put(str, packageAppData);
        }
        return packageAppData;
    }

    public PackageAppData acquire(ApplicationInfo applicationInfo) {
        PackageAppData packageAppData;
        synchronized (this.packageDataMap) {
            packageAppData = (PackageAppData) this.packageDataMap.get(applicationInfo.packageName);
            if (packageAppData == null) {
                packageAppData = loadAppData(applicationInfo);
            }
        }
        return packageAppData;
    }

    public void acquire(ApplicationInfo applicationInfo, Callback<PackageAppData> callback) {
        Promise when = VUiKit.defer().when((Callable) new Callable(applicationInfo) {
            private final /* synthetic */ ApplicationInfo f$1;

            {
                this.f$1 = r2;
            }

            public final Object call() {
                return PackageAppDataStorage.this.acquire(this.f$1);
            }
        });
        callback.getClass();
        when.done(new DoneCallback() {
            public final void onDone(Object obj) {
                Callback.this.callback((PackageAppData) obj);
            }
        });
    }

    private PackageAppData loadAppData(ApplicationInfo applicationInfo) {
        PackageAppData packageAppData = new PackageAppData((Context) XApp.getApp(), applicationInfo);
        synchronized (this.packageDataMap) {
            this.packageDataMap.put(applicationInfo.packageName, packageAppData);
        }
        return packageAppData;
    }
}
