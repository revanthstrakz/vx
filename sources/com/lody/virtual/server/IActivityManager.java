package com.lody.virtual.server;

import android.app.IServiceConnection;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ProviderInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.lody.virtual.remote.AppTaskInfo;
import com.lody.virtual.remote.BadgerInfo;
import com.lody.virtual.remote.PendingIntentData;
import com.lody.virtual.remote.PendingResultData;
import com.lody.virtual.remote.VParceledListSlice;
import com.lody.virtual.server.interfaces.IProcessObserver;
import java.util.List;

public interface IActivityManager extends IInterface {

    public static abstract class Stub extends Binder implements IActivityManager {
        private static final String DESCRIPTOR = "com.lody.virtual.server.IActivityManager";
        static final int TRANSACTION_acquireProviderClient = 41;
        static final int TRANSACTION_addPendingIntent = 43;
        static final int TRANSACTION_appDoneExecuting = 18;
        static final int TRANSACTION_bindService = 34;
        static final int TRANSACTION_broadcastFinish = 47;
        static final int TRANSACTION_dump = 13;
        static final int TRANSACTION_getActivityClassForToken = 24;
        static final int TRANSACTION_getAppProcessName = 8;
        static final int TRANSACTION_getCallingActivity = 26;
        static final int TRANSACTION_getCallingPackage = 25;
        static final int TRANSACTION_getFreeStubCount = 2;
        static final int TRANSACTION_getInitialPackage = 16;
        static final int TRANSACTION_getPackageForIntentSender = 45;
        static final int TRANSACTION_getPackageForToken = 28;
        static final int TRANSACTION_getPendingIntent = 42;
        static final int TRANSACTION_getProcessPkgList = 9;
        static final int TRANSACTION_getServices = 40;
        static final int TRANSACTION_getSystemPid = 3;
        static final int TRANSACTION_getTaskInfo = 27;
        static final int TRANSACTION_getUidByPid = 4;
        static final int TRANSACTION_handleApplicationCrash = 17;
        static final int TRANSACTION_initProcess = 1;
        static final int TRANSACTION_isAppPid = 7;
        static final int TRANSACTION_isAppProcess = 5;
        static final int TRANSACTION_isAppRunning = 6;
        static final int TRANSACTION_isVAServiceToken = 29;
        static final int TRANSACTION_killAllApps = 10;
        static final int TRANSACTION_killAppByPkg = 11;
        static final int TRANSACTION_killApplicationProcess = 12;
        static final int TRANSACTION_notifyBadgerChange = 48;
        static final int TRANSACTION_onActivityCreated = 21;
        static final int TRANSACTION_onActivityDestroyed = 23;
        static final int TRANSACTION_onActivityResumed = 22;
        static final int TRANSACTION_peekService = 38;
        static final int TRANSACTION_processRestarted = 46;
        static final int TRANSACTION_publishService = 39;
        static final int TRANSACTION_registerProcessObserver = 14;
        static final int TRANSACTION_removePendingIntent = 44;
        static final int TRANSACTION_serviceDoneExecuting = 37;
        static final int TRANSACTION_setServiceForeground = 33;
        static final int TRANSACTION_startActivities = 19;
        static final int TRANSACTION_startActivity = 20;
        static final int TRANSACTION_startService = 30;
        static final int TRANSACTION_stopService = 31;
        static final int TRANSACTION_stopServiceToken = 32;
        static final int TRANSACTION_unbindFinished = 36;
        static final int TRANSACTION_unbindService = 35;
        static final int TRANSACTION_unregisterProcessObserver = 15;

