package com.lody.virtual.helper.compat;

import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import java.util.ArrayList;
import mirror.android.app.IApplicationThread;
import mirror.android.app.IApplicationThreadICSMR1;
import mirror.android.app.IApplicationThreadKitkat;
import mirror.android.app.IApplicationThreadOreo;
import mirror.android.app.ServiceStartArgs;
import mirror.android.content.res.CompatibilityInfo;

public class IApplicationThreadCompat {
    public static void scheduleCreateService(IInterface iInterface, IBinder iBinder, ServiceInfo serviceInfo, int i) throws RemoteException {
        if (VERSION.SDK_INT >= 19) {
            IApplicationThreadKitkat.scheduleCreateService.call(iInterface, iBinder, serviceInfo, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO.get(), Integer.valueOf(i));
        } else if (VERSION.SDK_INT >= 15) {
            IApplicationThreadICSMR1.scheduleCreateService.call(iInterface, iBinder, serviceInfo, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO.get());
        } else {
            IApplicationThread.scheduleCreateService.call(iInterface, iBinder, serviceInfo);
        }
    }

    public static void scheduleBindService(IInterface iInterface, IBinder iBinder, Intent intent, boolean z, int i) throws RemoteException {
        if (VERSION.SDK_INT >= 19) {
            IApplicationThreadKitkat.scheduleBindService.call(iInterface, iBinder, intent, Boolean.valueOf(z), Integer.valueOf(i));
            return;
        }
        IApplicationThread.scheduleBindService.call(iInterface, iBinder, intent, Boolean.valueOf(z));
    }

    public static void scheduleUnbindService(IInterface iInterface, IBinder iBinder, Intent intent) throws RemoteException {
        IApplicationThread.scheduleUnbindService.call(iInterface, iBinder, intent);
    }

    public static void scheduleServiceArgs(IInterface iInterface, IBinder iBinder, boolean z, int i, int i2, Intent intent) throws RemoteException {
        if (VERSION.SDK_INT >= 26) {
            ArrayList arrayList = new ArrayList(1);
            arrayList.add(ServiceStartArgs.ctor.newInstance(Boolean.valueOf(z), Integer.valueOf(i), Integer.valueOf(i2), intent));
            IApplicationThreadOreo.scheduleServiceArgs.call(iInterface, iBinder, ParceledListSliceCompat.create(arrayList));
        } else if (VERSION.SDK_INT >= 15) {
            IApplicationThreadICSMR1.scheduleServiceArgs.call(iInterface, iBinder, Boolean.valueOf(z), Integer.valueOf(i), Integer.valueOf(i2), intent);
        } else {
            IApplicationThread.scheduleServiceArgs.call(iInterface, iBinder, Integer.valueOf(i), Integer.valueOf(i2), intent);
        }
    }

    public static void scheduleStopService(IInterface iInterface, IBinder iBinder) throws RemoteException {
        IApplicationThread.scheduleStopService.call(iInterface, iBinder);
    }
}
