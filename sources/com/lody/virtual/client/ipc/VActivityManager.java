package com.lody.virtual.client.ipc;

import android.app.Activity;
import android.app.IServiceConnection;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.client.hook.secondary.ServiceConnectionDelegate;
import com.lody.virtual.helper.compat.ActivityManagerCompat;
import com.lody.virtual.helper.utils.ComponentUtils;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.AppTaskInfo;
import com.lody.virtual.remote.BadgerInfo;
import com.lody.virtual.remote.PendingIntentData;
import com.lody.virtual.remote.PendingResultData;
import com.lody.virtual.remote.VParceledListSlice;
import com.lody.virtual.server.IActivityManager;
import com.lody.virtual.server.IActivityManager.Stub;
import com.lody.virtual.server.interfaces.IProcessObserver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mirror.android.app.ActivityThread;
import mirror.android.content.ContentProviderNative;

public class VActivityManager {
    private static final VActivityManager sAM = new VActivityManager();
    private final Map<IBinder, ActivityClientRecord> mActivities = new HashMap(6);
    private IActivityManager mRemote;

    public static VActivityManager get() {
        return sAM;
    }

    public IActivityManager getService() {
        if (this.mRemote == null || (!this.mRemote.asBinder().pingBinder() && !VirtualCore.get().isVAppProcess())) {
            synchronized (VActivityManager.class) {
                this.mRemote = (IActivityManager) LocalProxyUtils.genProxy(IActivityManager.class, getRemoteInterface());
            }
        }
        return this.mRemote;
    }