        private static class Proxy implements IActivityManager {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public int initProcess(String str, String str2, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getFreeStubCount() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getSystemPid() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getUidByPid(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isAppProcess(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z = false;
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isAppRunning(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    boolean z = false;
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isAppPid(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    boolean z = false;
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getAppProcessName(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<String> getProcessPkgList(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createStringArrayList();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void killAllApps() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void killAppByPkg(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void killApplicationProcess(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void dump() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void registerProcessObserver(IProcessObserver iProcessObserver) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iProcessObserver != null ? iProcessObserver.asBinder() : null);
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unregisterProcessObserver(IProcessObserver iProcessObserver) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iProcessObserver != null ? iProcessObserver.asBinder() : null);
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInitialPackage(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void handleApplicationCrash() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void appDoneExecuting() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int startActivities(Intent[] intentArr, String[] strArr, IBinder iBinder, Bundle bundle, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedArray(intentArr, 0);
                    obtain.writeStringArray(strArr);
                    obtain.writeStrongBinder(iBinder);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i);
                    this.mRemote.transact(19, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int startActivity(Intent intent, ActivityInfo activityInfo, IBinder iBinder, Bundle bundle, String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (activityInfo != null) {
                        obtain.writeInt(1);
                        activityInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iBinder);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(20, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onActivityCreated(ComponentName componentName, ComponentName componentName2, IBinder iBinder, Intent intent, String str, int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (componentName2 != null) {
                        obtain.writeInt(1);
                        componentName2.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iBinder);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    this.mRemote.transact(21, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void onActivityResumed(int i, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(22, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean onActivityDestroyed(int i, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBinder);
                    boolean z = false;
                    this.mRemote.transact(23, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ComponentName getActivityClassForToken(int i, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(24, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getCallingPackage(int i, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(25, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ComponentName getCallingActivity(int i, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(26, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public AppTaskInfo getTaskInfo(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(27, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (AppTaskInfo) AppTaskInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getPackageForToken(int i, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(28, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isVAServiceToken(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    boolean z = false;
                    this.mRemote.transact(29, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ComponentName startService(IBinder iBinder, Intent intent, String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(30, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int stopService(IBinder iBinder, Intent intent, String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(31, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean stopServiceToken(ComponentName componentName, IBinder iBinder, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = true;
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(32, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setServiceForeground(ComponentName componentName, IBinder iBinder, int i, Notification notification, boolean z, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeInt(i);
                    if (notification != null) {
                        obtain.writeInt(1);
                        notification.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeInt(i2);
                    this.mRemote.transact(33, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int bindService(IBinder iBinder, IBinder iBinder2, Intent intent, String str, IServiceConnection iServiceConnection, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeStrongBinder(iBinder2);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iServiceConnection != null ? iServiceConnection.asBinder() : null);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(34, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean unbindService(IServiceConnection iServiceConnection, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iServiceConnection != null ? iServiceConnection.asBinder() : null);
                    obtain.writeInt(i);
                    boolean z = false;
                    this.mRemote.transact(35, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unbindFinished(IBinder iBinder, Intent intent, boolean z, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeInt(i);
                    this.mRemote.transact(36, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void serviceDoneExecuting(IBinder iBinder, int i, int i2, int i3, int i4) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    obtain.writeInt(i4);
                    this.mRemote.transact(37, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder peekService(Intent intent, String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(38, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void publishService(IBinder iBinder, Intent intent, IBinder iBinder2, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iBinder2);
                    obtain.writeInt(i);
                    this.mRemote.transact(39, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public VParceledListSlice getServices(int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    this.mRemote.transact(40, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (VParceledListSlice) VParceledListSlice.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder acquireProviderClient(int i, ProviderInfo providerInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (providerInfo != null) {
                        obtain.writeInt(1);
                        providerInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(41, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public PendingIntentData getPendingIntent(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(42, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (PendingIntentData) PendingIntentData.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void addPendingIntent(IBinder iBinder, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeString(str);
                    this.mRemote.transact(43, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void removePendingIntent(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(44, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getPackageForIntentSender(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(45, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void processRestarted(String str, String str2, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    this.mRemote.transact(46, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void broadcastFinish(PendingResultData pendingResultData) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (pendingResultData != null) {
                        obtain.writeInt(1);
                        pendingResultData.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(47, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void notifyBadgerChange(BadgerInfo badgerInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (badgerInfo != null) {
                        obtain.writeInt(1);
                        badgerInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(48, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IActivityManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IActivityManager)) {
                return new Proxy(iBinder);
            }
            return (IActivityManager) queryLocalInterface;
        }

        /* JADX WARNING: type inference failed for: r4v0 */
        /* JADX WARNING: type inference failed for: r4v1, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r0v32, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r4v2 */
        /* JADX WARNING: type inference failed for: r4v3, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r0v40, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r4v4 */
        /* JADX WARNING: type inference failed for: r4v5, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r0v53, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v6 */
        /* JADX WARNING: type inference failed for: r4v7, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v9, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v10, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v12, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v13, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r4v15, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r4v16, types: [android.app.Notification] */
        /* JADX WARNING: type inference failed for: r0v90, types: [android.app.Notification] */
        /* JADX WARNING: type inference failed for: r4v17 */
        /* JADX WARNING: type inference failed for: r4v18 */
        /* JADX WARNING: type inference failed for: r3v14, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r0v100, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v20 */
        /* JADX WARNING: type inference failed for: r4v21, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v23, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v26, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v28, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v29, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v31, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r4v32, types: [android.content.pm.ProviderInfo] */
        /* JADX WARNING: type inference failed for: r4v34, types: [android.content.pm.ProviderInfo] */
        /* JADX WARNING: type inference failed for: r4v35, types: [com.lody.virtual.remote.PendingResultData] */
        /* JADX WARNING: type inference failed for: r4v37, types: [com.lody.virtual.remote.PendingResultData] */
        /* JADX WARNING: type inference failed for: r4v38, types: [com.lody.virtual.remote.BadgerInfo] */
        /* JADX WARNING: type inference failed for: r4v40, types: [com.lody.virtual.remote.BadgerInfo] */
        /* JADX WARNING: type inference failed for: r4v41 */
        /* JADX WARNING: type inference failed for: r4v42 */
        /* JADX WARNING: type inference failed for: r4v43 */
        /* JADX WARNING: type inference failed for: r4v44 */
        /* JADX WARNING: type inference failed for: r4v45 */
        /* JADX WARNING: type inference failed for: r4v46 */
        /* JADX WARNING: type inference failed for: r4v47 */
        /* JADX WARNING: type inference failed for: r4v48 */
        /* JADX WARNING: type inference failed for: r4v49 */
        /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r4v0
          assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], ?[OBJECT, ARRAY], android.content.Intent, android.content.ComponentName, android.content.pm.ProviderInfo, com.lody.virtual.remote.PendingResultData, com.lody.virtual.remote.BadgerInfo]
          uses: [android.os.Bundle, android.content.Intent, android.content.ComponentName, android.app.Notification, ?[OBJECT, ARRAY], android.content.pm.ProviderInfo, com.lody.virtual.remote.PendingResultData, com.lody.virtual.remote.BadgerInfo]
          mth insns count: 522
        	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$0(DepthTraversal.java:13)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
        	at jadx.core.ProcessClass.process(ProcessClass.java:30)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
        	at jadx.core.ProcessClass.process(ProcessClass.java:35)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
         */
        /* JADX WARNING: Unknown variable types count: 16 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r14, android.os.Parcel r15, android.os.Parcel r16, int r17) throws android.os.RemoteException {
            /*
                r13 = this;
                r9 = r13
                r0 = r14
                r1 = r15
                r10 = r16
                java.lang.String r2 = "com.lody.virtual.server.IActivityManager"
                r3 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r11 = 1
                if (r0 == r3) goto L_0x0575
                r3 = 0
                r4 = 0
                switch(r0) {
                    case 1: goto L_0x055b;
                    case 2: goto L_0x054d;
                    case 3: goto L_0x053f;
                    case 4: goto L_0x052d;
                    case 5: goto L_0x051b;
                    case 6: goto L_0x0505;
                    case 7: goto L_0x04f3;
                    case 8: goto L_0x04e1;
                    case 9: goto L_0x04cf;
                    case 10: goto L_0x04c5;
                    case 11: goto L_0x04b3;
                    case 12: goto L_0x04a1;
                    case 13: goto L_0x0497;
                    case 14: goto L_0x0485;
                    case 15: goto L_0x0473;
                    case 16: goto L_0x0461;
                    case 17: goto L_0x0457;
                    case 18: goto L_0x044d;
                    case 19: goto L_0x0416;
                    case 20: goto L_0x03c0;
                    case 21: goto L_0x0369;
                    case 22: goto L_0x0357;
                    case 23: goto L_0x0341;
                    case 24: goto L_0x0322;
                    case 25: goto L_0x030c;
                    case 26: goto L_0x02ed;
                    case 27: goto L_0x02d2;
                    case 28: goto L_0x02bc;
                    case 29: goto L_0x02aa;
                    case 30: goto L_0x0278;
                    case 31: goto L_0x024f;
                    case 32: goto L_0x0226;
                    case 33: goto L_0x01e1;
                    case 34: goto L_0x01a0;
                    case 35: goto L_0x0186;
                    case 36: goto L_0x015e;
                    case 37: goto L_0x013a;
                    case 38: goto L_0x0115;
                    case 39: goto L_0x00f0;
                    case 40: goto L_0x00cd;
                    case 41: goto L_0x00ac;
                    case 42: goto L_0x0091;
                    case 43: goto L_0x007f;
                    case 44: goto L_0x0071;
                    case 45: goto L_0x005f;
                    case 46: goto L_0x0049;
                    case 47: goto L_0x0030;
                    case 48: goto L_0x0017;
                    default: goto L_0x0012;
                }
            L_0x0012:
                boolean r0 = super.onTransact(r14, r15, r16, r17)
                return r0
            L_0x0017:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x0029
                android.os.Parcelable$Creator<com.lody.virtual.remote.BadgerInfo> r0 = com.lody.virtual.remote.BadgerInfo.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                r4 = r0
                com.lody.virtual.remote.BadgerInfo r4 = (com.lody.virtual.remote.BadgerInfo) r4
            L_0x0029:
                r13.notifyBadgerChange(r4)
                r16.writeNoException()
                return r11
            L_0x0030:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x0042
                android.os.Parcelable$Creator<com.lody.virtual.remote.PendingResultData> r0 = com.lody.virtual.remote.PendingResultData.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                r4 = r0
                com.lody.virtual.remote.PendingResultData r4 = (com.lody.virtual.remote.PendingResultData) r4
            L_0x0042:
                r13.broadcastFinish(r4)
                r16.writeNoException()
                return r11
            L_0x0049:
                r15.enforceInterface(r2)
                java.lang.String r0 = r15.readString()
                java.lang.String r2 = r15.readString()
                int r1 = r15.readInt()
                r13.processRestarted(r0, r2, r1)
                r16.writeNoException()
                return r11
            L_0x005f:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                java.lang.String r0 = r13.getPackageForIntentSender(r0)
                r16.writeNoException()
                r10.writeString(r0)
                return r11
            L_0x0071:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                r13.removePendingIntent(r0)
                r16.writeNoException()
                return r11
            L_0x007f:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                java.lang.String r1 = r15.readString()
                r13.addPendingIntent(r0, r1)
                r16.writeNoException()
                return r11
            L_0x0091:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                com.lody.virtual.remote.PendingIntentData r0 = r13.getPendingIntent(r0)
                r16.writeNoException()
                if (r0 == 0) goto L_0x00a8
                r10.writeInt(r11)
                r0.writeToParcel(r10, r11)
                goto L_0x00ab
            L_0x00a8:
                r10.writeInt(r3)
            L_0x00ab:
                return r11
            L_0x00ac:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                int r2 = r15.readInt()
                if (r2 == 0) goto L_0x00c2
                android.os.Parcelable$Creator r2 = android.content.pm.ProviderInfo.CREATOR
                java.lang.Object r1 = r2.createFromParcel(r15)
                r4 = r1
                android.content.pm.ProviderInfo r4 = (android.content.pm.ProviderInfo) r4
            L_0x00c2:
                android.os.IBinder r0 = r13.acquireProviderClient(r0, r4)
                r16.writeNoException()
                r10.writeStrongBinder(r0)
                return r11
            L_0x00cd:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                int r2 = r15.readInt()
                int r1 = r15.readInt()
                com.lody.virtual.remote.VParceledListSlice r0 = r13.getServices(r0, r2, r1)
                r16.writeNoException()
                if (r0 == 0) goto L_0x00ec
                r10.writeInt(r11)
                r0.writeToParcel(r10, r11)
                goto L_0x00ef
            L_0x00ec:
                r10.writeInt(r3)
            L_0x00ef:
                return r11
            L_0x00f0:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                int r2 = r15.readInt()
                if (r2 == 0) goto L_0x0106
                android.os.Parcelable$Creator r2 = android.content.Intent.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r15)
                r4 = r2
                android.content.Intent r4 = (android.content.Intent) r4
            L_0x0106:
                android.os.IBinder r2 = r15.readStrongBinder()
                int r1 = r15.readInt()
                r13.publishService(r0, r4, r2, r1)
                r16.writeNoException()
                return r11
            L_0x0115:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x0127
                android.os.Parcelable$Creator r0 = android.content.Intent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                r4 = r0
                android.content.Intent r4 = (android.content.Intent) r4
            L_0x0127:
                java.lang.String r0 = r15.readString()
                int r1 = r15.readInt()
                android.os.IBinder r0 = r13.peekService(r4, r0, r1)
                r16.writeNoException()
                r10.writeStrongBinder(r0)
                return r11
            L_0x013a:
                r15.enforceInterface(r2)
                android.os.IBinder r2 = r15.readStrongBinder()
                int r3 = r15.readInt()
                int r4 = r15.readInt()
                int r5 = r15.readInt()
                int r6 = r15.readInt()
                r0 = r13
                r1 = r2
                r2 = r3
                r3 = r4
                r4 = r5
                r5 = r6
                r0.serviceDoneExecuting(r1, r2, r3, r4, r5)
                r16.writeNoException()
                return r11
            L_0x015e:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                int r2 = r15.readInt()
                if (r2 == 0) goto L_0x0174
                android.os.Parcelable$Creator r2 = android.content.Intent.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r15)
                r4 = r2
                android.content.Intent r4 = (android.content.Intent) r4
            L_0x0174:
                int r2 = r15.readInt()
                if (r2 == 0) goto L_0x017b
                r3 = 1
            L_0x017b:
                int r1 = r15.readInt()
                r13.unbindFinished(r0, r4, r3, r1)
                r16.writeNoException()
                return r11
            L_0x0186:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                android.app.IServiceConnection r0 = android.app.IServiceConnection.Stub.asInterface(r0)
                int r1 = r15.readInt()
                boolean r0 = r13.unbindService(r0, r1)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x01a0:
                r15.enforceInterface(r2)
                android.os.IBinder r2 = r15.readStrongBinder()
                android.os.IBinder r3 = r15.readStrongBinder()
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x01ba
                android.os.Parcelable$Creator r0 = android.content.Intent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                android.content.Intent r0 = (android.content.Intent) r0
                r4 = r0
            L_0x01ba:
                java.lang.String r5 = r15.readString()
                android.os.IBinder r0 = r15.readStrongBinder()
                android.app.IServiceConnection r6 = android.app.IServiceConnection.Stub.asInterface(r0)
                int r7 = r15.readInt()
                int r8 = r15.readInt()
                r0 = r13
                r1 = r2
                r2 = r3
                r3 = r4
                r4 = r5
                r5 = r6
                r6 = r7
                r7 = r8
                int r0 = r0.bindService(r1, r2, r3, r4, r5, r6, r7)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x01e1:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x01f4
                android.os.Parcelable$Creator r0 = android.content.ComponentName.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                android.content.ComponentName r0 = (android.content.ComponentName) r0
                r2 = r0
                goto L_0x01f5
            L_0x01f4:
                r2 = r4
            L_0x01f5:
                android.os.IBinder r5 = r15.readStrongBinder()
                int r6 = r15.readInt()
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x020c
                android.os.Parcelable$Creator r0 = android.app.Notification.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                android.app.Notification r0 = (android.app.Notification) r0
                r4 = r0
            L_0x020c:
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x0214
                r7 = 1
                goto L_0x0215
            L_0x0214:
                r7 = 0
            L_0x0215:
                int r8 = r15.readInt()
                r0 = r13
                r1 = r2
                r2 = r5
                r3 = r6
                r5 = r7
                r6 = r8
                r0.setServiceForeground(r1, r2, r3, r4, r5, r6)
                r16.writeNoException()
                return r11
            L_0x0226:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x0238
                android.os.Parcelable$Creator r0 = android.content.ComponentName.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                r4 = r0
                android.content.ComponentName r4 = (android.content.ComponentName) r4
            L_0x0238:
                android.os.IBinder r0 = r15.readStrongBinder()
                int r2 = r15.readInt()
                int r1 = r15.readInt()
                boolean r0 = r13.stopServiceToken(r4, r0, r2, r1)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x024f:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                int r2 = r15.readInt()
                if (r2 == 0) goto L_0x0265
                android.os.Parcelable$Creator r2 = android.content.Intent.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r15)
                r4 = r2
                android.content.Intent r4 = (android.content.Intent) r4
            L_0x0265:
                java.lang.String r2 = r15.readString()
                int r1 = r15.readInt()
                int r0 = r13.stopService(r0, r4, r2, r1)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x0278:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                int r2 = r15.readInt()
                if (r2 == 0) goto L_0x028e
                android.os.Parcelable$Creator r2 = android.content.Intent.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r15)
                r4 = r2
                android.content.Intent r4 = (android.content.Intent) r4
            L_0x028e:
                java.lang.String r2 = r15.readString()
                int r1 = r15.readInt()
                android.content.ComponentName r0 = r13.startService(r0, r4, r2, r1)
                r16.writeNoException()
                if (r0 == 0) goto L_0x02a6
                r10.writeInt(r11)
                r0.writeToParcel(r10, r11)
                goto L_0x02a9
            L_0x02a6:
                r10.writeInt(r3)
            L_0x02a9:
                return r11
            L_0x02aa:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                boolean r0 = r13.isVAServiceToken(r0)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x02bc:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                android.os.IBinder r1 = r15.readStrongBinder()
                java.lang.String r0 = r13.getPackageForToken(r0, r1)
                r16.writeNoException()
                r10.writeString(r0)
                return r11
            L_0x02d2:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                com.lody.virtual.remote.AppTaskInfo r0 = r13.getTaskInfo(r0)
                r16.writeNoException()
                if (r0 == 0) goto L_0x02e9
                r10.writeInt(r11)
                r0.writeToParcel(r10, r11)
                goto L_0x02ec
            L_0x02e9:
                r10.writeInt(r3)
            L_0x02ec:
                return r11
            L_0x02ed:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                android.os.IBinder r1 = r15.readStrongBinder()
                android.content.ComponentName r0 = r13.getCallingActivity(r0, r1)
                r16.writeNoException()
                if (r0 == 0) goto L_0x0308
                r10.writeInt(r11)
                r0.writeToParcel(r10, r11)
                goto L_0x030b
            L_0x0308:
                r10.writeInt(r3)
            L_0x030b:
                return r11
            L_0x030c:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                android.os.IBinder r1 = r15.readStrongBinder()
                java.lang.String r0 = r13.getCallingPackage(r0, r1)
                r16.writeNoException()
                r10.writeString(r0)
                return r11
            L_0x0322:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                android.os.IBinder r1 = r15.readStrongBinder()
                android.content.ComponentName r0 = r13.getActivityClassForToken(r0, r1)
                r16.writeNoException()
                if (r0 == 0) goto L_0x033d
                r10.writeInt(r11)
                r0.writeToParcel(r10, r11)
                goto L_0x0340
            L_0x033d:
                r10.writeInt(r3)
            L_0x0340:
                return r11
            L_0x0341:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                android.os.IBinder r1 = r15.readStrongBinder()
                boolean r0 = r13.onActivityDestroyed(r0, r1)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x0357:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                android.os.IBinder r1 = r15.readStrongBinder()
                r13.onActivityResumed(r0, r1)
                r16.writeNoException()
                return r11
            L_0x0369:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x037c
                android.os.Parcelable$Creator r0 = android.content.ComponentName.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                android.content.ComponentName r0 = (android.content.ComponentName) r0
                r2 = r0
                goto L_0x037d
            L_0x037c:
                r2 = r4
            L_0x037d:
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x038d
                android.os.Parcelable$Creator r0 = android.content.ComponentName.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                android.content.ComponentName r0 = (android.content.ComponentName) r0
                r3 = r0
                goto L_0x038e
            L_0x038d:
                r3 = r4
            L_0x038e:
                android.os.IBinder r5 = r15.readStrongBinder()
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x03a1
                android.os.Parcelable$Creator r0 = android.content.Intent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                android.content.Intent r0 = (android.content.Intent) r0
                r4 = r0
            L_0x03a1:
                java.lang.String r6 = r15.readString()
                int r7 = r15.readInt()
                int r8 = r15.readInt()
                int r12 = r15.readInt()
                r0 = r13
                r1 = r2
                r2 = r3
                r3 = r5
                r5 = r6
                r6 = r7
                r7 = r8
                r8 = r12
                r0.onActivityCreated(r1, r2, r3, r4, r5, r6, r7, r8)
                r16.writeNoException()
                return r11
            L_0x03c0:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x03d3
                android.os.Parcelable$Creator r0 = android.content.Intent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                android.content.Intent r0 = (android.content.Intent) r0
                r2 = r0
                goto L_0x03d4
            L_0x03d3:
                r2 = r4
            L_0x03d4:
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x03e4
                android.os.Parcelable$Creator r0 = android.content.pm.ActivityInfo.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                android.content.pm.ActivityInfo r0 = (android.content.pm.ActivityInfo) r0
                r3 = r0
                goto L_0x03e5
            L_0x03e4:
                r3 = r4
            L_0x03e5:
                android.os.IBinder r5 = r15.readStrongBinder()
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x03f8
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r4 = r0
            L_0x03f8:
                java.lang.String r6 = r15.readString()
                int r7 = r15.readInt()
                int r8 = r15.readInt()
                r0 = r13
                r1 = r2
                r2 = r3
                r3 = r5
                r5 = r6
                r6 = r7
                r7 = r8
                int r0 = r0.startActivity(r1, r2, r3, r4, r5, r6, r7)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x0416:
                r15.enforceInterface(r2)
                android.os.Parcelable$Creator r0 = android.content.Intent.CREATOR
                java.lang.Object[] r0 = r15.createTypedArray(r0)
                r2 = r0
                android.content.Intent[] r2 = (android.content.Intent[]) r2
                java.lang.String[] r3 = r15.createStringArray()
                android.os.IBinder r5 = r15.readStrongBinder()
                int r0 = r15.readInt()
                if (r0 == 0) goto L_0x0439
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r15)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r4 = r0
            L_0x0439:
                int r6 = r15.readInt()
                r0 = r13
                r1 = r2
                r2 = r3
                r3 = r5
                r5 = r6
                int r0 = r0.startActivities(r1, r2, r3, r4, r5)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x044d:
                r15.enforceInterface(r2)
                r13.appDoneExecuting()
                r16.writeNoException()
                return r11
            L_0x0457:
                r15.enforceInterface(r2)
                r13.handleApplicationCrash()
                r16.writeNoException()
                return r11
            L_0x0461:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                java.lang.String r0 = r13.getInitialPackage(r0)
                r16.writeNoException()
                r10.writeString(r0)
                return r11
            L_0x0473:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                com.lody.virtual.server.interfaces.IProcessObserver r0 = com.lody.virtual.server.interfaces.IProcessObserver.Stub.asInterface(r0)
                r13.unregisterProcessObserver(r0)
                r16.writeNoException()
                return r11
            L_0x0485:
                r15.enforceInterface(r2)
                android.os.IBinder r0 = r15.readStrongBinder()
                com.lody.virtual.server.interfaces.IProcessObserver r0 = com.lody.virtual.server.interfaces.IProcessObserver.Stub.asInterface(r0)
                r13.registerProcessObserver(r0)
                r16.writeNoException()
                return r11
            L_0x0497:
                r15.enforceInterface(r2)
                r13.dump()
                r16.writeNoException()
                return r11
            L_0x04a1:
                r15.enforceInterface(r2)
                java.lang.String r0 = r15.readString()
                int r1 = r15.readInt()
                r13.killApplicationProcess(r0, r1)
                r16.writeNoException()
                return r11
            L_0x04b3:
                r15.enforceInterface(r2)
                java.lang.String r0 = r15.readString()
                int r1 = r15.readInt()
                r13.killAppByPkg(r0, r1)
                r16.writeNoException()
                return r11
            L_0x04c5:
                r15.enforceInterface(r2)
                r13.killAllApps()
                r16.writeNoException()
                return r11
            L_0x04cf:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                java.util.List r0 = r13.getProcessPkgList(r0)
                r16.writeNoException()
                r10.writeStringList(r0)
                return r11
            L_0x04e1:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                java.lang.String r0 = r13.getAppProcessName(r0)
                r16.writeNoException()
                r10.writeString(r0)
                return r11
            L_0x04f3:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                boolean r0 = r13.isAppPid(r0)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x0505:
                r15.enforceInterface(r2)
                java.lang.String r0 = r15.readString()
                int r1 = r15.readInt()
                boolean r0 = r13.isAppRunning(r0, r1)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x051b:
                r15.enforceInterface(r2)
                java.lang.String r0 = r15.readString()
                boolean r0 = r13.isAppProcess(r0)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x052d:
                r15.enforceInterface(r2)
                int r0 = r15.readInt()
                int r0 = r13.getUidByPid(r0)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x053f:
                r15.enforceInterface(r2)
                int r0 = r13.getSystemPid()
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x054d:
                r15.enforceInterface(r2)
                int r0 = r13.getFreeStubCount()
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x055b:
                r15.enforceInterface(r2)
                java.lang.String r0 = r15.readString()
                java.lang.String r2 = r15.readString()
                int r1 = r15.readInt()
                int r0 = r13.initProcess(r0, r2, r1)
                r16.writeNoException()
                r10.writeInt(r0)
                return r11
            L_0x0575:
                r10.writeString(r2)
                return r11
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.IActivityManager.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    IBinder acquireProviderClient(int i, ProviderInfo providerInfo) throws RemoteException;

    void addPendingIntent(IBinder iBinder, String str) throws RemoteException;

    void appDoneExecuting() throws RemoteException;

    int bindService(IBinder iBinder, IBinder iBinder2, Intent intent, String str, IServiceConnection iServiceConnection, int i, int i2) throws RemoteException;

    void broadcastFinish(PendingResultData pendingResultData) throws RemoteException;

    void dump() throws RemoteException;

    ComponentName getActivityClassForToken(int i, IBinder iBinder) throws RemoteException;

    String getAppProcessName(int i) throws RemoteException;

    ComponentName getCallingActivity(int i, IBinder iBinder) throws RemoteException;

    String getCallingPackage(int i, IBinder iBinder) throws RemoteException;

    int getFreeStubCount() throws RemoteException;

    String getInitialPackage(int i) throws RemoteException;

    String getPackageForIntentSender(IBinder iBinder) throws RemoteException;

    String getPackageForToken(int i, IBinder iBinder) throws RemoteException;

    PendingIntentData getPendingIntent(IBinder iBinder) throws RemoteException;

    List<String> getProcessPkgList(int i) throws RemoteException;

    VParceledListSlice getServices(int i, int i2, int i3) throws RemoteException;

    int getSystemPid() throws RemoteException;

    AppTaskInfo getTaskInfo(int i) throws RemoteException;

    int getUidByPid(int i) throws RemoteException;

    void handleApplicationCrash() throws RemoteException;

    int initProcess(String str, String str2, int i) throws RemoteException;

    boolean isAppPid(int i) throws RemoteException;

    boolean isAppProcess(String str) throws RemoteException;

    boolean isAppRunning(String str, int i) throws RemoteException;

    boolean isVAServiceToken(IBinder iBinder) throws RemoteException;

    void killAllApps() throws RemoteException;

    void killAppByPkg(String str, int i) throws RemoteException;

    void killApplicationProcess(String str, int i) throws RemoteException;

    void notifyBadgerChange(BadgerInfo badgerInfo) throws RemoteException;

    void onActivityCreated(ComponentName componentName, ComponentName componentName2, IBinder iBinder, Intent intent, String str, int i, int i2, int i3) throws RemoteException;

    boolean onActivityDestroyed(int i, IBinder iBinder) throws RemoteException;

    void onActivityResumed(int i, IBinder iBinder) throws RemoteException;

    IBinder peekService(Intent intent, String str, int i) throws RemoteException;

    void processRestarted(String str, String str2, int i) throws RemoteException;

    void publishService(IBinder iBinder, Intent intent, IBinder iBinder2, int i) throws RemoteException;

    void registerProcessObserver(IProcessObserver iProcessObserver) throws RemoteException;

    void removePendingIntent(IBinder iBinder) throws RemoteException;

    void serviceDoneExecuting(IBinder iBinder, int i, int i2, int i3, int i4) throws RemoteException;

    void setServiceForeground(ComponentName componentName, IBinder iBinder, int i, Notification notification, boolean z, int i2) throws RemoteException;

    int startActivities(Intent[] intentArr, String[] strArr, IBinder iBinder, Bundle bundle, int i) throws RemoteException;

    int startActivity(Intent intent, ActivityInfo activityInfo, IBinder iBinder, Bundle bundle, String str, int i, int i2) throws RemoteException;

    ComponentName startService(IBinder iBinder, Intent intent, String str, int i) throws RemoteException;

    int stopService(IBinder iBinder, Intent intent, String str, int i) throws RemoteException;

    boolean stopServiceToken(ComponentName componentName, IBinder iBinder, int i, int i2) throws RemoteException;

    void unbindFinished(IBinder iBinder, Intent intent, boolean z, int i) throws RemoteException;

    boolean unbindService(IServiceConnection iServiceConnection, int i) throws RemoteException;

    void unregisterProcessObserver(IProcessObserver iProcessObserver) throws RemoteException;
}
