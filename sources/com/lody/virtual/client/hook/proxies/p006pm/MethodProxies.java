package com.lody.virtual.client.hook.proxies.p006pm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.p000pm.IPackageDataObserver;
import android.content.p000pm.IPackageDeleteObserver2;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Binder;
import android.os.IInterface;
import android.os.Process;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.client.ipc.VPackageManager;
import com.lody.virtual.helper.collection.ArraySet;
import com.lody.virtual.helper.compat.ParceledListSliceCompat;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.EncodeUtils;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.server.IPackageInstaller;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import mirror.android.content.p016pm.ParceledListSlice;

/* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies */
class MethodProxies {

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$ActivitySupportsIntent */
    static class ActivitySupportsIntent extends MethodProxy {
        public String getMethodName() {
            return "activitySupportsIntent";
        }

        ActivitySupportsIntent() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Boolean.valueOf(VPackageManager.get().activitySupportsIntent(objArr[0], objArr[1], objArr[2]));
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$AddPackageToPreferred */
    static class AddPackageToPreferred extends MethodProxy {
        public String getMethodName() {
            return "addPackageToPreferred";
        }

        AddPackageToPreferred() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Integer.valueOf(0);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$CheckPermission */
    static class CheckPermission extends MethodProxy {
        public String getMethodName() {
            return "checkPermission";
        }

        CheckPermission() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Integer.valueOf(VPackageManager.get().checkPermission(objArr[0], objArr[1], VUserHandle.myUserId()));
        }

        public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
            return super.afterCall(obj, method, objArr, obj2);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    @SuppressLint({"PackageManagerGetSignatures"})
    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$CheckSignatures */
    static class CheckSignatures extends MethodProxy {
        public String getMethodName() {
            return "checkSignatures";
        }

        CheckSignatures() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (objArr.length == 2 && (objArr[0] instanceof String) && (objArr[1] instanceof String)) {
                PackageManager pm = VirtualCore.getPM();
                String str = objArr[0];
                String str2 = objArr[1];
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(str, 64);
                    PackageInfo packageInfo2 = pm.getPackageInfo(str2, 64);
                    Signature[] signatureArr = packageInfo.signatures;
                    Signature[] signatureArr2 = packageInfo2.signatures;
                    if (ArrayUtils.isEmpty(signatureArr)) {
                        if (!ArrayUtils.isEmpty(signatureArr2)) {
                            return Integer.valueOf(-1);
                        }
                        return Integer.valueOf(1);
                    } else if (ArrayUtils.isEmpty(signatureArr2)) {
                        return Integer.valueOf(-2);
                    } else {
                        if (Arrays.equals(signatureArr, signatureArr2)) {
                            return Integer.valueOf(0);
                        }
                        return Integer.valueOf(-3);
                    }
                } catch (Throwable unused) {
                }
            }
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$ClearPackagePersistentPreferredActivities */
    static class ClearPackagePersistentPreferredActivities extends MethodProxy {
        public String getMethodName() {
            return "clearPackagePersistentPreferredActivities";
        }

        ClearPackagePersistentPreferredActivities() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$ClearPackagePreferredActivities */
    static class ClearPackagePreferredActivities extends MethodProxy {
        public String getMethodName() {
            return "clearPackagePreferredActivities";
        }

        ClearPackagePreferredActivities() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$DeleteApplicationCacheFiles */
    static class DeleteApplicationCacheFiles extends MethodProxy {
        public String getMethodName() {
            return "deleteApplicationCacheFiles";
        }

        DeleteApplicationCacheFiles() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$DeletePackage */
    static class DeletePackage extends MethodProxy {
        public String getMethodName() {
            return "deletePackage";
        }

        DeletePackage() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = objArr[0];
            try {
                VirtualCore.get().uninstallPackage(str);
                IPackageDeleteObserver2 iPackageDeleteObserver2 = objArr[1];
                if (iPackageDeleteObserver2 != null) {
                    iPackageDeleteObserver2.onPackageDeleted(str, 0, "done.");
                }
            } catch (Throwable unused) {
            }
            return Integer.valueOf(0);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$FreeStorageAndNotify */
    static class FreeStorageAndNotify extends MethodProxy {
        public String getMethodName() {
            return "freeStorageAndNotify";
        }

