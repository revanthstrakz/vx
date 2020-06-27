package com.lody.virtual.server.p008am;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.IServiceConnection;
import android.app.IStopUserCallback;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import com.lody.virtual.client.IVClient;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.client.env.SpecialComponentList;
import com.lody.virtual.client.ipc.ProviderCall;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.client.ipc.VNotificationManager;
import com.lody.virtual.client.stub.ChooseTypeAndAccountActivity;
import com.lody.virtual.client.stub.VASettings;
import com.lody.virtual.helper.collection.ArrayMap;
import com.lody.virtual.helper.collection.SparseArray;
import com.lody.virtual.helper.compat.ApplicationThreadCompat;
import com.lody.virtual.helper.compat.BundleCompat;
import com.lody.virtual.helper.compat.IApplicationThreadCompat;
import com.lody.virtual.helper.utils.ComponentUtils;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VBinder;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.AppTaskInfo;
import com.lody.virtual.remote.BadgerInfo;
import com.lody.virtual.remote.PendingIntentData;
import com.lody.virtual.remote.PendingResultData;
import com.lody.virtual.remote.VParceledListSlice;
import com.lody.virtual.server.IActivityManager.Stub;
import com.lody.virtual.server.interfaces.IProcessObserver;
import com.lody.virtual.server.p008am.ServiceRecord.IntentBindRecord;
import com.lody.virtual.server.p009pm.PackageCacheManager;
import com.lody.virtual.server.p009pm.PackageSetting;
import com.lody.virtual.server.p009pm.VAppManagerService;
import com.lody.virtual.server.p009pm.VPackageManagerService;
import com.lody.virtual.server.secondary.BinderDelegateService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import mirror.android.app.IServiceConnectionO;

/* renamed from: com.lody.virtual.server.am.VActivityManagerService */
public class VActivityManagerService extends Stub {
    private static final boolean BROADCAST_NOT_STARTED_PKG = false;
    private static final String TAG = VActivityManagerService.class.getSimpleName();
    private static final AtomicReference<VActivityManagerService> sService = new AtomicReference<>();

    /* renamed from: am */
    private ActivityManager f182am = ((ActivityManager) VirtualCore.get().getContext().getSystemService(ServiceManagerNative.ACTIVITY));
    private final Set<ServiceRecord> mHistory = new HashSet();
    private final ActivityStack mMainStack = new ActivityStack(this);
    private final PendingIntents mPendingIntents = new PendingIntents();
    private final SparseArray<ProcessRecord> mPidsSelfLocked = new SparseArray<>();
    private final ProcessMap<ProcessRecord> mProcessNames = new ProcessMap<>();

    /* renamed from: nm */
    private NotificationManager f183nm = ((NotificationManager) VirtualCore.get().getContext().getSystemService(ServiceManagerNative.NOTIFICATION));

    public void dump() {
    }

    public void handleApplicationCrash() {
    }

    public void registerProcessObserver(IProcessObserver iProcessObserver) {
    }

    public void unregisterProcessObserver(IProcessObserver iProcessObserver) {
    }

    public static VActivityManagerService get() {
        return (VActivityManagerService) sService.get();
    }

    public static void systemReady(Context context) {
        new VActivityManagerService().onCreate(context);
    }

    private static ServiceInfo resolveServiceInfo(Intent intent, int i) {
        if (intent != null) {
            ServiceInfo resolveServiceInfo = VirtualCore.get().resolveServiceInfo(intent, i);
            if (resolveServiceInfo != null) {
                return resolveServiceInfo;
            }
        }
        return null;
    }

    public void onCreate(Context context) {
        PackageInfo packageInfo;
        AttributeCache.init(context);
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 137);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        if (packageInfo != null) {
            sService.set(this);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unable to found PackageInfo : ");
        sb.append(context.getPackageName());
        throw new RuntimeException(sb.toString());
    }

    public int startActivity(Intent intent, ActivityInfo activityInfo, IBinder iBinder, Bundle bundle, String str, int i, int i2) {
        int startActivityLocked;
        synchronized (this) {
            startActivityLocked = this.mMainStack.startActivityLocked(i2, intent, activityInfo, iBinder, bundle, str, i);
        }
        return startActivityLocked;
    }

    public int startActivities(Intent[] intentArr, String[] strArr, IBinder iBinder, Bundle bundle, int i) {
        synchronized (this) {
            ActivityInfo[] activityInfoArr = new ActivityInfo[intentArr.length];
            for (int i2 = 0; i2 < intentArr.length; i2++) {
                ActivityInfo resolveActivityInfo = VirtualCore.get().resolveActivityInfo(intentArr[i2], i);
                if (resolveActivityInfo == null) {
                    return -1;
                }
                activityInfoArr[i2] = resolveActivityInfo;
            }
            int startActivitiesLocked = this.mMainStack.startActivitiesLocked(i, intentArr, activityInfoArr, strArr, iBinder, bundle);
            return startActivitiesLocked;
        }
    }

