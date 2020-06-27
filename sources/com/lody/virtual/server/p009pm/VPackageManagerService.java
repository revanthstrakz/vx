package com.lody.virtual.server.p009pm;

import android.annotation.TargetApi;
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
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.client.stub.VASettings;
import com.lody.virtual.helper.compat.ObjectsCompat;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.VParceledListSlice;
import com.lody.virtual.server.IPackageInstaller;
import com.lody.virtual.server.IPackageManager.Stub;
import com.lody.virtual.server.p009pm.installer.VPackageInstallerService;
import com.lody.virtual.server.p009pm.parser.PackageParserEx;
import com.lody.virtual.server.p009pm.parser.VPackage;
import com.lody.virtual.server.p009pm.parser.VPackage.ActivityComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.ActivityIntentInfo;
import com.lody.virtual.server.p009pm.parser.VPackage.PermissionComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.PermissionGroupComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.ProviderComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.ServiceComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.ServiceIntentInfo;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/* renamed from: com.lody.virtual.server.pm.VPackageManagerService */
public class VPackageManagerService extends Stub {
    static final String TAG = "PackageManager";
    private static final AtomicReference<VPackageManagerService> gService = new AtomicReference<>();
    private static final Comparator<ProviderInfo> sProviderInitOrderSorter = new Comparator<ProviderInfo>() {
        public int compare(ProviderInfo providerInfo, ProviderInfo providerInfo2) {
            int i = providerInfo.initOrder;
            int i2 = providerInfo2.initOrder;
            if (i > i2) {
                return -1;
            }
            return i < i2 ? 1 : 0;
        }
    };
    static final Comparator<ResolveInfo> sResolvePrioritySorter = new Comparator<ResolveInfo>() {
        public int compare(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
            int i = resolveInfo.priority;
            int i2 = resolveInfo2.priority;
            int i3 = 1;
            if (i != i2) {
                if (i > i2) {
                    i3 = -1;
                }
                return i3;
            }
            int i4 = resolveInfo.preferredOrder;
            int i5 = resolveInfo2.preferredOrder;
            if (i4 != i5) {
                if (i4 > i5) {
                    i3 = -1;
                }
                return i3;
            } else if (resolveInfo.isDefault != resolveInfo2.isDefault) {
                if (resolveInfo.isDefault) {
                    i3 = -1;
                }
                return i3;
            } else {
                int i6 = resolveInfo.match;
                int i7 = resolveInfo2.match;
                if (i6 == i7) {
                    return 0;
                }
                if (i6 > i7) {
                    i3 = -1;
                }
                return i3;
            }
        }
    };
    private final ActivityIntentResolver mActivities = new ActivityIntentResolver();
    private final Map<String, VPackage> mPackages;
    private final HashMap<String, PermissionGroupComponent> mPermissionGroups;
    private final HashMap<String, PermissionComponent> mPermissions;
    private final ProviderIntentResolver mProviders;
    private final HashMap<String, ProviderComponent> mProvidersByAuthority;
    private final HashMap<ComponentName, ProviderComponent> mProvidersByComponent;
    private final ActivityIntentResolver mReceivers = new ActivityIntentResolver();
    private final ResolveInfo mResolveInfo;
    private final ServiceIntentResolver mServices = new ServiceIntentResolver();

    /* renamed from: com.lody.virtual.server.pm.VPackageManagerService$ActivityIntentResolver */
    private final class ActivityIntentResolver extends IntentResolver<ActivityIntentInfo, ResolveInfo> {
        /* access modifiers changed from: private */
        public final HashMap<ComponentName, ActivityComponent> mActivities;
        private int mFlags;

        /* access modifiers changed from: protected */
        public void dumpFilter(PrintWriter printWriter, String str, ActivityIntentInfo activityIntentInfo) {
        }

        /* access modifiers changed from: protected */
        public void dumpFilterLabel(PrintWriter printWriter, String str, Object obj, int i) {
        }

        /* access modifiers changed from: protected */
        public boolean isFilterStopped(ActivityIntentInfo activityIntentInfo) {
            return false;
        }

        private ActivityIntentResolver() {
            this.mActivities = new HashMap<>();
        }

        public List<ResolveInfo> queryIntent(Intent intent, String str, boolean z, int i) {
            this.mFlags = z ? 65536 : 0;
            return super.queryIntent(intent, str, z, i);
        }

        /* access modifiers changed from: 0000 */
        public List<ResolveInfo> queryIntent(Intent intent, String str, int i, int i2) {
            this.mFlags = i;
            return super.queryIntent(intent, str, (i & 65536) != 0, i2);
        }

        /* access modifiers changed from: 0000 */
        public List<ResolveInfo> queryIntentForPackage(Intent intent, String str, int i, ArrayList<ActivityComponent> arrayList, int i2) {
            if (arrayList == null) {
                return null;
            }
            this.mFlags = i;
            boolean z = (i & 65536) != 0;
            int size = arrayList.size();
            ArrayList arrayList2 = new ArrayList(size);
            for (int i3 = 0; i3 < size; i3++) {
                ArrayList arrayList3 = ((ActivityComponent) arrayList.get(i3)).intents;
                if (arrayList3 != null && arrayList3.size() > 0) {
                    ActivityIntentInfo[] activityIntentInfoArr = new ActivityIntentInfo[arrayList3.size()];
                    arrayList3.toArray(activityIntentInfoArr);
                    arrayList2.add(activityIntentInfoArr);
                }
            }
            return super.queryIntentFromList(intent, str, z, arrayList2, i2);
        }

        public final void addActivity(ActivityComponent activityComponent, String str) {
            this.mActivities.put(activityComponent.getComponentName(), activityComponent);
            int size = activityComponent.intents.size();
            for (int i = 0; i < size; i++) {
                ActivityIntentInfo activityIntentInfo = (ActivityIntentInfo) activityComponent.intents.get(i);
                if (activityIntentInfo.filter.getPriority() > 0 && ServiceManagerNative.ACTIVITY.equals(str)) {
                    activityIntentInfo.filter.setPriority(0);
                    String str2 = VPackageManagerService.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Package ");
                    sb.append(activityComponent.info.applicationInfo.packageName);
                    sb.append(" has activity ");
                    sb.append(activityComponent.className);
                    sb.append(" with priority > 0, forcing to 0");
                    Log.w(str2, sb.toString());
                }
                addFilter(activityIntentInfo);
            }
        }