        FreeStorageAndNotify() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (objArr[objArr.length - 1] instanceof IPackageDataObserver) {
                objArr[objArr.length - 1].onRemoveCompleted(null, true);
            }
            return Integer.valueOf(0);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetActivityInfo */
    static class GetActivityInfo extends MethodProxy {
        public String getMethodName() {
            return "getActivityInfo";
        }

        GetActivityInfo() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            ComponentName componentName = objArr[0];
            if (getHostPkg().equals(componentName.getPackageName())) {
                return method.invoke(obj, objArr);
            }
            int myUserId = VUserHandle.myUserId();
            ActivityInfo activityInfo = VPackageManager.get().getActivityInfo(componentName, objArr[1].intValue(), myUserId);
            if (activityInfo == null) {
                activityInfo = (ActivityInfo) method.invoke(obj, objArr);
                if (activityInfo == null || !isVisiblePackage(activityInfo.applicationInfo)) {
                    return null;
                }
            }
            return activityInfo;
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetApplicationBlockedSettingAsUser */
    static class GetApplicationBlockedSettingAsUser extends MethodProxy {
        public String getMethodName() {
            return "getApplicationBlockedSettingAsUser";
        }

        GetApplicationBlockedSettingAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetApplicationEnabledSetting */
    static class GetApplicationEnabledSetting extends MethodProxy {
        public String getMethodName() {
            return "getApplicationEnabledSetting";
        }

        GetApplicationEnabledSetting() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetApplicationInfo */
    static class GetApplicationInfo extends MethodProxy {
        public String getMethodName() {
            return "getApplicationInfo";
        }

        GetApplicationInfo() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = objArr[0];
            int intValue = objArr[1].intValue();
            if (getHostPkg().equals(str)) {
                return method.invoke(obj, objArr);
            }
            ApplicationInfo applicationInfo = VPackageManager.get().getApplicationInfo(str, intValue, VUserHandle.myUserId());
            if (applicationInfo != null) {
                return applicationInfo;
            }
            ApplicationInfo applicationInfo2 = (ApplicationInfo) method.invoke(obj, objArr);
            if (applicationInfo2 == null || !isVisiblePackage(applicationInfo2)) {
                return null;
            }
            return applicationInfo2;
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetComponentEnabledSetting */
    static class GetComponentEnabledSetting extends MethodProxy {
        public String getMethodName() {
            return "getComponentEnabledSetting";
        }

        GetComponentEnabledSetting() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (objArr[0] != null) {
                return Integer.valueOf(1);
            }
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetInstalledApplications */
    static class GetInstalledApplications extends MethodProxy {
        public String getMethodName() {
            return "getInstalledApplications";
        }

        GetInstalledApplications() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            List installedApplications = VPackageManager.get().getInstalledApplications(objArr[0].intValue(), VUserHandle.myUserId());
            return ParceledListSliceCompat.isReturnParceledListSlice(method) ? ParceledListSliceCompat.create(installedApplications) : installedApplications;
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetInstalledPackages */
    static class GetInstalledPackages extends MethodProxy {
        public String getMethodName() {
            return "getInstalledPackages";
        }

        GetInstalledPackages() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            List list;
            int intValue = objArr[0].intValue();
            int myUserId = VUserHandle.myUserId();
            if (isAppProcess()) {
                list = new ArrayList(VirtualCore.get().getInstalledAppCount());
            } else {
                list = VirtualCore.get().getUnHookPackageManager().getInstalledPackages(intValue);
            }
            list.addAll(VPackageManager.get().getInstalledPackages(intValue, myUserId));
            return ParceledListSliceCompat.isReturnParceledListSlice(method) ? ParceledListSliceCompat.create(list) : list;
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetInstallerPackageName */
    static class GetInstallerPackageName extends MethodProxy {
        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return "com.android.vending";
        }

        public String getMethodName() {
            return "getInstallerPackageName";
        }

