package com.lody.virtual.client.ipc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.server.IPackageInstaller;
import com.lody.virtual.server.IPackageManager;
import com.lody.virtual.server.IPackageManager.Stub;
import java.util.List;

public class VPackageManager {
    private static final VPackageManager sMgr = new VPackageManager();
    private IPackageManager mRemote;

    public static VPackageManager get() {
        return sMgr;
    }

    public IPackageManager getInterface() {
        if (this.mRemote == null || (!this.mRemote.asBinder().pingBinder() && !VirtualCore.get().isVAppProcess())) {
            synchronized (VPackageManager.class) {
                this.mRemote = (IPackageManager) LocalProxyUtils.genProxy(IPackageManager.class, getRemoteInterface());
            }
        }
        return this.mRemote;
    }

    private Object getRemoteInterface() {
        return Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.PACKAGE));
    }

    public int checkPermission(String str, String str2, int i) {
        try {
            return getInterface().checkPermission(str, str2, i);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public ResolveInfo resolveService(Intent intent, String str, int i, int i2) {
        try {
            return getInterface().resolveService(intent, str, i, i2);
        } catch (RemoteException e) {
            return (ResolveInfo) VirtualRuntime.crash(e);
        }
    }

    public PermissionGroupInfo getPermissionGroupInfo(String str, int i) {
        try {
            return getInterface().getPermissionGroupInfo(str, i);
        } catch (RemoteException e) {
            return (PermissionGroupInfo) VirtualRuntime.crash(e);
        }
    }

    public List<ApplicationInfo> getInstalledApplications(int i, int i2) {
        try {
            return getInterface().getInstalledApplications(i, i2).getList();
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public PackageInfo getPackageInfo(String str, int i, int i2) {
        try {
            return getInterface().getPackageInfo(str, i, i2);
        } catch (RemoteException e) {
            return (PackageInfo) VirtualRuntime.crash(e);
        }
    }

    public ResolveInfo resolveIntent(Intent intent, String str, int i, int i2) {
        try {
            return getInterface().resolveIntent(intent, str, i, i2);
        } catch (RemoteException e) {
            return (ResolveInfo) VirtualRuntime.crash(e);
        }
    }

    public List<ResolveInfo> queryIntentContentProviders(Intent intent, String str, int i, int i2) {
        try {
            return getInterface().queryIntentContentProviders(intent, str, i, i2);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public ActivityInfo getReceiverInfo(ComponentName componentName, int i, int i2) {
        try {
            return getInterface().getReceiverInfo(componentName, i, i2);
        } catch (RemoteException e) {
            return (ActivityInfo) VirtualRuntime.crash(e);
        }
    }

    public List<PackageInfo> getInstalledPackages(int i, int i2) {
        try {
            return getInterface().getInstalledPackages(i, i2).getList();
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public List<PermissionInfo> queryPermissionsByGroup(String str, int i) {
        try {
            return getInterface().queryPermissionsByGroup(str, i);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public PermissionInfo getPermissionInfo(String str, int i) {
        try {
            return getInterface().getPermissionInfo(str, i);
        } catch (RemoteException e) {
            return (PermissionInfo) VirtualRuntime.crash(e);
        }
    }

    public ActivityInfo getActivityInfo(ComponentName componentName, int i, int i2) {
        try {
            return getInterface().getActivityInfo(componentName, i, i2);
        } catch (RemoteException e) {
            return (ActivityInfo) VirtualRuntime.crash(e);
        }
    }

    public List<ResolveInfo> queryIntentReceivers(Intent intent, String str, int i, int i2) {
        try {
            return getInterface().queryIntentReceivers(intent, str, i, i2);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public List<PermissionGroupInfo> getAllPermissionGroups(int i) {
        try {
            return getInterface().getAllPermissionGroups(i);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public List<ResolveInfo> queryIntentActivities(Intent intent, String str, int i, int i2) {
        try {
            return getInterface().queryIntentActivities(intent, str, i, i2);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public List<ResolveInfo> queryIntentServices(Intent intent, String str, int i, int i2) {
        try {
            return getInterface().queryIntentServices(intent, str, i, i2);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public ApplicationInfo getApplicationInfo(String str, int i, int i2) {
        String[] strArr;
        try {
            ApplicationInfo applicationInfo = getInterface().getApplicationInfo(str, i, i2);
            if (applicationInfo == null) {
                return null;
            }
            if (VERSION.SDK_INT >= 28 && applicationInfo.targetSdkVersion <= 28) {
                if (applicationInfo.sharedLibraryFiles == null) {
                    strArr = new String[]{"/system/framework/org.apache.http.legacy.boot.jar"};
                } else {
                    int length = applicationInfo.sharedLibraryFiles.length + 1;
                    String[] strArr2 = new String[length];
                    int i3 = length - 1;
                    System.arraycopy(applicationInfo.sharedLibraryFiles, 0, strArr2, 0, i3);
                    strArr2[i3] = "/system/framework/org.apache.http.legacy.boot.jar";
                    strArr = strArr2;
                }
                applicationInfo.sharedLibraryFiles = strArr;
            }
            return applicationInfo;
        } catch (RemoteException e) {
            return (ApplicationInfo) VirtualRuntime.crash(e);
        }
    }

    public ProviderInfo resolveContentProvider(String str, int i, int i2) {
        try {
            return getInterface().resolveContentProvider(str, i, i2);
        } catch (RemoteException e) {
            return (ProviderInfo) VirtualRuntime.crash(e);
        }
    }

    public ServiceInfo getServiceInfo(ComponentName componentName, int i, int i2) {
        try {
            return getInterface().getServiceInfo(componentName, i, i2);
        } catch (RemoteException e) {
            return (ServiceInfo) VirtualRuntime.crash(e);
        }
    }

    public ProviderInfo getProviderInfo(ComponentName componentName, int i, int i2) {
        try {
            return getInterface().getProviderInfo(componentName, i, i2);
        } catch (RemoteException e) {
            return (ProviderInfo) VirtualRuntime.crash(e);
        }
    }

    public boolean activitySupportsIntent(ComponentName componentName, Intent intent, String str) {
        try {
            return getInterface().activitySupportsIntent(componentName, intent, str);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public List<ProviderInfo> queryContentProviders(String str, int i, int i2) {
        try {
            return getInterface().queryContentProviders(str, i, i2).getList();
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public List<String> querySharedPackages(String str) {
        try {
            return getInterface().querySharedPackages(str);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public String[] getPackagesForUid(int i) {
        try {
            return getInterface().getPackagesForUid(i);
        } catch (RemoteException e) {
            return (String[]) VirtualRuntime.crash(e);
        }
    }

    public int getPackageUid(String str, int i) {
        try {
            return getInterface().getPackageUid(str, i);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public String getNameForUid(int i) {
        try {
            return getInterface().getNameForUid(i);
        } catch (RemoteException e) {
            return (String) VirtualRuntime.crash(e);
        }
    }

    public IPackageInstaller getPackageInstaller() {
        try {
            return getInterface().getPackageInstaller();
        } catch (RemoteException e) {
            return (IPackageInstaller) VirtualRuntime.crash(e);
        }
    }
}