        public final void removeActivity(ActivityComponent activityComponent, String str) {
            this.mActivities.remove(activityComponent.getComponentName());
            int size = activityComponent.intents.size();
            for (int i = 0; i < size; i++) {
                removeFilter((ActivityIntentInfo) activityComponent.intents.get(i));
            }
        }

        /* access modifiers changed from: protected */
        public boolean allowFilterResult(ActivityIntentInfo activityIntentInfo, List<ResolveInfo> list) {
            ActivityInfo activityInfo = activityIntentInfo.activity.info;
            for (int size = list.size() - 1; size >= 0; size--) {
                ActivityInfo activityInfo2 = ((ResolveInfo) list.get(size)).activityInfo;
                if (ObjectsCompat.equals(activityInfo2.name, activityInfo.name) && ObjectsCompat.equals(activityInfo2.packageName, activityInfo.packageName)) {
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public ActivityIntentInfo[] newArray(int i) {
            return new ActivityIntentInfo[i];
        }

        /* access modifiers changed from: protected */
        public boolean isPackageForFilter(String str, ActivityIntentInfo activityIntentInfo) {
            return str.equals(activityIntentInfo.activity.owner.packageName);
        }

        /* access modifiers changed from: protected */
        public ResolveInfo newResult(ActivityIntentInfo activityIntentInfo, int i, int i2) {
            ActivityComponent activityComponent = activityIntentInfo.activity;
            ActivityInfo generateActivityInfo = PackageParserEx.generateActivityInfo(activityComponent, this.mFlags, ((PackageSetting) activityComponent.owner.mExtras).readUserState(i2), i2);
            if (generateActivityInfo == null) {
                return null;
            }
            ResolveInfo resolveInfo = new ResolveInfo();
            resolveInfo.activityInfo = generateActivityInfo;
            if ((this.mFlags & 64) != 0) {
                resolveInfo.filter = activityIntentInfo.filter;
            }
            resolveInfo.priority = activityIntentInfo.filter.getPriority();
            resolveInfo.preferredOrder = activityComponent.owner.mPreferredOrder;
            resolveInfo.match = i;
            resolveInfo.isDefault = activityIntentInfo.hasDefault;
            resolveInfo.labelRes = activityIntentInfo.labelRes;
            resolveInfo.nonLocalizedLabel = activityIntentInfo.nonLocalizedLabel;
            resolveInfo.icon = activityIntentInfo.icon;
            return resolveInfo;
        }

        /* access modifiers changed from: protected */
        public void sortResults(List<ResolveInfo> list) {
            Collections.sort(list, VPackageManagerService.sResolvePrioritySorter);
        }

        /* access modifiers changed from: protected */
        public Object filterToLabel(ActivityIntentInfo activityIntentInfo) {
            return activityIntentInfo.activity;
        }
    }

    /* renamed from: com.lody.virtual.server.pm.VPackageManagerService$ServiceIntentResolver */
    private final class ServiceIntentResolver extends IntentResolver<ServiceIntentInfo, ResolveInfo> {
        private int mFlags;
        /* access modifiers changed from: private */
        public final HashMap<ComponentName, ServiceComponent> mServices;

        /* access modifiers changed from: protected */
        public void dumpFilter(PrintWriter printWriter, String str, ServiceIntentInfo serviceIntentInfo) {
        }

        /* access modifiers changed from: protected */
        public void dumpFilterLabel(PrintWriter printWriter, String str, Object obj, int i) {
        }

        /* access modifiers changed from: protected */
        public boolean isFilterStopped(ServiceIntentInfo serviceIntentInfo) {
            return false;
        }

        private ServiceIntentResolver() {
            this.mServices = new HashMap<>();
        }

        public List<ResolveInfo> queryIntent(Intent intent, String str, boolean z, int i) {
            this.mFlags = z ? 65536 : 0;
            return super.queryIntent(intent, str, z, i);
        }

        public List<ResolveInfo> queryIntent(Intent intent, String str, int i, int i2) {
            this.mFlags = i;
            return super.queryIntent(intent, str, (i & 65536) != 0, i2);
        }

        public List<ResolveInfo> queryIntentForPackage(Intent intent, String str, int i, ArrayList<ServiceComponent> arrayList, int i2) {
            if (arrayList == null) {
                return null;
            }
            this.mFlags = i;
            boolean z = (i & 65536) != 0;
            int size = arrayList.size();
            ArrayList arrayList2 = new ArrayList(size);
            for (int i3 = 0; i3 < size; i3++) {
                ArrayList arrayList3 = ((ServiceComponent) arrayList.get(i3)).intents;
                if (arrayList3 != null && arrayList3.size() > 0) {
                    ServiceIntentInfo[] serviceIntentInfoArr = new ServiceIntentInfo[arrayList3.size()];
                    arrayList3.toArray(serviceIntentInfoArr);
                    arrayList2.add(serviceIntentInfoArr);
                }
            }
            return super.queryIntentFromList(intent, str, z, arrayList2, i2);
        }

        public final void addService(ServiceComponent serviceComponent) {
            this.mServices.put(serviceComponent.getComponentName(), serviceComponent);
            int size = serviceComponent.intents.size();
            for (int i = 0; i < size; i++) {
                addFilter((ServiceIntentInfo) serviceComponent.intents.get(i));
            }
        }

        public final void removeService(ServiceComponent serviceComponent) {
            this.mServices.remove(serviceComponent.getComponentName());
            int size = serviceComponent.intents.size();
            for (int i = 0; i < size; i++) {
                removeFilter((ServiceIntentInfo) serviceComponent.intents.get(i));
            }
        }

        /* access modifiers changed from: protected */
        public boolean allowFilterResult(ServiceIntentInfo serviceIntentInfo, List<ResolveInfo> list) {
            ServiceInfo serviceInfo = serviceIntentInfo.service.info;
            for (int size = list.size() - 1; size >= 0; size--) {
                ServiceInfo serviceInfo2 = ((ResolveInfo) list.get(size)).serviceInfo;
                if (ObjectsCompat.equals(serviceInfo2.name, serviceInfo.name) && ObjectsCompat.equals(serviceInfo2.packageName, serviceInfo.packageName)) {
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public ServiceIntentInfo[] newArray(int i) {
            return new ServiceIntentInfo[i];
        }

        /* access modifiers changed from: protected */
        public boolean isPackageForFilter(String str, ServiceIntentInfo serviceIntentInfo) {
            return str.equals(serviceIntentInfo.service.owner.packageName);
        }

        /* access modifiers changed from: protected */
        public ResolveInfo newResult(ServiceIntentInfo serviceIntentInfo, int i, int i2) {
            ServiceComponent serviceComponent = serviceIntentInfo.service;
            ServiceInfo generateServiceInfo = PackageParserEx.generateServiceInfo(serviceComponent, this.mFlags, ((PackageSetting) serviceComponent.owner.mExtras).readUserState(i2), i2);
            if (generateServiceInfo == null) {
                return null;
            }
            ResolveInfo resolveInfo = new ResolveInfo();
            resolveInfo.serviceInfo = generateServiceInfo;
            if ((this.mFlags & 64) != 0) {
                resolveInfo.filter = serviceIntentInfo.filter;
            }
            resolveInfo.priority = serviceIntentInfo.filter.getPriority();
            resolveInfo.preferredOrder = serviceComponent.owner.mPreferredOrder;
            resolveInfo.match = i;
            resolveInfo.isDefault = serviceIntentInfo.hasDefault;
            resolveInfo.labelRes = serviceIntentInfo.labelRes;
            resolveInfo.nonLocalizedLabel = serviceIntentInfo.nonLocalizedLabel;
            resolveInfo.icon = serviceIntentInfo.icon;
            return resolveInfo;
        }

        /* access modifiers changed from: protected */
        public void sortResults(List<ResolveInfo> list) {
            Collections.sort(list, VPackageManagerService.sResolvePrioritySorter);
        }

        /* access modifiers changed from: protected */
        public Object filterToLabel(ServiceIntentInfo serviceIntentInfo) {
            return serviceIntentInfo.service;
        }
    }

    private ResolveInfo findPreferredActivity(Intent intent, String str, int i, List<ResolveInfo> list, int i2) {
        return null;
    }

    public VPackageManagerService() {
        ProviderIntentResolver providerIntentResolver = null;
        if (VERSION.SDK_INT >= 19) {
            providerIntentResolver = new ProviderIntentResolver();
        }
        this.mProviders = providerIntentResolver;
        this.mProvidersByComponent = new HashMap<>();
        this.mPermissions = new HashMap<>();
        this.mPermissionGroups = new HashMap<>();
        this.mProvidersByAuthority = new HashMap<>();
        this.mPackages = PackageCacheManager.PACKAGE_CACHE;
        Intent intent = new Intent();
        intent.setClassName(VirtualCore.get().getHostPkg(), VASettings.RESOLVER_ACTIVITY);
        this.mResolveInfo = VirtualCore.get().getUnHookPackageManager().resolveActivity(intent, 0);
    }

    public static void systemReady() {
        VPackageManagerService vPackageManagerService = new VPackageManagerService();
        new VUserManagerService(VirtualCore.get().getContext(), vPackageManagerService, new char[0], vPackageManagerService.mPackages);
        gService.set(vPackageManagerService);
    }

    public static VPackageManagerService get() {
        return (VPackageManagerService) gService.get();
    }

    /* access modifiers changed from: 0000 */
    public void analyzePackageLocked(VPackage vPackage) {
        String[] split;
        int size = vPackage.activities.size();
        for (int i = 0; i < size; i++) {
            ActivityComponent activityComponent = (ActivityComponent) vPackage.activities.get(i);
            if (activityComponent.info.processName == null) {
                activityComponent.info.processName = activityComponent.info.packageName;
            }
            this.mActivities.addActivity(activityComponent, ServiceManagerNative.ACTIVITY);
        }
        int size2 = vPackage.services.size();
        for (int i2 = 0; i2 < size2; i2++) {
            ServiceComponent serviceComponent = (ServiceComponent) vPackage.services.get(i2);
            if (serviceComponent.info.processName == null) {
                serviceComponent.info.processName = serviceComponent.info.packageName;
            }
            this.mServices.addService(serviceComponent);
        }
        int size3 = vPackage.receivers.size();
        for (int i3 = 0; i3 < size3; i3++) {
            ActivityComponent activityComponent2 = (ActivityComponent) vPackage.receivers.get(i3);
            if (activityComponent2.info.processName == null) {
                activityComponent2.info.processName = activityComponent2.info.packageName;
            }
            this.mReceivers.addActivity(activityComponent2, "receiver");
        }
        int size4 = vPackage.providers.size();
        for (int i4 = 0; i4 < size4; i4++) {
            ProviderComponent providerComponent = (ProviderComponent) vPackage.providers.get(i4);
            if (providerComponent.info.processName == null) {
                providerComponent.info.processName = providerComponent.info.packageName;
            }
            if (VERSION.SDK_INT >= 19) {
                this.mProviders.addProvider(providerComponent);
            }
            for (String str : providerComponent.info.authority.split(";")) {
                if (!this.mProvidersByAuthority.containsKey(str)) {
                    this.mProvidersByAuthority.put(str, providerComponent);
                }
            }
            this.mProvidersByComponent.put(providerComponent.getComponentName(), providerComponent);
        }
        int size5 = vPackage.permissions.size();
        for (int i5 = 0; i5 < size5; i5++) {
            PermissionComponent permissionComponent = (PermissionComponent) vPackage.permissions.get(i5);
            this.mPermissions.put(permissionComponent.className, permissionComponent);
        }
        int size6 = vPackage.permissionGroups.size();
        for (int i6 = 0; i6 < size6; i6++) {
            PermissionGroupComponent permissionGroupComponent = (PermissionGroupComponent) vPackage.permissionGroups.get(i6);
            this.mPermissionGroups.put(permissionGroupComponent.className, permissionGroupComponent);
        }
    }

    /* access modifiers changed from: 0000 */
    public void deletePackageLocked(String str) {
        VPackage vPackage = (VPackage) this.mPackages.get(str);
        if (vPackage != null) {
            int size = vPackage.activities.size();
            for (int i = 0; i < size; i++) {
                this.mActivities.removeActivity((ActivityComponent) vPackage.activities.get(i), ServiceManagerNative.ACTIVITY);
            }
            int size2 = vPackage.services.size();
            for (int i2 = 0; i2 < size2; i2++) {
                this.mServices.removeService((ServiceComponent) vPackage.services.get(i2));
            }
            int size3 = vPackage.receivers.size();
            for (int i3 = 0; i3 < size3; i3++) {
                this.mReceivers.removeActivity((ActivityComponent) vPackage.receivers.get(i3), "receiver");
            }
            int size4 = vPackage.providers.size();
            for (int i4 = 0; i4 < size4; i4++) {
                ProviderComponent providerComponent = (ProviderComponent) vPackage.providers.get(i4);
                if (VERSION.SDK_INT >= 19) {
                    this.mProviders.removeProvider(providerComponent);
                }
                for (String remove : providerComponent.info.authority.split(";")) {
                    this.mProvidersByAuthority.remove(remove);
                }
                this.mProvidersByComponent.remove(providerComponent.getComponentName());
            }
            int size5 = vPackage.permissions.size();
            for (int i5 = 0; i5 < size5; i5++) {
                this.mPermissions.remove(((PermissionComponent) vPackage.permissions.get(i5)).className);
            }
            int size6 = vPackage.permissionGroups.size();
            for (int i6 = 0; i6 < size6; i6++) {
                this.mPermissionGroups.remove(((PermissionGroupComponent) vPackage.permissionGroups.get(i6)).className);
            }
        }
    }

    public List<String> getSharedLibraries(String str) {
        synchronized (this.mPackages) {
            VPackage vPackage = (VPackage) this.mPackages.get(str);
            if (vPackage == null) {
                return null;
            }
            ArrayList<String> arrayList = vPackage.usesLibraries;
            return arrayList;
        }
    }

    public int checkPermission(String str, String str2, int i) {
        if ("android.permission.INTERACT_ACROSS_USERS".equals(str) || "android.permission.INTERACT_ACROSS_USERS_FULL".equals(str)) {
            return -1;
        }
        return VirtualCore.get().getPackageManager().checkPermission(str, VirtualCore.get().getHostPkg());
    }

    public PackageInfo getPackageInfo(String str, int i, int i2) {
        checkUserId(i2);
        synchronized (this.mPackages) {
            VPackage vPackage = (VPackage) this.mPackages.get(str);
            if (vPackage == null) {
                return null;
            }
            PackageInfo generatePackageInfo = generatePackageInfo(vPackage, (PackageSetting) vPackage.mExtras, i, i2);
            return generatePackageInfo;
        }
    }

    private PackageInfo generatePackageInfo(VPackage vPackage, PackageSetting packageSetting, int i, int i2) {
        PackageInfo generatePackageInfo = PackageParserEx.generatePackageInfo(vPackage, updateFlagsNought(i), packageSetting.firstInstallTime, packageSetting.lastUpdateTime, packageSetting.readUserState(i2), i2);
        if (generatePackageInfo != null) {
            return generatePackageInfo;
        }
        return null;
    }

    private int updateFlagsNought(int i) {
        if (VERSION.SDK_INT < 24) {
            return i;
        }
        if ((i & 786432) == 0) {
            i |= 786432;
        }
        return i;
    }

    private void checkUserId(int i) {
        if (!VUserManagerService.get().exists(i)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid userId ");
            sb.append(i);
            throw new SecurityException(sb.toString());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0039, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.pm.ActivityInfo getActivityInfo(android.content.ComponentName r4, int r5, int r6) {
        /*
            r3 = this;
            r3.checkUserId(r6)
            int r5 = r3.updateFlagsNought(r5)
            java.util.Map<java.lang.String, com.lody.virtual.server.pm.parser.VPackage> r0 = r3.mPackages
            monitor-enter(r0)
            java.util.Map<java.lang.String, com.lody.virtual.server.pm.parser.VPackage> r1 = r3.mPackages     // Catch:{ all -> 0x003a }
            java.lang.String r2 = r4.getPackageName()     // Catch:{ all -> 0x003a }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.parser.VPackage r1 = (com.lody.virtual.server.p009pm.parser.VPackage) r1     // Catch:{ all -> 0x003a }
            if (r1 == 0) goto L_0x0037
            java.lang.Object r1 = r1.mExtras     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.PackageSetting r1 = (com.lody.virtual.server.p009pm.PackageSetting) r1     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.VPackageManagerService$ActivityIntentResolver r2 = r3.mActivities     // Catch:{ all -> 0x003a }
            java.util.HashMap r2 = r2.mActivities     // Catch:{ all -> 0x003a }
            java.lang.Object r4 = r2.get(r4)     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.parser.VPackage$ActivityComponent r4 = (com.lody.virtual.server.p009pm.parser.VPackage.ActivityComponent) r4     // Catch:{ all -> 0x003a }
            if (r4 == 0) goto L_0x0037
            com.lody.virtual.server.pm.PackageUserState r2 = r1.readUserState(r6)     // Catch:{ all -> 0x003a }
            android.content.pm.ActivityInfo r4 = com.lody.virtual.server.p009pm.parser.PackageParserEx.generateActivityInfo(r4, r5, r2, r6)     // Catch:{ all -> 0x003a }
            com.lody.virtual.client.fixer.ComponentFixer.fixComponentInfo(r1, r4, r6)     // Catch:{ all -> 0x003a }
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            return r4
        L_0x0037:
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            r4 = 0
            return r4
        L_0x003a:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VPackageManagerService.getActivityInfo(android.content.ComponentName, int, int):android.content.pm.ActivityInfo");
    }

    public boolean activitySupportsIntent(ComponentName componentName, Intent intent, String str) {
        synchronized (this.mPackages) {
            ActivityComponent activityComponent = (ActivityComponent) this.mActivities.mActivities.get(componentName);
            if (activityComponent == null) {
                return false;
            }
            for (int i = 0; i < activityComponent.intents.size(); i++) {
                if (((ActivityIntentInfo) activityComponent.intents.get(i)).filter.match(intent.getAction(), str, intent.getScheme(), intent.getData(), intent.getCategories(), TAG) >= 0) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0039, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.pm.ActivityInfo getReceiverInfo(android.content.ComponentName r4, int r5, int r6) {
        /*
            r3 = this;
            r3.checkUserId(r6)
            int r5 = r3.updateFlagsNought(r5)
            java.util.Map<java.lang.String, com.lody.virtual.server.pm.parser.VPackage> r0 = r3.mPackages
            monitor-enter(r0)
            java.util.Map<java.lang.String, com.lody.virtual.server.pm.parser.VPackage> r1 = r3.mPackages     // Catch:{ all -> 0x003a }
            java.lang.String r2 = r4.getPackageName()     // Catch:{ all -> 0x003a }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.parser.VPackage r1 = (com.lody.virtual.server.p009pm.parser.VPackage) r1     // Catch:{ all -> 0x003a }
            if (r1 == 0) goto L_0x0037
            java.lang.Object r1 = r1.mExtras     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.PackageSetting r1 = (com.lody.virtual.server.p009pm.PackageSetting) r1     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.VPackageManagerService$ActivityIntentResolver r2 = r3.mReceivers     // Catch:{ all -> 0x003a }
            java.util.HashMap r2 = r2.mActivities     // Catch:{ all -> 0x003a }
            java.lang.Object r4 = r2.get(r4)     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.parser.VPackage$ActivityComponent r4 = (com.lody.virtual.server.p009pm.parser.VPackage.ActivityComponent) r4     // Catch:{ all -> 0x003a }
            if (r4 == 0) goto L_0x0037
            com.lody.virtual.server.pm.PackageUserState r2 = r1.readUserState(r6)     // Catch:{ all -> 0x003a }
            android.content.pm.ActivityInfo r4 = com.lody.virtual.server.p009pm.parser.PackageParserEx.generateActivityInfo(r4, r5, r2, r6)     // Catch:{ all -> 0x003a }
            com.lody.virtual.client.fixer.ComponentFixer.fixComponentInfo(r1, r4, r6)     // Catch:{ all -> 0x003a }
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            return r4
        L_0x0037:
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            r4 = 0
            return r4
        L_0x003a:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VPackageManagerService.getReceiverInfo(android.content.ComponentName, int, int):android.content.pm.ActivityInfo");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0039, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.pm.ServiceInfo getServiceInfo(android.content.ComponentName r4, int r5, int r6) {
        /*
            r3 = this;
            r3.checkUserId(r6)
            int r5 = r3.updateFlagsNought(r5)
            java.util.Map<java.lang.String, com.lody.virtual.server.pm.parser.VPackage> r0 = r3.mPackages
            monitor-enter(r0)
            java.util.Map<java.lang.String, com.lody.virtual.server.pm.parser.VPackage> r1 = r3.mPackages     // Catch:{ all -> 0x003a }
            java.lang.String r2 = r4.getPackageName()     // Catch:{ all -> 0x003a }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.parser.VPackage r1 = (com.lody.virtual.server.p009pm.parser.VPackage) r1     // Catch:{ all -> 0x003a }
            if (r1 == 0) goto L_0x0037
            java.lang.Object r1 = r1.mExtras     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.PackageSetting r1 = (com.lody.virtual.server.p009pm.PackageSetting) r1     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.VPackageManagerService$ServiceIntentResolver r2 = r3.mServices     // Catch:{ all -> 0x003a }
            java.util.HashMap r2 = r2.mServices     // Catch:{ all -> 0x003a }
            java.lang.Object r4 = r2.get(r4)     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.parser.VPackage$ServiceComponent r4 = (com.lody.virtual.server.p009pm.parser.VPackage.ServiceComponent) r4     // Catch:{ all -> 0x003a }
            if (r4 == 0) goto L_0x0037
            com.lody.virtual.server.pm.PackageUserState r2 = r1.readUserState(r6)     // Catch:{ all -> 0x003a }
            android.content.pm.ServiceInfo r4 = com.lody.virtual.server.p009pm.parser.PackageParserEx.generateServiceInfo(r4, r5, r2, r6)     // Catch:{ all -> 0x003a }
            com.lody.virtual.client.fixer.ComponentFixer.fixComponentInfo(r1, r4, r6)     // Catch:{ all -> 0x003a }
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            return r4
        L_0x0037:
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            r4 = 0
            return r4
        L_0x003a:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VPackageManagerService.getServiceInfo(android.content.ComponentName, int, int):android.content.pm.ServiceInfo");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0035, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.pm.ProviderInfo getProviderInfo(android.content.ComponentName r4, int r5, int r6) {
        /*
            r3 = this;
            r3.checkUserId(r6)
            int r5 = r3.updateFlagsNought(r5)
            java.util.Map<java.lang.String, com.lody.virtual.server.pm.parser.VPackage> r0 = r3.mPackages
            monitor-enter(r0)
            java.util.Map<java.lang.String, com.lody.virtual.server.pm.parser.VPackage> r1 = r3.mPackages     // Catch:{ all -> 0x0036 }
            java.lang.String r2 = r4.getPackageName()     // Catch:{ all -> 0x0036 }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0036 }
            com.lody.virtual.server.pm.parser.VPackage r1 = (com.lody.virtual.server.p009pm.parser.VPackage) r1     // Catch:{ all -> 0x0036 }
            if (r1 == 0) goto L_0x0033
            java.lang.Object r1 = r1.mExtras     // Catch:{ all -> 0x0036 }
            com.lody.virtual.server.pm.PackageSetting r1 = (com.lody.virtual.server.p009pm.PackageSetting) r1     // Catch:{ all -> 0x0036 }
            java.util.HashMap<android.content.ComponentName, com.lody.virtual.server.pm.parser.VPackage$ProviderComponent> r2 = r3.mProvidersByComponent     // Catch:{ all -> 0x0036 }
            java.lang.Object r4 = r2.get(r4)     // Catch:{ all -> 0x0036 }
            com.lody.virtual.server.pm.parser.VPackage$ProviderComponent r4 = (com.lody.virtual.server.p009pm.parser.VPackage.ProviderComponent) r4     // Catch:{ all -> 0x0036 }
            if (r4 == 0) goto L_0x0033
            com.lody.virtual.server.pm.PackageUserState r2 = r1.readUserState(r6)     // Catch:{ all -> 0x0036 }
            android.content.pm.ProviderInfo r4 = com.lody.virtual.server.p009pm.parser.PackageParserEx.generateProviderInfo(r4, r5, r2, r6)     // Catch:{ all -> 0x0036 }
            com.lody.virtual.client.fixer.ComponentFixer.fixComponentInfo(r1, r4, r6)     // Catch:{ all -> 0x0036 }
            monitor-exit(r0)     // Catch:{ all -> 0x0036 }
            return r4
        L_0x0033:
            monitor-exit(r0)     // Catch:{ all -> 0x0036 }
            r4 = 0
            return r4
        L_0x0036:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0036 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VPackageManagerService.getProviderInfo(android.content.ComponentName, int, int):android.content.pm.ProviderInfo");
    }

    public ResolveInfo resolveIntent(Intent intent, String str, int i, int i2) {
        checkUserId(i2);
        int updateFlagsNought = updateFlagsNought(i);
        return chooseBestActivity(intent, str, updateFlagsNought, queryIntentActivities(intent, str, updateFlagsNought, 0));
    }

    private ResolveInfo chooseBestActivity(Intent intent, String str, int i, List<ResolveInfo> list) {
        if (list != null) {
            int size = list.size();
            if (size == 1) {
                return (ResolveInfo) list.get(0);
            }
            if (size > 1) {
                ResolveInfo resolveInfo = (ResolveInfo) list.get(0);
                ResolveInfo resolveInfo2 = (ResolveInfo) list.get(1);
                if (resolveInfo.priority != resolveInfo2.priority || resolveInfo.preferredOrder != resolveInfo2.preferredOrder || resolveInfo.isDefault != resolveInfo2.isDefault) {
                    return (ResolveInfo) list.get(0);
                }
                ResolveInfo findPreferredActivity = findPreferredActivity(intent, str, i, list, resolveInfo.priority);
                if (findPreferredActivity != null) {
                    return findPreferredActivity;
                }
                return (ResolveInfo) list.get(0);
            }
        }
        return null;
    }

    public List<ResolveInfo> queryIntentActivities(Intent intent, String str, int i, int i2) {
        checkUserId(i2);
        int updateFlagsNought = updateFlagsNought(i);
        ComponentName component = intent.getComponent();
        if (component == null && VERSION.SDK_INT >= 15 && intent.getSelector() != null) {
            intent = intent.getSelector();
            component = intent.getComponent();
        }
        Intent intent2 = intent;
        if (component != null) {
            ArrayList arrayList = new ArrayList(1);
            ActivityInfo activityInfo = getActivityInfo(component, updateFlagsNought, i2);
            if (activityInfo != null) {
                ResolveInfo resolveInfo = new ResolveInfo();
                resolveInfo.activityInfo = activityInfo;
                arrayList.add(resolveInfo);
            }
            return arrayList;
        }
        synchronized (this.mPackages) {
            String str2 = intent2.getPackage();
            if (str2 == null) {
                List<ResolveInfo> queryIntent = this.mActivities.queryIntent(intent2, str, updateFlagsNought, i2);
                return queryIntent;
            }
            VPackage vPackage = (VPackage) this.mPackages.get(str2);
            if (vPackage != null) {
                List<ResolveInfo> queryIntentForPackage = this.mActivities.queryIntentForPackage(intent2, str, updateFlagsNought, vPackage.activities, i2);
                return queryIntentForPackage;
            }
            List<ResolveInfo> emptyList = Collections.emptyList();
            return emptyList;
        }
    }

    public List<ResolveInfo> queryIntentReceivers(Intent intent, String str, int i, int i2) {
        checkUserId(i2);
        int updateFlagsNought = updateFlagsNought(i);
        ComponentName component = intent.getComponent();
        if (component == null && VERSION.SDK_INT >= 15 && intent.getSelector() != null) {
            intent = intent.getSelector();
            component = intent.getComponent();
        }
        Intent intent2 = intent;
        if (component != null) {
            ArrayList arrayList = new ArrayList(1);
            ActivityInfo receiverInfo = getReceiverInfo(component, updateFlagsNought, i2);
            if (receiverInfo != null) {
                ResolveInfo resolveInfo = new ResolveInfo();
                resolveInfo.activityInfo = receiverInfo;
                arrayList.add(resolveInfo);
            }
            return arrayList;
        }
        synchronized (this.mPackages) {
            String str2 = intent2.getPackage();
            if (str2 == null) {
                List<ResolveInfo> queryIntent = this.mReceivers.queryIntent(intent2, str, updateFlagsNought, i2);
                return queryIntent;
            }
            VPackage vPackage = (VPackage) this.mPackages.get(str2);
            if (vPackage != null) {
                List<ResolveInfo> queryIntentForPackage = this.mReceivers.queryIntentForPackage(intent2, str, updateFlagsNought, vPackage.receivers, i2);
                return queryIntentForPackage;
            }
            List<ResolveInfo> emptyList = Collections.emptyList();
            return emptyList;
        }
    }

    public ResolveInfo resolveService(Intent intent, String str, int i, int i2) {
        checkUserId(i2);
        List queryIntentServices = queryIntentServices(intent, str, updateFlagsNought(i), i2);
        if (queryIntentServices == null || queryIntentServices.size() < 1) {
            return null;
        }
        return (ResolveInfo) queryIntentServices.get(0);
    }

    public List<ResolveInfo> queryIntentServices(Intent intent, String str, int i, int i2) {
        checkUserId(i2);
        int updateFlagsNought = updateFlagsNought(i);
        ComponentName component = intent.getComponent();
        if (component == null && VERSION.SDK_INT >= 15 && intent.getSelector() != null) {
            intent = intent.getSelector();
            component = intent.getComponent();
        }
        Intent intent2 = intent;
        if (component != null) {
            ArrayList arrayList = new ArrayList(1);
            ServiceInfo serviceInfo = getServiceInfo(component, updateFlagsNought, i2);
            if (serviceInfo != null) {
                ResolveInfo resolveInfo = new ResolveInfo();
                resolveInfo.serviceInfo = serviceInfo;
                arrayList.add(resolveInfo);
            }
            return arrayList;
        }
        synchronized (this.mPackages) {
            String str2 = intent2.getPackage();
            if (str2 == null) {
                List<ResolveInfo> queryIntent = this.mServices.queryIntent(intent2, str, updateFlagsNought, i2);
                return queryIntent;
            }
            VPackage vPackage = (VPackage) this.mPackages.get(str2);
            if (vPackage != null) {
                List<ResolveInfo> queryIntentForPackage = this.mServices.queryIntentForPackage(intent2, str, updateFlagsNought, vPackage.services, i2);
                return queryIntentForPackage;
            }
            List<ResolveInfo> emptyList = Collections.emptyList();
            return emptyList;
        }
    }

    @TargetApi(19)
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, String str, int i, int i2) {
        checkUserId(i2);
        int updateFlagsNought = updateFlagsNought(i);
        ComponentName component = intent.getComponent();
        if (component == null && VERSION.SDK_INT >= 15 && intent.getSelector() != null) {
            intent = intent.getSelector();
            component = intent.getComponent();
        }
        Intent intent2 = intent;
        if (component != null) {
            ArrayList arrayList = new ArrayList(1);
            ProviderInfo providerInfo = getProviderInfo(component, updateFlagsNought, i2);
            if (providerInfo != null) {
                ResolveInfo resolveInfo = new ResolveInfo();
                resolveInfo.providerInfo = providerInfo;
                arrayList.add(resolveInfo);
            }
            return arrayList;
        }
        synchronized (this.mPackages) {
            String str2 = intent2.getPackage();
            if (str2 == null) {
                List<ResolveInfo> queryIntent = this.mProviders.queryIntent(intent2, str, updateFlagsNought, i2);
                return queryIntent;
            }
            VPackage vPackage = (VPackage) this.mPackages.get(str2);
            if (vPackage != null) {
                List<ResolveInfo> queryIntentForPackage = this.mProviders.queryIntentForPackage(intent2, str, updateFlagsNought, vPackage.providers, i2);
                return queryIntentForPackage;
            }
            List<ResolveInfo> emptyList = Collections.emptyList();
            return emptyList;
        }
    }

    public VParceledListSlice<ProviderInfo> queryContentProviders(String str, int i, int i2) {
        int userId = VUserHandle.getUserId(i);
        checkUserId(userId);
        int updateFlagsNought = updateFlagsNought(i2);
        ArrayList arrayList = new ArrayList(3);
        synchronized (this.mPackages) {
            for (ProviderComponent providerComponent : this.mProvidersByComponent.values()) {
                PackageSetting packageSetting = (PackageSetting) providerComponent.owner.mExtras;
                if (str == null || (packageSetting.appId == VUserHandle.getAppId(i) && providerComponent.info.processName.equals(str))) {
                    arrayList.add(PackageParserEx.generateProviderInfo(providerComponent, updateFlagsNought, packageSetting.readUserState(userId), userId));
                }
            }
        }
        if (!arrayList.isEmpty()) {
            Collections.sort(arrayList, sProviderInitOrderSorter);
        }
        return new VParceledListSlice<>(arrayList);
    }

    public VParceledListSlice<PackageInfo> getInstalledPackages(int i, int i2) {
        checkUserId(i2);
        ArrayList arrayList = new ArrayList(this.mPackages.size());
        synchronized (this.mPackages) {
            for (VPackage vPackage : this.mPackages.values()) {
                PackageInfo generatePackageInfo = generatePackageInfo(vPackage, (PackageSetting) vPackage.mExtras, i, i2);
                if (generatePackageInfo != null) {
                    arrayList.add(generatePackageInfo);
                }
            }
        }
        return new VParceledListSlice<>(arrayList);
    }

    public VParceledListSlice<ApplicationInfo> getInstalledApplications(int i, int i2) {
        checkUserId(i2);
        int updateFlagsNought = updateFlagsNought(i);
        ArrayList arrayList = new ArrayList(this.mPackages.size());
        synchronized (this.mPackages) {
            for (VPackage vPackage : this.mPackages.values()) {
                arrayList.add(PackageParserEx.generateApplicationInfo(vPackage, updateFlagsNought, ((PackageSetting) vPackage.mExtras).readUserState(i2), i2));
            }
        }
        return new VParceledListSlice<>(arrayList);
    }

    public PermissionInfo getPermissionInfo(String str, int i) {
        synchronized (this.mPackages) {
            PermissionComponent permissionComponent = (PermissionComponent) this.mPermissions.get(str);
            if (permissionComponent == null) {
                return null;
            }
            PermissionInfo permissionInfo = new PermissionInfo(permissionComponent.info);
            return permissionInfo;
        }
    }

    public List<PermissionInfo> queryPermissionsByGroup(String str, int i) {
        synchronized (this.mPackages) {
        }
        return null;
    }

    public PermissionGroupInfo getPermissionGroupInfo(String str, int i) {
        synchronized (this.mPackages) {
            PermissionGroupComponent permissionGroupComponent = (PermissionGroupComponent) this.mPermissionGroups.get(str);
            if (permissionGroupComponent == null) {
                return null;
            }
            PermissionGroupInfo permissionGroupInfo = new PermissionGroupInfo(permissionGroupComponent.info);
            return permissionGroupInfo;
        }
    }

    public List<PermissionGroupInfo> getAllPermissionGroups(int i) {
        ArrayList arrayList;
        synchronized (this.mPackages) {
            arrayList = new ArrayList(this.mPermissionGroups.size());
            for (PermissionGroupComponent permissionGroupComponent : this.mPermissionGroups.values()) {
                arrayList.add(new PermissionGroupInfo(permissionGroupComponent.info));
            }
        }
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0039, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.pm.ProviderInfo resolveContentProvider(java.lang.String r3, int r4, int r5) {
        /*
            r2 = this;
            r2.checkUserId(r5)
            int r4 = r2.updateFlagsNought(r4)
            java.util.Map<java.lang.String, com.lody.virtual.server.pm.parser.VPackage> r0 = r2.mPackages
            monitor-enter(r0)
            java.util.HashMap<java.lang.String, com.lody.virtual.server.pm.parser.VPackage$ProviderComponent> r1 = r2.mProvidersByAuthority     // Catch:{ all -> 0x003a }
            java.lang.Object r3 = r1.get(r3)     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.parser.VPackage$ProviderComponent r3 = (com.lody.virtual.server.p009pm.parser.VPackage.ProviderComponent) r3     // Catch:{ all -> 0x003a }
            if (r3 == 0) goto L_0x0037
            com.lody.virtual.server.pm.parser.VPackage r1 = r3.owner     // Catch:{ all -> 0x003a }
            java.lang.Object r1 = r1.mExtras     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.PackageSetting r1 = (com.lody.virtual.server.p009pm.PackageSetting) r1     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.PackageUserState r1 = r1.readUserState(r5)     // Catch:{ all -> 0x003a }
            android.content.pm.ProviderInfo r3 = com.lody.virtual.server.p009pm.parser.PackageParserEx.generateProviderInfo(r3, r4, r1, r5)     // Catch:{ all -> 0x003a }
            if (r3 == 0) goto L_0x0037
            java.util.Map<java.lang.String, com.lody.virtual.server.pm.parser.VPackage> r4 = r2.mPackages     // Catch:{ all -> 0x003a }
            java.lang.String r1 = r3.packageName     // Catch:{ all -> 0x003a }
            java.lang.Object r4 = r4.get(r1)     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.parser.VPackage r4 = (com.lody.virtual.server.p009pm.parser.VPackage) r4     // Catch:{ all -> 0x003a }
            java.lang.Object r4 = r4.mExtras     // Catch:{ all -> 0x003a }
            com.lody.virtual.server.pm.PackageSetting r4 = (com.lody.virtual.server.p009pm.PackageSetting) r4     // Catch:{ all -> 0x003a }
            com.lody.virtual.client.fixer.ComponentFixer.fixComponentInfo(r4, r3, r5)     // Catch:{ all -> 0x003a }
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            return r3
        L_0x0037:
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            r3 = 0
            return r3
        L_0x003a:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003a }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VPackageManagerService.resolveContentProvider(java.lang.String, int, int):android.content.pm.ProviderInfo");
    }

    public ApplicationInfo getApplicationInfo(String str, int i, int i2) {
        checkUserId(i2);
        int updateFlagsNought = updateFlagsNought(i);
        synchronized (this.mPackages) {
            VPackage vPackage = (VPackage) this.mPackages.get(str);
            if (vPackage == null) {
                return null;
            }
            ApplicationInfo generateApplicationInfo = PackageParserEx.generateApplicationInfo(vPackage, updateFlagsNought, ((PackageSetting) vPackage.mExtras).readUserState(i2), i2);
            return generateApplicationInfo;
        }
    }

    public String[] getPackagesForUid(int i) {
        String[] strArr;
        int userId = VUserHandle.getUserId(i);
        checkUserId(userId);
        synchronized (this) {
            ArrayList arrayList = new ArrayList(2);
            for (VPackage vPackage : this.mPackages.values()) {
                if (VUserHandle.getUid(userId, ((PackageSetting) vPackage.mExtras).appId) == i) {
                    arrayList.add(vPackage.packageName);
                }
            }
            strArr = (String[]) arrayList.toArray(new String[arrayList.size()]);
        }
        return strArr;
    }

    public int getPackageUid(String str, int i) {
        checkUserId(i);
        synchronized (this.mPackages) {
            VPackage vPackage = (VPackage) this.mPackages.get(str);
            if (vPackage == null) {
                return -1;
            }
            int uid = VUserHandle.getUid(i, ((PackageSetting) vPackage.mExtras).appId);
            return uid;
        }
    }

    public String getNameForUid(int i) {
        int appId = VUserHandle.getAppId(i);
        synchronized (this.mPackages) {
            for (VPackage vPackage : this.mPackages.values()) {
                PackageSetting packageSetting = (PackageSetting) vPackage.mExtras;
                if (packageSetting.appId == appId) {
                    String str = packageSetting.packageName;
                    return str;
                }
            }
            return null;
        }
    }

    public List<String> querySharedPackages(String str) {
        synchronized (this.mPackages) {
            VPackage vPackage = (VPackage) this.mPackages.get(str);
            if (vPackage != null) {
                if (vPackage.mSharedUserId != null) {
                    ArrayList arrayList = new ArrayList();
                    for (VPackage vPackage2 : this.mPackages.values()) {
                        if (TextUtils.equals(vPackage2.mSharedUserId, vPackage.mSharedUserId)) {
                            arrayList.add(vPackage2.packageName);
                        }
                    }
                    return arrayList;
                }
            }
            List<String> list = Collections.EMPTY_LIST;
            return list;
        }
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (Throwable th) {
            th.printStackTrace();
            throw th;
        }
    }

    public IPackageInstaller getPackageInstaller() {
        return VPackageInstallerService.get();
    }

    /* access modifiers changed from: 0000 */
    public void createNewUser(int i, File file) {
        for (VPackage vPackage : this.mPackages.values()) {
            ((PackageSetting) vPackage.mExtras).modifyUserState(i);
        }
    }

    /* access modifiers changed from: 0000 */
    public void cleanUpUser(int i) {
        for (VPackage vPackage : this.mPackages.values()) {
            ((PackageSetting) vPackage.mExtras).removeUser(i);
        }
    }
}