    public String getPackageForIntentSender(IBinder iBinder) {
        PendingIntentData pendingIntent = this.mPendingIntents.getPendingIntent(iBinder);
        if (pendingIntent != null) {
            return pendingIntent.creator;
        }
        return null;
    }

    public PendingIntentData getPendingIntent(IBinder iBinder) {
        return this.mPendingIntents.getPendingIntent(iBinder);
    }

    public void addPendingIntent(IBinder iBinder, String str) {
        this.mPendingIntents.addPendingIntent(iBinder, str);
    }

    public void removePendingIntent(IBinder iBinder) {
        this.mPendingIntents.removePendingIntent(iBinder);
    }

    public int getSystemPid() {
        return VirtualCore.get().myUid();
    }

    public void onActivityCreated(ComponentName componentName, ComponentName componentName2, IBinder iBinder, Intent intent, String str, int i, int i2, int i3) {
        ProcessRecord findProcessLocked = findProcessLocked(Binder.getCallingPid());
        if (findProcessLocked != null) {
            this.mMainStack.onActivityCreated(findProcessLocked, componentName, componentName2, iBinder, intent, str, i, i2, i3);
        }
    }

    public void onActivityResumed(int i, IBinder iBinder) {
        this.mMainStack.onActivityResumed(i, iBinder);
    }

    public boolean onActivityDestroyed(int i, IBinder iBinder) {
        return this.mMainStack.onActivityDestroyed(i, iBinder) != null;
    }

    public AppTaskInfo getTaskInfo(int i) {
        return this.mMainStack.getTaskInfo(i);
    }

    public String getPackageForToken(int i, IBinder iBinder) {
        return this.mMainStack.getPackageForToken(i, iBinder);
    }

    public ComponentName getActivityClassForToken(int i, IBinder iBinder) {
        return this.mMainStack.getActivityClassForToken(i, iBinder);
    }

    private void processDead(ProcessRecord processRecord) {
        synchronized (this.mHistory) {
            Iterator it = this.mHistory.iterator();
            while (it.hasNext()) {
                ServiceRecord serviceRecord = (ServiceRecord) it.next();
                if (serviceRecord.process != null && serviceRecord.process.pid == processRecord.pid) {
                    it.remove();
                }
            }
            this.mMainStack.processDied(processRecord);
        }
    }