        GetInstallerPackageName() {
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPackageGids */
    static class GetPackageGids extends MethodProxy {
        public String getMethodName() {
            return "getPackageGids";
        }

        GetPackageGids() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPackageGidsEtc */
    static class GetPackageGidsEtc extends GetPackageGids {
        GetPackageGidsEtc() {
        }

        public String getMethodName() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.getMethodName());
            sb.append("Etc");
            return sb.toString();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPackageInfo */
    static final class GetPackageInfo extends MethodProxy {
        public String getMethodName() {
            return "getPackageInfo";
        }

        GetPackageInfo() {
        }

        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            return (objArr == null || objArr[0] == null) ? false : true;
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            PackageInfo packageInfo = VPackageManager.get().getPackageInfo(objArr[0], objArr[1].intValue(), VUserHandle.myUserId());
            if (packageInfo != null) {
                return packageInfo;
            }
            PackageInfo packageInfo2 = (PackageInfo) method.invoke(obj, objArr);
            if (packageInfo2 == null || !isVisiblePackage(packageInfo2.applicationInfo)) {
                return null;
            }
            return packageInfo2;
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPackageInstaller */
    static class GetPackageInstaller extends MethodProxy {
        public String getMethodName() {
            return "getPackageInstaller";
        }

        GetPackageInstaller() {
        }

        public boolean isEnable() {
            return isAppProcess();
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            IInterface iInterface = (IInterface) method.invoke(obj, objArr);
            final IPackageInstaller packageInstaller = VPackageManager.get().getPackageInstaller();
            return Proxy.newProxyInstance(iInterface.getClass().getClassLoader(), iInterface.getClass().getInterfaces(), new InvocationHandler() {
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public java.lang.Object invoke(java.lang.Object r4, java.lang.reflect.Method r5, java.lang.Object[] r6) throws java.lang.Throwable {
                    /*
                        r3 = this;
                        java.lang.String r4 = r5.getName()
                        int r0 = r4.hashCode()
                        r1 = 1
                        r2 = 0
                        switch(r0) {
                            case -663066834: goto L_0x0077;
                            case -652885011: goto L_0x006d;
                            case -403218424: goto L_0x0062;
                            case -93516191: goto L_0x0058;
                            case -63461894: goto L_0x004e;
                            case 938656808: goto L_0x0044;
                            case 1170196863: goto L_0x0039;
                            case 1238099456: goto L_0x002f;
                            case 1568181855: goto L_0x0025;
                            case 1738611873: goto L_0x001a;
                            case 1788161260: goto L_0x000f;
                            default: goto L_0x000d;
                        }
                    L_0x000d:
                        goto L_0x0081
                    L_0x000f:
                        java.lang.String r0 = "openSession"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 4
                        goto L_0x0082
                    L_0x001a:
                        java.lang.String r0 = "unregisterCallback"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 9
                        goto L_0x0082
                    L_0x0025:
                        java.lang.String r0 = "getMySessions"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 7
                        goto L_0x0082
                    L_0x002f:
                        java.lang.String r0 = "updateSessionAppLabel"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 2
                        goto L_0x0082
                    L_0x0039:
                        java.lang.String r0 = "setPermissionsResult"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 10
                        goto L_0x0082
                    L_0x0044:
                        java.lang.String r0 = "getAllSessions"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 6
                        goto L_0x0082
                    L_0x004e:
                        java.lang.String r0 = "createSession"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 0
                        goto L_0x0082
                    L_0x0058:
                        java.lang.String r0 = "abandonSession"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 3
                        goto L_0x0082
                    L_0x0062:
                        java.lang.String r0 = "registerCallback"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 8
                        goto L_0x0082
                    L_0x006d:
                        java.lang.String r0 = "updateSessionAppIcon"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 1
                        goto L_0x0082
                    L_0x0077:
                        java.lang.String r0 = "getSessionInfo"
                        boolean r4 = r4.equals(r0)
                        if (r4 == 0) goto L_0x0081
                        r4 = 5
                        goto L_0x0082
                    L_0x0081:
                        r4 = -1
                    L_0x0082:
                        switch(r4) {
                            case 0: goto L_0x0170;
                            case 1: goto L_0x015a;
                            case 2: goto L_0x0144;
                            case 3: goto L_0x0132;
                            case 4: goto L_0x0123;
                            case 5: goto L_0x010c;
                            case 6: goto L_0x00f5;
                            case 7: goto L_0x00da;
                            case 8: goto L_0x00c8;
                            case 9: goto L_0x00ba;
                            case 10: goto L_0x00a0;
                            default: goto L_0x0085;
                        }
                    L_0x0085:
                        java.lang.RuntimeException r4 = new java.lang.RuntimeException
                        java.lang.StringBuilder r6 = new java.lang.StringBuilder
                        r6.<init>()
                        java.lang.String r0 = "Not support PackageInstaller method : "
                        r6.append(r0)
                        java.lang.String r5 = r5.getName()
                        r6.append(r5)
                        java.lang.String r5 = r6.toString()
                        r4.<init>(r5)
                        throw r4
                    L_0x00a0:
                        r4 = r6[r2]
                        java.lang.Integer r4 = (java.lang.Integer) r4
                        int r4 = r4.intValue()
                        r5 = r6[r1]
                        java.lang.Boolean r5 = (java.lang.Boolean) r5
                        boolean r5 = r5.booleanValue()
                        com.lody.virtual.server.IPackageInstaller r6 = r3
                        r6.setPermissionsResult(r4, r5)
                        java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
                        return r4
                    L_0x00ba:
                        r4 = r6[r2]
                        android.content.pm.IPackageInstallerCallback r4 = (android.content.p000pm.IPackageInstallerCallback) r4
                        com.lody.virtual.server.IPackageInstaller r5 = r3
                        r5.unregisterCallback(r4)
                        java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
                        return r4
                    L_0x00c8:
                        r4 = r6[r2]
                        android.content.pm.IPackageInstallerCallback r4 = (android.content.p000pm.IPackageInstallerCallback) r4
                        com.lody.virtual.server.IPackageInstaller r5 = r3
                        int r6 = com.lody.virtual.p007os.VUserHandle.myUserId()
                        r5.registerCallback(r4, r6)
                        java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
                        return r4
                    L_0x00da:
                        r4 = r6[r2]
                        java.lang.String r4 = (java.lang.String) r4
                        r5 = r6[r1]
                        java.lang.Integer r5 = (java.lang.Integer) r5
                        int r5 = r5.intValue()
                        com.lody.virtual.server.IPackageInstaller r6 = r3
                        com.lody.virtual.remote.VParceledListSlice r4 = r6.getMySessions(r4, r5)
                        java.util.List r4 = r4.getList()
                        java.lang.Object r4 = com.lody.virtual.helper.compat.ParceledListSliceCompat.create(r4)
                        return r4
                    L_0x00f5:
                        com.lody.virtual.server.IPackageInstaller r4 = r3
                        r5 = r6[r2]
                        java.lang.Integer r5 = (java.lang.Integer) r5
                        int r5 = r5.intValue()
                        com.lody.virtual.remote.VParceledListSlice r4 = r4.getAllSessions(r5)
                        java.util.List r4 = r4.getList()
                        java.lang.Object r4 = com.lody.virtual.helper.compat.ParceledListSliceCompat.create(r4)
                        return r4
                    L_0x010c:
                        com.lody.virtual.server.IPackageInstaller r4 = r3
                        r5 = r6[r2]
                        java.lang.Integer r5 = (java.lang.Integer) r5
                        int r5 = r5.intValue()
                        com.lody.virtual.server.pm.installer.SessionInfo r4 = r4.getSessionInfo(r5)
                        if (r4 == 0) goto L_0x0121
                        android.content.pm.PackageInstaller$SessionInfo r4 = r4.alloc()
                        return r4
                    L_0x0121:
                        r4 = 0
                        return r4
                    L_0x0123:
                        com.lody.virtual.server.IPackageInstaller r4 = r3
                        r5 = r6[r2]
                        java.lang.Integer r5 = (java.lang.Integer) r5
                        int r5 = r5.intValue()
                        android.content.pm.IPackageInstallerSession r4 = r4.openSession(r5)
                        return r4
                    L_0x0132:
                        com.lody.virtual.server.IPackageInstaller r4 = r3
                        r5 = r6[r2]
                        java.lang.Integer r5 = (java.lang.Integer) r5
                        int r5 = r5.intValue()
                        r4.abandonSession(r5)
                        java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
                        return r4
                    L_0x0144:
                        r4 = r6[r2]
                        java.lang.Integer r4 = (java.lang.Integer) r4
                        int r4 = r4.intValue()
                        r5 = r6[r1]
                        java.lang.String r5 = (java.lang.String) r5
                        com.lody.virtual.server.IPackageInstaller r6 = r3
                        r6.updateSessionAppLabel(r4, r5)
                        java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
                        return r4
                    L_0x015a:
                        r4 = r6[r2]
                        java.lang.Integer r4 = (java.lang.Integer) r4
                        int r4 = r4.intValue()
                        r5 = r6[r1]
                        android.graphics.Bitmap r5 = (android.graphics.Bitmap) r5
                        com.lody.virtual.server.IPackageInstaller r6 = r3
                        r6.updateSessionAppIcon(r4, r5)
                        java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
                        return r4
                    L_0x0170:
                        r4 = r6[r2]
                        android.content.pm.PackageInstaller$SessionParams r4 = (android.content.pm.PackageInstaller.SessionParams) r4
                        com.lody.virtual.server.pm.installer.SessionParams r4 = com.lody.virtual.server.p009pm.installer.SessionParams.create(r4)
                        r5 = r6[r1]
                        java.lang.String r5 = (java.lang.String) r5
                        com.lody.virtual.server.IPackageInstaller r6 = r3
                        int r0 = com.lody.virtual.p007os.VUserHandle.myUserId()
                        int r4 = r6.createSession(r4, r5, r0)
                        java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                        return r4
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.client.hook.proxies.p006pm.MethodProxies.GetPackageInstaller.C10041.invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[]):java.lang.Object");
                }
            });
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPackageUid */
    static class GetPackageUid extends MethodProxy {
        public String getMethodName() {
            return "getPackageUid";
        }

        GetPackageUid() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = objArr[0];
            if (str.equals(getHostPkg())) {
                return method.invoke(obj, objArr);
            }
            return Integer.valueOf(VUserHandle.getAppId(VPackageManager.get().getPackageUid(str, 0)));
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPackageUidEtc */
    static class GetPackageUidEtc extends GetPackageUid {
        GetPackageUidEtc() {
        }

        public String getMethodName() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.getMethodName());
            sb.append("Etc");
            return sb.toString();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPackagesForUid */
    static class GetPackagesForUid extends MethodProxy {
        public String getMethodName() {
            return "getPackagesForUid";
        }

        GetPackagesForUid() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            int intValue = objArr[0].intValue();
            int callingUid = Binder.getCallingUid();
            if (intValue == VirtualCore.get().myUid()) {
                intValue = getBaseVUid();
            }
            String[] packagesForUid = VPackageManager.get().getPackagesForUid(callingUid);
            String[] packagesForUid2 = VPackageManager.get().getPackagesForUid(intValue);
            String[] packagesForUid3 = VPackageManager.get().getPackagesForUid(Process.myUid());
            ArraySet arraySet = new ArraySet(2);
            if (packagesForUid != null && packagesForUid.length > 0) {
                arraySet.addAll(Arrays.asList(packagesForUid));
            }
            if (packagesForUid2 != null && packagesForUid2.length > 0) {
                arraySet.addAll(Arrays.asList(packagesForUid2));
            }
            if (packagesForUid3 != null && packagesForUid3.length > 0) {
                arraySet.addAll(Arrays.asList(packagesForUid3));
            }
            return arraySet.toArray(new String[arraySet.size()]);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    @TargetApi(17)
    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPermissionFlags */
    static class GetPermissionFlags extends MethodProxy {
        public String getMethodName() {
            return "getPermissionFlags";
        }

        GetPermissionFlags() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPermissionGroupInfo */
    static class GetPermissionGroupInfo extends MethodProxy {
        public String getMethodName() {
            return "getPermissionGroupInfo";
        }

        GetPermissionGroupInfo() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            PermissionGroupInfo permissionGroupInfo = VPackageManager.get().getPermissionGroupInfo(objArr[0], objArr[1].intValue());
            if (permissionGroupInfo != null) {
                return permissionGroupInfo;
            }
            return super.call(obj, method, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPermissions */
    static class GetPermissions extends MethodProxy {
        public String getMethodName() {
            return "getPermissions";
        }

        GetPermissions() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetPreferredActivities */
    static class GetPreferredActivities extends MethodProxy {
        public String getMethodName() {
            return "getPreferredActivities";
        }

        GetPreferredActivities() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(objArr);
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetProviderInfo */
    static class GetProviderInfo extends MethodProxy {
        public String getMethodName() {
            return "getProviderInfo";
        }

        GetProviderInfo() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            ComponentName componentName = objArr[0];
            int intValue = objArr[1].intValue();
            if (getHostPkg().equals(componentName.getPackageName())) {
                return method.invoke(obj, objArr);
            }
            ProviderInfo providerInfo = VPackageManager.get().getProviderInfo(componentName, intValue, VUserHandle.myUserId());
            if (providerInfo == null) {
                providerInfo = (ProviderInfo) method.invoke(obj, objArr);
                if (providerInfo == null || !isVisiblePackage(providerInfo.applicationInfo)) {
                    return null;
                }
            }
            return providerInfo;
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetReceiverInfo */
    static class GetReceiverInfo extends MethodProxy {
        public String getMethodName() {
            return "getReceiverInfo";
        }

        GetReceiverInfo() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            ComponentName componentName = objArr[0];
            if (getHostPkg().equals(componentName.getPackageName())) {
                return method.invoke(obj, objArr);
            }
            ActivityInfo receiverInfo = VPackageManager.get().getReceiverInfo(componentName, objArr[1].intValue(), 0);
            if (receiverInfo == null) {
                receiverInfo = (ActivityInfo) method.invoke(obj, objArr);
                if (receiverInfo == null || !isVisiblePackage(receiverInfo.applicationInfo)) {
                    return null;
                }
            }
            return receiverInfo;
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$GetServiceInfo */
    static class GetServiceInfo extends MethodProxy {
        public String getMethodName() {
            return "getServiceInfo";
        }

        GetServiceInfo() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            ServiceInfo serviceInfo = VPackageManager.get().getServiceInfo(objArr[0], objArr[1].intValue(), VUserHandle.myUserId());
            if (serviceInfo != null) {
                return serviceInfo;
            }
            ServiceInfo serviceInfo2 = (ServiceInfo) method.invoke(obj, objArr);
            if (serviceInfo2 == null || !isVisiblePackage(serviceInfo2.applicationInfo)) {
                return null;
            }
            return serviceInfo2;
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$IsPackageAvailable */
    static class IsPackageAvailable extends MethodProxy {
        public String getMethodName() {
            return "isPackageAvailable";
        }

        IsPackageAvailable() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isAppPkg(objArr[0])) {
                return Boolean.valueOf(true);
            }
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$IsPackageForzen */
    static class IsPackageForzen extends MethodProxy {
        public String getMethodName() {
            return "isPackageForzen";
        }

        IsPackageForzen() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Boolean.valueOf(false);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$QueryContentProviders */
    static class QueryContentProviders extends MethodProxy {
        public String getMethodName() {
            return "queryContentProviders";
        }

        QueryContentProviders() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            List queryContentProviders = VPackageManager.get().queryContentProviders(objArr[0], objArr[1].intValue(), objArr[2].intValue());
            return ParceledListSliceCompat.isReturnParceledListSlice(method) ? ParceledListSliceCompat.create(queryContentProviders) : queryContentProviders;
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$QueryIntentActivities */
    static class QueryIntentActivities extends MethodProxy {
        public String getMethodName() {
            return "queryIntentActivities";
        }

        QueryIntentActivities() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            boolean isReturnParceledListSlice = ParceledListSliceCompat.isReturnParceledListSlice(method);
            List queryIntentActivities = VPackageManager.get().queryIntentActivities(objArr[0], objArr[1], objArr[2].intValue(), VUserHandle.myUserId());
            Object invoke = method.invoke(obj, objArr);
            if (invoke != null) {
                if (isReturnParceledListSlice) {
                    invoke = ParceledListSlice.getList.call(invoke, new Object[0]);
                }
                List list = (List) invoke;
                if (list != null) {
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        ResolveInfo resolveInfo = (ResolveInfo) it.next();
                        if (resolveInfo == null || resolveInfo.activityInfo == null || !isVisiblePackage(resolveInfo.activityInfo.applicationInfo)) {
                            it.remove();
                        }
                    }
                    queryIntentActivities.addAll(list);
                }
            }
            return isReturnParceledListSlice ? ParceledListSliceCompat.create(queryIntentActivities) : queryIntentActivities;
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    @TargetApi(19)
    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$QueryIntentContentProviders */
    static class QueryIntentContentProviders extends MethodProxy {
        public String getMethodName() {
            return "queryIntentContentProviders";
        }

        QueryIntentContentProviders() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            boolean isReturnParceledListSlice = ParceledListSliceCompat.isReturnParceledListSlice(method);
            List queryIntentContentProviders = VPackageManager.get().queryIntentContentProviders(objArr[0], objArr[1], objArr[2].intValue(), VUserHandle.myUserId());
            Object invoke = method.invoke(obj, objArr);
            if (isReturnParceledListSlice) {
                invoke = ParceledListSlice.getList.call(invoke, new Object[0]);
            }
            List list = (List) invoke;
            if (list != null) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    ResolveInfo resolveInfo = (ResolveInfo) it.next();
                    if (resolveInfo == null || resolveInfo.providerInfo == null || !isVisiblePackage(resolveInfo.providerInfo.applicationInfo)) {
                        it.remove();
                    }
                }
                queryIntentContentProviders.addAll(list);
            }
            return ParceledListSliceCompat.isReturnParceledListSlice(method) ? ParceledListSliceCompat.create(queryIntentContentProviders) : queryIntentContentProviders;
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$QueryIntentReceivers */
    static class QueryIntentReceivers extends MethodProxy {
        public String getMethodName() {
            return "queryIntentReceivers";
        }

        QueryIntentReceivers() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            boolean isReturnParceledListSlice = ParceledListSliceCompat.isReturnParceledListSlice(method);
            List queryIntentReceivers = VPackageManager.get().queryIntentReceivers(objArr[0], objArr[1], objArr[2].intValue(), VUserHandle.myUserId());
            Object invoke = method.invoke(obj, objArr);
            if (isReturnParceledListSlice) {
                invoke = ParceledListSlice.getList.call(invoke, new Object[0]);
            }
            List list = (List) invoke;
            if (list != null) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    ResolveInfo resolveInfo = (ResolveInfo) it.next();
                    if (resolveInfo == null || resolveInfo.activityInfo == null || !isVisiblePackage(resolveInfo.activityInfo.applicationInfo)) {
                        it.remove();
                    }
                }
                queryIntentReceivers.addAll(list);
            }
            return isReturnParceledListSlice ? ParceledListSliceCompat.create(queryIntentReceivers) : queryIntentReceivers;
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$QueryIntentServices */
    static class QueryIntentServices extends MethodProxy {
        public String getMethodName() {
            return "queryIntentServices";
        }

        QueryIntentServices() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            boolean isReturnParceledListSlice = ParceledListSliceCompat.isReturnParceledListSlice(method);
            List queryIntentServices = VPackageManager.get().queryIntentServices(objArr[0], objArr[1], objArr[2].intValue(), VUserHandle.myUserId());
            Object invoke = method.invoke(obj, objArr);
            if (invoke != null) {
                if (isReturnParceledListSlice) {
                    invoke = ParceledListSlice.getList.call(invoke, new Object[0]);
                }
                List list = (List) invoke;
                if (list != null) {
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        ResolveInfo resolveInfo = (ResolveInfo) it.next();
                        if (resolveInfo == null || resolveInfo.serviceInfo == null || !isVisiblePackage(resolveInfo.serviceInfo.applicationInfo)) {
                            it.remove();
                        }
                    }
                    queryIntentServices.addAll(list);
                }
            }
            return isReturnParceledListSlice ? ParceledListSliceCompat.create(queryIntentServices) : queryIntentServices;
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$RemovePackageFromPreferred */
    static class RemovePackageFromPreferred extends MethodProxy {
        public String getMethodName() {
            return "removePackageFromPreferred";
        }

        RemovePackageFromPreferred() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$ResolveContentProvider */
    static class ResolveContentProvider extends MethodProxy {
        public String getMethodName() {
            return "resolveContentProvider";
        }

        ResolveContentProvider() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            ProviderInfo resolveContentProvider = VPackageManager.get().resolveContentProvider(objArr[0], objArr[1].intValue(), VUserHandle.myUserId());
            if (resolveContentProvider == null) {
                resolveContentProvider = (ProviderInfo) method.invoke(obj, objArr);
                return (resolveContentProvider == null || !isVisiblePackage(resolveContentProvider.applicationInfo)) ? resolveContentProvider : resolveContentProvider;
            }
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$ResolveIntent */
    static class ResolveIntent extends MethodProxy {
        public String getMethodName() {
            return "resolveIntent";
        }

        ResolveIntent() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            ResolveInfo resolveIntent = VPackageManager.get().resolveIntent(objArr[0], objArr[1], objArr[2].intValue(), VUserHandle.myUserId());
            return resolveIntent == null ? (ResolveInfo) method.invoke(obj, objArr) : resolveIntent;
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$ResolveService */
    static class ResolveService extends MethodProxy {
        public String getMethodName() {
            return "resolveService";
        }

        ResolveService() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            ResolveInfo resolveService = VPackageManager.get().resolveService(objArr[0], objArr[1], objArr[2].intValue(), VUserHandle.myUserId());
            return resolveService == null ? (ResolveInfo) method.invoke(obj, objArr) : resolveService;
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$RevokeRuntimePermission */
    static class RevokeRuntimePermission extends MethodProxy {
        public String getMethodName() {
            return "revokeRuntimePermission";
        }

        RevokeRuntimePermission() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$SetApplicationBlockedSettingAsUser */
    static class SetApplicationBlockedSettingAsUser extends MethodProxy {
        public String getMethodName() {
            return "setApplicationBlockedSettingAsUser";
        }

        SetApplicationBlockedSettingAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$SetApplicationEnabledSetting */
    static class SetApplicationEnabledSetting extends MethodProxy {
        public String getMethodName() {
            return "setApplicationEnabledSetting";
        }

        SetApplicationEnabledSetting() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$SetComponentEnabledSetting */
    static class SetComponentEnabledSetting extends MethodProxy {
        SetComponentEnabledSetting() {
        }

        public String getMethodName() {
            return EncodeUtils.decode("c2V0Q29tcG9uZW50RW5hYmxlZFNldHRpbmc=");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Integer.valueOf(0);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$SetPackageStoppedState */
    static class SetPackageStoppedState extends MethodProxy {
        public String getMethodName() {
            return "setPackageStoppedState";
        }

        SetPackageStoppedState() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$checkUidSignatures */
    static class checkUidSignatures extends MethodProxy {
        public String getMethodName() {
            return "checkUidSignatures";
        }

        checkUidSignatures() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            objArr[0].intValue();
            objArr[1].intValue();
            return Integer.valueOf(0);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.pm.MethodProxies$getNameForUid */
    static class getNameForUid extends MethodProxy {
        public String getMethodName() {
            return "getNameForUid";
        }

        getNameForUid() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return VPackageManager.get().getNameForUid(objArr[0].intValue());
        }
    }

    MethodProxies() {
    }
}