    private Object getRemoteInterface() {
        return Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.ACTIVITY));
    }

    public int startActivity(Intent intent, ActivityInfo activityInfo, IBinder iBinder, Bundle bundle, String str, int i, int i2) {
        try {
            return getService().startActivity(intent, activityInfo, iBinder, bundle, str, i, i2);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public int startActivities(Intent[] intentArr, String[] strArr, IBinder iBinder, Bundle bundle, int i) {
        try {
            return getService().startActivities(intentArr, strArr, iBinder, bundle, i);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public int startActivity(Intent intent, int i) {
        if (i < 0) {
            return -8;
        }
        ActivityInfo resolveActivityInfo = VirtualCore.get().resolveActivityInfo(intent, i);
        if (resolveActivityInfo == null) {
            return -1;
        }
        return startActivity(intent, resolveActivityInfo, null, null, null, 0, i);
    }

    public ActivityClientRecord onActivityCreate(ComponentName componentName, ComponentName componentName2, IBinder iBinder, ActivityInfo activityInfo, Intent intent, String str, int i, int i2, int i3) {
        ActivityClientRecord activityClientRecord = new ActivityClientRecord();
        activityClientRecord.info = activityInfo;
        IBinder iBinder2 = iBinder;
        this.mActivities.put(iBinder, activityClientRecord);
        try {
            getService().onActivityCreated(componentName, componentName2, iBinder, intent, str, i, i2, i3);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return activityClientRecord;
    }

    public ActivityClientRecord getActivityRecord(IBinder iBinder) {
        ActivityClientRecord activityClientRecord;
        synchronized (this.mActivities) {
            if (iBinder == null) {
                activityClientRecord = null;
            } else {
                activityClientRecord = (ActivityClientRecord) this.mActivities.get(iBinder);
            }
        }
        return activityClientRecord;
    }

    public void onActivityResumed(Activity activity) {
        onActivityResumed((IBinder) mirror.android.app.Activity.mToken.get(activity));
    }

    public void onActivityResumed(IBinder iBinder) {
        try {
            getService().onActivityResumed(VUserHandle.myUserId(), iBinder);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean onActivityDestroy(IBinder iBinder) {
        this.mActivities.remove(iBinder);
        try {
            return getService().onActivityDestroyed(VUserHandle.myUserId(), iBinder);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public AppTaskInfo getTaskInfo(int i) {
        try {
            return getService().getTaskInfo(i);
        } catch (RemoteException e) {
            return (AppTaskInfo) VirtualRuntime.crash(e);
        }
    }

    public ComponentName getCallingActivity(IBinder iBinder) {
        try {
            return getService().getCallingActivity(VUserHandle.myUserId(), iBinder);
        } catch (RemoteException e) {
            return (ComponentName) VirtualRuntime.crash(e);
        }
    }

    public String getCallingPackage(IBinder iBinder) {
        try {
            return getService().getCallingPackage(VUserHandle.myUserId(), iBinder);
        } catch (RemoteException e) {
            return (String) VirtualRuntime.crash(e);
        }
    }

    public String getPackageForToken(IBinder iBinder) {
        try {
            return getService().getPackageForToken(VUserHandle.myUserId(), iBinder);
        } catch (RemoteException e) {
            return (String) VirtualRuntime.crash(e);
        }
    }

    public ComponentName getActivityForToken(IBinder iBinder) {
        try {
            return getService().getActivityClassForToken(VUserHandle.myUserId(), iBinder);
        } catch (RemoteException e) {
            return (ComponentName) VirtualRuntime.crash(e);
        }
    }

    public ComponentName startService(IInterface iInterface, Intent intent, String str, int i) {
        try {
            return getService().startService(iInterface != null ? iInterface.asBinder() : null, intent, str, i);
        } catch (RemoteException e) {
            return (ComponentName) VirtualRuntime.crash(e);
        }
    }

    public int stopService(IInterface iInterface, Intent intent, String str) {
        try {
            return getService().stopService(iInterface != null ? iInterface.asBinder() : null, intent, str, VUserHandle.myUserId());
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public boolean stopServiceToken(ComponentName componentName, IBinder iBinder, int i) {
        try {
            return getService().stopServiceToken(componentName, iBinder, i, VUserHandle.myUserId());
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public void setServiceForeground(ComponentName componentName, IBinder iBinder, int i, Notification notification, boolean z) {
        try {
            getService().setServiceForeground(componentName, iBinder, i, notification, z, VUserHandle.myUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int bindService(Context context, Intent intent, ServiceConnection serviceConnection, int i) {
        try {
            return getService().bindService(null, null, intent, null, ServiceConnectionDelegate.getDelegate(context, serviceConnection, i), i, 0);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public boolean unbindService(Context context, ServiceConnection serviceConnection) {
        try {
            return getService().unbindService(ServiceConnectionDelegate.removeDelegate(context, serviceConnection), VUserHandle.myUserId());
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public int bindService(IBinder iBinder, IBinder iBinder2, Intent intent, String str, IServiceConnection iServiceConnection, int i, int i2) {
        try {
            return getService().bindService(iBinder, iBinder2, intent, str, iServiceConnection, i, i2);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public boolean unbindService(IServiceConnection iServiceConnection) {
        try {
            return getService().unbindService(iServiceConnection, VUserHandle.myUserId());
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public void unbindFinished(IBinder iBinder, Intent intent, boolean z) {
        try {
            getService().unbindFinished(iBinder, intent, z, VUserHandle.myUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void serviceDoneExecuting(IBinder iBinder, int i, int i2, int i3) {
        try {
            getService().serviceDoneExecuting(iBinder, i, i2, i3, VUserHandle.myUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public IBinder peekService(Intent intent, String str) {
        try {
            return getService().peekService(intent, str, VUserHandle.myUserId());
        } catch (RemoteException e) {
            return (IBinder) VirtualRuntime.crash(e);
        }
    }

    public void publishService(IBinder iBinder, Intent intent, IBinder iBinder2) {
        try {
            getService().publishService(iBinder, intent, iBinder2, VUserHandle.myUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public VParceledListSlice getServices(int i, int i2) {
        try {
            return getService().getServices(i, i2, VUserHandle.myUserId());
        } catch (RemoteException e) {
            return (VParceledListSlice) VirtualRuntime.crash(e);
        }
    }

    public void processRestarted(String str, String str2, int i) {
        try {
            getService().processRestarted(str, str2, i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getAppProcessName(int i) {
        try {
            return getService().getAppProcessName(i);
        } catch (RemoteException e) {
            return (String) VirtualRuntime.crash(e);
        }
    }

    public String getInitialPackage(int i) {
        try {
            return getService().getInitialPackage(i);
        } catch (RemoteException e) {
            return (String) VirtualRuntime.crash(e);
        }
    }

    public boolean isAppProcess(String str) {
        try {
            return getService().isAppProcess(str);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public void handleApplicationCrash() {
        try {
            getService().handleApplicationCrash();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void killAllApps() {
        try {
            getService().killAllApps();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void killApplicationProcess(String str, int i) {
        try {
            getService().killApplicationProcess(str, i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void registerProcessObserver(IProcessObserver iProcessObserver) {
        try {
            getService().registerProcessObserver(iProcessObserver);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void killAppByPkg(String str, int i) {
        try {
            getService().killAppByPkg(str, i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregisterProcessObserver(IProcessObserver iProcessObserver) {
        try {
            getService().unregisterProcessObserver(iProcessObserver);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void appDoneExecuting() {
        try {
            getService().appDoneExecuting();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public List<String> getProcessPkgList(int i) {
        try {
            return getService().getProcessPkgList(i);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public boolean isAppPid(int i) {
        try {
            return getService().isAppPid(i);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public int getUidByPid(int i) {
        try {
            return getService().getUidByPid(i);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public int getSystemPid() {
        try {
            return getService().getSystemPid();
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public void sendActivityResult(IBinder iBinder, String str, int i) {
        ActivityClientRecord activityClientRecord = (ActivityClientRecord) this.mActivities.get(iBinder);
        if (activityClientRecord != null && activityClientRecord.activity != null) {
            Object mainThread = VirtualCore.mainThread();
            ActivityThread.sendActivityResult.call(mainThread, iBinder, str, Integer.valueOf(i), Integer.valueOf(0), null);
        }
    }

    public IInterface acquireProviderClient(int i, ProviderInfo providerInfo) throws RemoteException {
        return (IInterface) ContentProviderNative.asInterface.call(getService().acquireProviderClient(i, providerInfo));
    }

    public PendingIntentData getPendingIntent(IBinder iBinder) throws RemoteException {
        return getService().getPendingIntent(iBinder);
    }

    public void addPendingIntent(IBinder iBinder, String str) throws RemoteException {
        getService().addPendingIntent(iBinder, str);
    }

    public void removePendingIntent(IBinder iBinder) throws RemoteException {
        getService().removePendingIntent(iBinder);
    }

    public void finishActivity(IBinder iBinder) {
        ActivityClientRecord activityRecord = getActivityRecord(iBinder);
        if (activityRecord != null) {
            Activity activity = activityRecord.activity;
            while (true) {
                Activity activity2 = (Activity) mirror.android.app.Activity.mParent.get(activity);
                if (activity2 == null) {
                    break;
                }
                activity = activity2;
            }
            if (!mirror.android.app.Activity.mFinished.get(activity)) {
                ActivityManagerCompat.finishActivity(iBinder, mirror.android.app.Activity.mResultCode.get(activity), (Intent) mirror.android.app.Activity.mResultData.get(activity));
                mirror.android.app.Activity.mFinished.set(activity, true);
            }
        }
    }

    public boolean isAppRunning(String str, int i) {
        try {
            return getService().isAppRunning(str, i);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public int initProcess(String str, String str2, int i) {
        try {
            return getService().initProcess(str, str2, i);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public void sendBroadcast(Intent intent, int i) {
        Intent redirectBroadcastIntent = ComponentUtils.redirectBroadcastIntent(intent, i);
        if (redirectBroadcastIntent != null) {
            VirtualCore.get().getContext().sendBroadcast(redirectBroadcastIntent);
        }
    }

    public boolean isVAServiceToken(IBinder iBinder) {
        try {
            return getService().isVAServiceToken(iBinder);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public void broadcastFinish(PendingResultData pendingResultData) {
        try {
            getService().broadcastFinish(pendingResultData);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public String getPackageForIntentSender(IBinder iBinder) {
        try {
            return getService().getPackageForIntentSender(iBinder);
        } catch (RemoteException e) {
            return (String) VirtualRuntime.crash(e);
        }
    }

    public void notifyBadgerChange(BadgerInfo badgerInfo) {
        try {
            getService().notifyBadgerChange(badgerInfo);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }
}