    public IBinder acquireProviderClient(int i, ProviderInfo providerInfo) {
        ProcessRecord findProcessLocked;
        ProcessRecord startProcessIfNeedLocked;
        synchronized (this.mPidsSelfLocked) {
            findProcessLocked = findProcessLocked(VBinder.getCallingPid());
        }
        if (findProcessLocked != null) {
            String str = providerInfo.processName;
            synchronized (this) {
                startProcessIfNeedLocked = startProcessIfNeedLocked(str, i, providerInfo.packageName);
            }
            if (startProcessIfNeedLocked != null && startProcessIfNeedLocked.client.asBinder().pingBinder()) {
                try {
                    return startProcessIfNeedLocked.client.acquireProviderClient(providerInfo);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        throw new SecurityException("Who are you?");
    }

    public ComponentName getCallingActivity(int i, IBinder iBinder) {
        return this.mMainStack.getCallingActivity(i, iBinder);
    }

    public String getCallingPackage(int i, IBinder iBinder) {
        return this.mMainStack.getCallingPackage(i, iBinder);
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (Throwable th) {
            th.printStackTrace();
            throw th;
        }
    }

    private void addRecord(ServiceRecord serviceRecord) {
        this.mHistory.add(serviceRecord);
    }

    private ServiceRecord findRecordLocked(int i, ServiceInfo serviceInfo) {
        synchronized (this.mHistory) {
            for (ServiceRecord serviceRecord : this.mHistory) {
                if ((serviceRecord.process == null || serviceRecord.process.userId == i) && ComponentUtils.isSameComponent(serviceInfo, serviceRecord.serviceInfo)) {
                    return serviceRecord;
                }
            }
            return null;
        }
    }

    private ServiceRecord findRecordLocked(IServiceConnection iServiceConnection) {
        synchronized (this.mHistory) {
            for (ServiceRecord serviceRecord : this.mHistory) {
                if (serviceRecord.containConnection(iServiceConnection)) {
                    return serviceRecord;
                }
            }
            return null;
        }
    }

    public ComponentName startService(IBinder iBinder, Intent intent, String str, int i) {
        ComponentName startServiceCommon;
        synchronized (this) {
            startServiceCommon = startServiceCommon(intent, true, i);
        }
        return startServiceCommon;
    }

    private ComponentName startServiceCommon(Intent intent, boolean z, int i) {
        ServiceInfo resolveServiceInfo = resolveServiceInfo(intent, i);
        if (resolveServiceInfo == null) {
            return null;
        }
        ProcessRecord startProcessIfNeedLocked = startProcessIfNeedLocked(ComponentUtils.getProcessName(resolveServiceInfo), i, resolveServiceInfo.packageName);
        if (startProcessIfNeedLocked == null) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to start new Process for : ");
            sb.append(ComponentUtils.toComponentName(resolveServiceInfo));
            VLog.m87e(str, sb.toString(), new Object[0]);
            return null;
        }
        IInterface iInterface = startProcessIfNeedLocked.appThread;
        ServiceRecord findRecordLocked = findRecordLocked(i, resolveServiceInfo);
        if (findRecordLocked == null) {
            findRecordLocked = new ServiceRecord();
            findRecordLocked.startId = 0;
            findRecordLocked.activeSince = SystemClock.elapsedRealtime();
            findRecordLocked.process = startProcessIfNeedLocked;
            findRecordLocked.serviceInfo = resolveServiceInfo;
            try {
                IApplicationThreadCompat.scheduleCreateService(iInterface, findRecordLocked, findRecordLocked.serviceInfo, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            addRecord(findRecordLocked);
        }
        findRecordLocked.lastActivityTime = SystemClock.uptimeMillis();
        if (z) {
            findRecordLocked.startId++;
            try {
                IApplicationThreadCompat.scheduleServiceArgs(iInterface, findRecordLocked, resolveServiceInfo.applicationInfo != null && resolveServiceInfo.applicationInfo.targetSdkVersion < 5, findRecordLocked.startId, 0, intent);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
        return ComponentUtils.toComponentName(resolveServiceInfo);
    }

    public int stopService(IBinder iBinder, Intent intent, String str, int i) {
        synchronized (this) {
            ServiceInfo resolveServiceInfo = resolveServiceInfo(intent, i);
            if (resolveServiceInfo == null) {
                return 0;
            }
            ServiceRecord findRecordLocked = findRecordLocked(i, resolveServiceInfo);
            if (findRecordLocked == null) {
                return 0;
            }
            stopServiceCommon(findRecordLocked, ComponentUtils.toComponentName(resolveServiceInfo));
            return 1;
        }
    }

    public boolean stopServiceToken(ComponentName componentName, IBinder iBinder, int i, int i2) {
        synchronized (this) {
            ServiceRecord serviceRecord = (ServiceRecord) iBinder;
            if (serviceRecord == null || (serviceRecord.startId != i && i != -1)) {
                return false;
            }
            stopServiceCommon(serviceRecord, componentName);
            return true;
        }
    }

    private void stopServiceCommon(ServiceRecord serviceRecord, ComponentName componentName) {
        for (IntentBindRecord intentBindRecord : serviceRecord.bindings) {
            for (IServiceConnection iServiceConnection : intentBindRecord.connections) {
                try {
                    if (VERSION.SDK_INT >= 26) {
                        IServiceConnectionO.connected.call(iServiceConnection, componentName, null, Boolean.valueOf(true));
                    } else {
                        iServiceConnection.connected(componentName, null);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            try {
                IApplicationThreadCompat.scheduleUnbindService(serviceRecord.process.appThread, serviceRecord, intentBindRecord.intent);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
        try {
            IApplicationThreadCompat.scheduleStopService(serviceRecord.process.appThread, serviceRecord);
        } catch (RemoteException e3) {
            e3.printStackTrace();
        }
        this.mHistory.remove(serviceRecord);
    }

    public int bindService(IBinder iBinder, IBinder iBinder2, Intent intent, String str, IServiceConnection iServiceConnection, int i, int i2) {
        synchronized (this) {
            ServiceInfo resolveServiceInfo = resolveServiceInfo(intent, i2);
            if (resolveServiceInfo == null) {
                return 0;
            }
            ServiceRecord findRecordLocked = findRecordLocked(i2, resolveServiceInfo);
            if ((findRecordLocked == null) && (i & 1) != 0) {
                startServiceCommon(intent, false, i2);
                findRecordLocked = findRecordLocked(i2, resolveServiceInfo);
            }
            if (findRecordLocked == null) {
                return 0;
            }
            IntentBindRecord peekBinding = findRecordLocked.peekBinding(intent);
            if (peekBinding == null || peekBinding.binder == null || !peekBinding.binder.pingBinder()) {
                try {
                    IApplicationThreadCompat.scheduleBindService(findRecordLocked.process.appThread, findRecordLocked, intent, false, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                if (peekBinding.doRebind) {
                    try {
                        IApplicationThreadCompat.scheduleBindService(findRecordLocked.process.appThread, findRecordLocked, intent, true, 0);
                    } catch (RemoteException e2) {
                        e2.printStackTrace();
                    }
                }
                connectService(iServiceConnection, new ComponentName(findRecordLocked.serviceInfo.packageName, findRecordLocked.serviceInfo.name), peekBinding, false);
            }
            findRecordLocked.lastActivityTime = SystemClock.uptimeMillis();
            findRecordLocked.addToBoundIntent(intent, iServiceConnection);
            return 1;
        }
    }

    public boolean unbindService(IServiceConnection iServiceConnection, int i) {
        synchronized (this) {
            ServiceRecord findRecordLocked = findRecordLocked(iServiceConnection);
            if (findRecordLocked == null) {
                return false;
            }
            for (IntentBindRecord intentBindRecord : findRecordLocked.bindings) {
                if (intentBindRecord.containConnection(iServiceConnection)) {
                    intentBindRecord.removeConnection(iServiceConnection);
                    try {
                        IApplicationThreadCompat.scheduleUnbindService(findRecordLocked.process.appThread, findRecordLocked, intentBindRecord.intent);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (findRecordLocked.startId <= 0 && findRecordLocked.getConnectionCount() <= 0) {
                try {
                    IApplicationThreadCompat.scheduleStopService(findRecordLocked.process.appThread, findRecordLocked);
                } catch (RemoteException e2) {
                    e2.printStackTrace();
                }
                if (VERSION.SDK_INT < 21) {
                    this.mHistory.remove(findRecordLocked);
                }
            }
            return true;
        }
    }

    public void unbindFinished(IBinder iBinder, Intent intent, boolean z, int i) {
        synchronized (this) {
            ServiceRecord serviceRecord = (ServiceRecord) iBinder;
            if (serviceRecord != null) {
                IntentBindRecord peekBinding = serviceRecord.peekBinding(intent);
                if (peekBinding != null) {
                    peekBinding.doRebind = z;
                }
            }
        }
    }

    public boolean isVAServiceToken(IBinder iBinder) {
        return iBinder instanceof ServiceRecord;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0010, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void serviceDoneExecuting(android.os.IBinder r1, int r2, int r3, int r4, int r5) {
        /*
            r0 = this;
            monitor-enter(r0)
            com.lody.virtual.server.am.ServiceRecord r1 = (com.lody.virtual.server.p008am.ServiceRecord) r1     // Catch:{ all -> 0x0011 }
            if (r1 != 0) goto L_0x0007
            monitor-exit(r0)     // Catch:{ all -> 0x0011 }
            return
        L_0x0007:
            r3 = 2
            if (r3 != r2) goto L_0x000f
            java.util.Set<com.lody.virtual.server.am.ServiceRecord> r2 = r0.mHistory     // Catch:{ all -> 0x0011 }
            r2.remove(r1)     // Catch:{ all -> 0x0011 }
        L_0x000f:
            monitor-exit(r0)     // Catch:{ all -> 0x0011 }
            return
        L_0x0011:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0011 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p008am.VActivityManagerService.serviceDoneExecuting(android.os.IBinder, int, int, int, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001b, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.IBinder peekService(android.content.Intent r2, java.lang.String r3, int r4) {
        /*
            r1 = this;
            monitor-enter(r1)
            android.content.pm.ServiceInfo r3 = resolveServiceInfo(r2, r4)     // Catch:{ all -> 0x001c }
            r0 = 0
            if (r3 != 0) goto L_0x000a
            monitor-exit(r1)     // Catch:{ all -> 0x001c }
            return r0
        L_0x000a:
            com.lody.virtual.server.am.ServiceRecord r3 = r1.findRecordLocked(r4, r3)     // Catch:{ all -> 0x001c }
            if (r3 == 0) goto L_0x001a
            com.lody.virtual.server.am.ServiceRecord$IntentBindRecord r2 = r3.peekBinding(r2)     // Catch:{ all -> 0x001c }
            if (r2 == 0) goto L_0x001a
            android.os.IBinder r2 = r2.binder     // Catch:{ all -> 0x001c }
            monitor-exit(r1)     // Catch:{ all -> 0x001c }
            return r2
        L_0x001a:
            monitor-exit(r1)     // Catch:{ all -> 0x001c }
            return r0
        L_0x001c:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x001c }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p008am.VActivityManagerService.peekService(android.content.Intent, java.lang.String, int):android.os.IBinder");
    }

    public void publishService(IBinder iBinder, Intent intent, IBinder iBinder2, int i) {
        synchronized (this) {
            ServiceRecord serviceRecord = (ServiceRecord) iBinder;
            if (serviceRecord != null) {
                IntentBindRecord peekBinding = serviceRecord.peekBinding(intent);
                if (peekBinding != null) {
                    peekBinding.binder = iBinder2;
                    for (IServiceConnection connectService : peekBinding.connections) {
                        connectService(connectService, ComponentUtils.toComponentName(serviceRecord.serviceInfo), peekBinding, false);
                    }
                }
            }
        }
    }

    private void connectService(IServiceConnection iServiceConnection, ComponentName componentName, IntentBindRecord intentBindRecord, boolean z) {
        try {
            BinderDelegateService binderDelegateService = new BinderDelegateService(componentName, intentBindRecord.binder);
            if (VERSION.SDK_INT >= 26) {
                IServiceConnectionO.connected.call(iServiceConnection, componentName, binderDelegateService, Boolean.valueOf(z));
                return;
            }
            iServiceConnection.connected(componentName, binderDelegateService);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public VParceledListSlice<RunningServiceInfo> getServices(int i, int i2, int i3) {
        VParceledListSlice<RunningServiceInfo> vParceledListSlice;
        synchronized (this.mHistory) {
            ArrayList arrayList = new ArrayList(this.mHistory.size());
            for (ServiceRecord serviceRecord : this.mHistory) {
                if (serviceRecord.process.userId == i3) {
                    RunningServiceInfo runningServiceInfo = new RunningServiceInfo();
                    runningServiceInfo.uid = serviceRecord.process.vuid;
                    runningServiceInfo.pid = serviceRecord.process.pid;
                    ProcessRecord findProcessLocked = findProcessLocked(serviceRecord.process.pid);
                    if (findProcessLocked != null) {
                        runningServiceInfo.process = findProcessLocked.processName;
                        runningServiceInfo.clientPackage = findProcessLocked.info.packageName;
                    }
                    runningServiceInfo.activeSince = serviceRecord.activeSince;
                    runningServiceInfo.lastActivityTime = serviceRecord.lastActivityTime;
                    runningServiceInfo.clientCount = serviceRecord.getClientCount();
                    runningServiceInfo.service = ComponentUtils.toComponentName(serviceRecord.serviceInfo);
                    runningServiceInfo.started = serviceRecord.startId > 0;
                    arrayList.add(runningServiceInfo);
                }
            }
            vParceledListSlice = new VParceledListSlice<>(arrayList);
        }
        return vParceledListSlice;
    }

    public void setServiceForeground(ComponentName componentName, IBinder iBinder, int i, Notification notification, boolean z, int i2) {
        ServiceRecord serviceRecord = (ServiceRecord) iBinder;
        if (serviceRecord == null) {
            return;
        }
        if (i != 0) {
            if (notification != null) {
                if (serviceRecord.foregroundId != i) {
                    if (serviceRecord.foregroundId != 0) {
                        cancelNotification(i2, serviceRecord.foregroundId, serviceRecord.serviceInfo.packageName);
                    }
                    serviceRecord.foregroundId = i;
                }
                serviceRecord.foregroundNoti = notification;
                postNotification(i2, i, serviceRecord.serviceInfo.packageName, notification);
                return;
            }
            throw new IllegalArgumentException("null notification");
        } else if (z) {
            cancelNotification(i2, serviceRecord.foregroundId, serviceRecord.serviceInfo.packageName);
            serviceRecord.foregroundId = 0;
            serviceRecord.foregroundNoti = null;
        }
    }

    private void cancelNotification(int i, int i2, String str) {
        int dealNotificationId = VNotificationManager.get().dealNotificationId(i2, str, null, i);
        this.f183nm.cancel(VNotificationManager.get().dealNotificationTag(dealNotificationId, str, null, i), dealNotificationId);
    }

    private void postNotification(int i, int i2, String str, Notification notification) {
        int dealNotificationId = VNotificationManager.get().dealNotificationId(i2, str, null, i);
        String dealNotificationTag = VNotificationManager.get().dealNotificationTag(dealNotificationId, str, null, i);
        VNotificationManager.get().addNotification(dealNotificationId, dealNotificationTag, str, i);
        try {
            this.f183nm.notify(dealNotificationTag, dealNotificationId, notification);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void processRestarted(String str, String str2, int i) {
        int callingPid = getCallingPid();
        int uid = VUserHandle.getUid(i, VAppManagerService.get().getAppId(str));
        synchronized (this) {
            if (findProcessLocked(callingPid) == null) {
                ApplicationInfo applicationInfo = VPackageManagerService.get().getApplicationInfo(str, 0, i);
                applicationInfo.flags |= 4;
                int parseVPid = parseVPid(getProcessName(callingPid));
                if (parseVPid != -1) {
                    performStartProcessLocked(uid, parseVPid, applicationInfo, str2);
                }
            }
        }
    }

    private int parseVPid(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(VirtualCore.get().getHostPkg());
        sb.append(":p");
        String sb2 = sb.toString();
        if (str != null && str.startsWith(sb2)) {
            try {
                return Integer.parseInt(str.substring(sb2.length()));
            } catch (NumberFormatException unused) {
            }
        }
        return -1;
    }

    private String getProcessName(int i) {
        for (RunningAppProcessInfo runningAppProcessInfo : this.f182am.getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == i) {
                return runningAppProcessInfo.processName;
            }
        }
        return null;
    }

    private void attachClient(int i, final IBinder iBinder) {
        IInterface iInterface;
        IVClient asInterface = IVClient.Stub.asInterface(iBinder);
        if (asInterface == null) {
            Process.killProcess(i);
            return;
        }
        final ProcessRecord processRecord = null;
        try {
            iInterface = ApplicationThreadCompat.asInterface(asInterface.getAppThread());
        } catch (RemoteException unused) {
            iInterface = null;
        }
        if (iInterface == null) {
            Process.killProcess(i);
            return;
        }
        try {
            IBinder token = asInterface.getToken();
            if (token instanceof ProcessRecord) {
                processRecord = (ProcessRecord) token;
            }
        } catch (RemoteException unused2) {
        }
        if (processRecord == null) {
            Process.killProcess(i);
            return;
        }
        try {
            iBinder.linkToDeath(new DeathRecipient() {
                public void binderDied() {
                    iBinder.unlinkToDeath(this, 0);
                    VActivityManagerService.this.onProcessDead(processRecord);
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        processRecord.client = asInterface;
        processRecord.appThread = iInterface;
        processRecord.pid = i;
        synchronized (this.mProcessNames) {
            this.mProcessNames.put(processRecord.processName, processRecord.vuid, processRecord);
            this.mPidsSelfLocked.put(processRecord.pid, processRecord);
        }
    }

    /* access modifiers changed from: private */
    public void onProcessDead(ProcessRecord processRecord) {
        this.mProcessNames.remove(processRecord.processName, processRecord.vuid);
        this.mPidsSelfLocked.remove(processRecord.pid);
        processDead(processRecord);
        processRecord.lock.open();
    }

    public int getFreeStubCount() {
        return VASettings.STUB_COUNT - this.mPidsSelfLocked.size();
    }

    public int initProcess(String str, String str2, int i) {
        int i2;
        synchronized (this) {
            ProcessRecord startProcessIfNeedLocked = startProcessIfNeedLocked(str2, i, str);
            i2 = startProcessIfNeedLocked != null ? startProcessIfNeedLocked.vpid : -1;
        }
        return i2;
    }

    /* access modifiers changed from: 0000 */
    public ProcessRecord startProcessIfNeedLocked(String str, int i, String str2) {
        if (get().getFreeStubCount() < 3) {
            killAllApps();
        }
        PackageSetting setting = PackageCacheManager.getSetting(str2);
        ApplicationInfo applicationInfo = VPackageManagerService.get().getApplicationInfo(str2, 0, i);
        if (setting == null || applicationInfo == null) {
            return null;
        }
        if (!setting.isLaunched(i)) {
            sendFirstLaunchBroadcast(setting, i);
            setting.setLaunched(i, true);
            VAppManagerService.get().savePersistenceData();
        }
        int uid = VUserHandle.getUid(i, setting.appId);
        ProcessRecord processRecord = (ProcessRecord) this.mProcessNames.get(str, uid);
        if (processRecord != null && processRecord.client.asBinder().pingBinder()) {
            return processRecord;
        }
        int queryFreeStubProcessLocked = queryFreeStubProcessLocked();
        if (queryFreeStubProcessLocked == -1) {
            return null;
        }
        ProcessRecord performStartProcessLocked = performStartProcessLocked(uid, queryFreeStubProcessLocked, applicationInfo, str);
        if (performStartProcessLocked != null) {
            performStartProcessLocked.pkgList.add(applicationInfo.packageName);
        }
        return performStartProcessLocked;
    }

    private void sendFirstLaunchBroadcast(PackageSetting packageSetting, int i) {
        Intent intent = new Intent("android.intent.action.PACKAGE_FIRST_LAUNCH", Uri.fromParts(ServiceManagerNative.PACKAGE, packageSetting.packageName, null));
        intent.setPackage(packageSetting.packageName);
        intent.putExtra("android.intent.extra.UID", VUserHandle.getUid(packageSetting.appId, i));
        intent.putExtra(Constants.EXTRA_USER_HANDLE, i);
        sendBroadcastAsUser(intent, null);
    }

    public int getUidByPid(int i) {
        synchronized (this.mPidsSelfLocked) {
            ProcessRecord findProcessLocked = findProcessLocked(i);
            if (findProcessLocked == null) {
                return Process.myUid();
            }
            int i2 = findProcessLocked.vuid;
            return i2;
        }
    }

    private ProcessRecord performStartProcessLocked(int i, int i2, ApplicationInfo applicationInfo, String str) {
        ProcessRecord processRecord = new ProcessRecord(applicationInfo, str, i, i2);
        Bundle bundle = new Bundle();
        BundleCompat.putBinder(bundle, "_VA_|_binder_", processRecord);
        bundle.putInt("_VA_|_vuid_", i);
        bundle.putString("_VA_|_process_", str);
        bundle.putString("_VA_|_pkg_", applicationInfo.packageName);
        Bundle call = ProviderCall.call(VASettings.getStubAuthority(i2), "_VA_|_init_process_", null, bundle);
        if (call == null) {
            return null;
        }
        attachClient(call.getInt("_VA_|_pid_"), BundleCompat.getBinder(call, "_VA_|_client_"));
        return processRecord;
    }

    private int queryFreeStubProcessLocked() {
        boolean z;
        for (int i = 0; i < VASettings.STUB_COUNT; i++) {
            int size = this.mPidsSelfLocked.size();
            while (true) {
                int i2 = size - 1;
                if (size <= 0) {
                    z = false;
                    break;
                } else if (((ProcessRecord) this.mPidsSelfLocked.valueAt(i2)).vpid == i) {
                    z = true;
                    break;
                } else {
                    size = i2;
                }
            }
            if (!z) {
                return i;
            }
        }
        return -1;
    }

    public boolean isAppProcess(String str) {
        return parseVPid(str) != -1;
    }

    public boolean isAppPid(int i) {
        boolean z;
        synchronized (this.mPidsSelfLocked) {
            z = findProcessLocked(i) != null;
        }
        return z;
    }

    public String getAppProcessName(int i) {
        synchronized (this.mPidsSelfLocked) {
            ProcessRecord processRecord = (ProcessRecord) this.mPidsSelfLocked.get(i);
            if (processRecord == null) {
                return null;
            }
            String str = processRecord.processName;
            return str;
        }
    }

    public List<String> getProcessPkgList(int i) {
        synchronized (this.mPidsSelfLocked) {
            ProcessRecord processRecord = (ProcessRecord) this.mPidsSelfLocked.get(i);
            if (processRecord == null) {
                return Collections.emptyList();
            }
            ArrayList arrayList = new ArrayList(processRecord.pkgList);
            return arrayList;
        }
    }

    public void killAllApps() {
        synchronized (this.mPidsSelfLocked) {
            for (int i = 0; i < this.mPidsSelfLocked.size(); i++) {
                Process.killProcess(((ProcessRecord) this.mPidsSelfLocked.valueAt(i)).pid);
            }
        }
    }

    public void killAppByPkg(String str, int i) {
        synchronized (this.mProcessNames) {
            ArrayMap map = this.mProcessNames.getMap();
            int size = map.size();
            while (true) {
                int i2 = size - 1;
                if (size > 0) {
                    SparseArray sparseArray = (SparseArray) map.valueAt(i2);
                    for (int i3 = 0; i3 < sparseArray.size(); i3++) {
                        ProcessRecord processRecord = (ProcessRecord) sparseArray.valueAt(i3);
                        if (i == -1 || processRecord.userId == i) {
                            if (processRecord.pkgList.contains(str)) {
                                Process.killProcess(processRecord.pid);
                            }
                        }
                    }
                    size = i2;
                }
            }
        }
    }

    public boolean isAppRunning(String str, int i) {
        boolean z;
        synchronized (this.mPidsSelfLocked) {
            int size = this.mPidsSelfLocked.size();
            while (true) {
                int i2 = size - 1;
                if (size <= 0) {
                    z = false;
                    break;
                }
                ProcessRecord processRecord = (ProcessRecord) this.mPidsSelfLocked.valueAt(i2);
                if (processRecord.userId == i && processRecord.info.packageName.equals(str)) {
                    z = true;
                    break;
                }
                size = i2;
            }
        }
        return z;
    }

    public void killApplicationProcess(String str, int i) {
        synchronized (this.mProcessNames) {
            ProcessRecord processRecord = (ProcessRecord) this.mProcessNames.get(str, i);
            if (processRecord != null) {
                Process.killProcess(processRecord.pid);
            }
        }
    }

    public String getInitialPackage(int i) {
        synchronized (this.mPidsSelfLocked) {
            ProcessRecord processRecord = (ProcessRecord) this.mPidsSelfLocked.get(i);
            if (processRecord == null) {
                return null;
            }
            String str = processRecord.info.packageName;
            return str;
        }
    }

    public void appDoneExecuting() {
        synchronized (this.mPidsSelfLocked) {
            ProcessRecord processRecord = (ProcessRecord) this.mPidsSelfLocked.get(VBinder.getCallingPid());
            if (processRecord != null) {
                processRecord.doneExecuting = true;
                processRecord.lock.open();
            }
        }
    }

    public ProcessRecord findProcessLocked(int i) {
        return (ProcessRecord) this.mPidsSelfLocked.get(i);
    }

    public ProcessRecord findProcessLocked(String str, int i) {
        return (ProcessRecord) this.mProcessNames.get(str, i);
    }

    public int stopUser(int i, IStopUserCallback.Stub stub) {
        synchronized (this.mPidsSelfLocked) {
            int size = this.mPidsSelfLocked.size();
            while (true) {
                int i2 = size - 1;
                if (size <= 0) {
                    break;
                }
                ProcessRecord processRecord = (ProcessRecord) this.mPidsSelfLocked.valueAt(i2);
                if (processRecord.userId == i) {
                    Process.killProcess(processRecord.pid);
                }
                size = i2;
            }
        }
        try {
            stub.userStopped(i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void sendOrderedBroadcastAsUser(Intent intent, VUserHandle vUserHandle, String str, BroadcastReceiver broadcastReceiver, Handler handler, int i, String str2, Bundle bundle) {
        Context context = VirtualCore.get().getContext();
        if (vUserHandle != null) {
            Intent intent2 = intent;
            intent.putExtra("_VA_|_user_id_", vUserHandle.getIdentifier());
        } else {
            Intent intent3 = intent;
        }
        context.sendOrderedBroadcast(intent, null, broadcastReceiver, handler, i, str2, bundle);
    }

    public void sendBroadcastAsUser(Intent intent, VUserHandle vUserHandle) {
        SpecialComponentList.protectIntent(intent);
        Context context = VirtualCore.get().getContext();
        if (vUserHandle != null) {
            intent.putExtra("_VA_|_user_id_", vUserHandle.getIdentifier());
        }
        context.sendBroadcast(intent);
    }

    public boolean bindServiceAsUser(Intent intent, ServiceConnection serviceConnection, int i, VUserHandle vUserHandle) {
        Intent intent2 = new Intent(intent);
        if (vUserHandle != null) {
            intent2.putExtra("_VA_|_user_id_", vUserHandle.getIdentifier());
        }
        return VirtualCore.get().getContext().bindService(intent2, serviceConnection, i);
    }

    public void sendBroadcastAsUser(Intent intent, VUserHandle vUserHandle, String str) {
        SpecialComponentList.protectIntent(intent);
        Context context = VirtualCore.get().getContext();
        if (vUserHandle != null) {
            intent.putExtra("_VA_|_user_id_", vUserHandle.getIdentifier());
        }
        context.sendBroadcast(intent);
    }

    /* access modifiers changed from: 0000 */
    public boolean handleStaticBroadcast(int i, ActivityInfo activityInfo, Intent intent, PendingResultData pendingResultData) {
        Intent intent2 = (Intent) intent.getParcelableExtra("_VA_|_intent_");
        ComponentName componentName = (ComponentName) intent.getParcelableExtra("_VA_|_component_");
        int intExtra = intent.getIntExtra("_VA_|_user_id_", -10000);
        if (intent2 == null) {
            return false;
        }
        if (intExtra < 0) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Sent a broadcast without userId ");
            sb.append(intent2);
            VLog.m91w(str, sb.toString(), new Object[0]);
            return false;
        }
        return handleUserBroadcast(VUserHandle.getUid(intExtra, i), activityInfo, componentName, intent2, pendingResultData);
    }

    private boolean handleUserBroadcast(int i, ActivityInfo activityInfo, ComponentName componentName, Intent intent, PendingResultData pendingResultData) {
        if (componentName != null && !ComponentUtils.toComponentName(activityInfo).equals(componentName)) {
            return false;
        }
        String unprotectAction = SpecialComponentList.unprotectAction(intent.getAction());
        if (unprotectAction != null) {
            intent.setAction(unprotectAction);
        }
        handleStaticBroadcastAsUser(i, activityInfo, intent, pendingResultData);
        return true;
    }

    private void handleStaticBroadcastAsUser(int i, ActivityInfo activityInfo, Intent intent, PendingResultData pendingResultData) {
        synchronized (this) {
            ProcessRecord findProcessLocked = findProcessLocked(activityInfo.processName, i);
            if (isStartProcessForBroadcast(activityInfo.processName, activityInfo.packageName) && findProcessLocked == null) {
                findProcessLocked = startProcessIfNeedLocked(activityInfo.processName, VUserHandle.getUserId(i), activityInfo.packageName);
            }
            if (!(findProcessLocked == null || findProcessLocked.appThread == null)) {
                performScheduleReceiver(findProcessLocked.client, i, activityInfo, intent, pendingResultData);
            }
        }
    }

    private static boolean isStartProcessForBroadcast(String str, String str2) {
        return Constants.PRIVILEGE_APP.contains(str2);
    }

    private void performScheduleReceiver(IVClient iVClient, int i, ActivityInfo activityInfo, Intent intent, PendingResultData pendingResultData) {
        ComponentName componentName = ComponentUtils.toComponentName(activityInfo);
        BroadcastSystem.get().broadcastSent(i, activityInfo, pendingResultData);
        try {
            iVClient.scheduleReceiver(activityInfo.processName, componentName, intent, pendingResultData);
        } catch (Throwable unused) {
            if (pendingResultData != null) {
                pendingResultData.finish();
            }
        }
    }

    public void broadcastFinish(PendingResultData pendingResultData) {
        BroadcastSystem.get().broadcastFinish(pendingResultData);
    }

    public void notifyBadgerChange(BadgerInfo badgerInfo) throws RemoteException {
        Intent intent = new Intent(VASettings.ACTION_BADGER_CHANGE);
        intent.putExtra(ChooseTypeAndAccountActivity.KEY_USER_ID, badgerInfo.userId);
        intent.putExtra("packageName", badgerInfo.packageName);
        intent.putExtra("badgerCount", badgerInfo.badgerCount);
        VirtualCore.get().getContext().sendBroadcast(intent);
    }
}
