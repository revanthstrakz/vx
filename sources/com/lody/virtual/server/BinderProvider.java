package com.lody.virtual.server;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.client.stub.DaemonService;
import com.lody.virtual.helper.compat.BundleCompat;
import com.lody.virtual.server.accounts.VAccountManagerService;
import com.lody.virtual.server.device.VDeviceManagerService;
import com.lody.virtual.server.interfaces.IServiceFetcher.Stub;
import com.lody.virtual.server.job.VJobSchedulerService;
import com.lody.virtual.server.location.VirtualLocationService;
import com.lody.virtual.server.notification.VNotificationManagerService;
import com.lody.virtual.server.p008am.BroadcastSystem;
import com.lody.virtual.server.p008am.VActivityManagerService;
import com.lody.virtual.server.p009pm.VAppManagerService;
import com.lody.virtual.server.p009pm.VPackageManagerService;
import com.lody.virtual.server.p009pm.VUserManagerService;
import com.lody.virtual.server.p010vs.VirtualStorageService;

public final class BinderProvider extends ContentProvider {
    private final ServiceFetcher mServiceFetcher = new ServiceFetcher();

    private class ServiceFetcher extends Stub {
        private ServiceFetcher() {
        }

        public IBinder getService(String str) throws RemoteException {
            if (str != null) {
                return ServiceCache.getService(str);
            }
            return null;
        }

        public void addService(String str, IBinder iBinder) throws RemoteException {
            if (str != null && iBinder != null) {
                ServiceCache.addService(str, iBinder);
            }
        }

        public void removeService(String str) throws RemoteException {
            if (str != null) {
                ServiceCache.removeService(str);
            }
        }
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public boolean onCreate() {
        Context context = getContext();
        DaemonService.startup(context);
        if (!VirtualCore.get().isStartup()) {
            return true;
        }
        VPackageManagerService.systemReady();
        addService(ServiceManagerNative.PACKAGE, VPackageManagerService.get());
        VActivityManagerService.systemReady(context);
        addService(ServiceManagerNative.ACTIVITY, VActivityManagerService.get());
        addService(ServiceManagerNative.USER, VUserManagerService.get());
        VAppManagerService.systemReady();
        addService(ServiceManagerNative.APP, VAppManagerService.get());
        BroadcastSystem.attach(VActivityManagerService.get(), VAppManagerService.get());
        if (VERSION.SDK_INT >= 21) {
            addService(ServiceManagerNative.JOB, VJobSchedulerService.get());
        }
        VNotificationManagerService.systemReady(context);
        addService(ServiceManagerNative.NOTIFICATION, VNotificationManagerService.get());
        VAppManagerService.get().scanApps();
        VAccountManagerService.systemReady();
        addService(ServiceManagerNative.ACCOUNT, VAccountManagerService.get());
        addService(ServiceManagerNative.f178VS, VirtualStorageService.get());
        addService(ServiceManagerNative.DEVICE, VDeviceManagerService.get());
        addService(ServiceManagerNative.VIRTUAL_LOC, VirtualLocationService.get());
        return true;
    }

    private void addService(String str, IBinder iBinder) {
        ServiceCache.addService(str, iBinder);
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        if (!"@".equals(str)) {
            return null;
        }
        Bundle bundle2 = new Bundle();
        BundleCompat.putBinder(bundle2, "_VA_|_binder_", this.mServiceFetcher);
        return bundle2;
    }
}
