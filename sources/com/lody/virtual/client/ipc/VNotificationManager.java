package com.lody.virtual.client.ipc;

import android.app.Notification;
import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.server.INotificationManager;
import com.lody.virtual.server.INotificationManager.Stub;
import com.lody.virtual.server.notification.NotificationCompat;

public class VNotificationManager {
    private static final VNotificationManager sInstance = new VNotificationManager();
    private final NotificationCompat mNotificationCompat = NotificationCompat.create();
    private INotificationManager mRemote;

    private VNotificationManager() {
    }

    public static VNotificationManager get() {
        return sInstance;
    }

    public INotificationManager getService() {
        if (this.mRemote == null || (!this.mRemote.asBinder().pingBinder() && !VirtualCore.get().isVAppProcess())) {
            synchronized (VNotificationManager.class) {
                this.mRemote = Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.NOTIFICATION));
            }
        }
        return this.mRemote;
    }

    public boolean dealNotification(int i, Notification notification, String str) {
        boolean z = false;
        if (notification == null) {
            return false;
        }
        if (VirtualCore.get().getHostPkg().equals(str) || this.mNotificationCompat.dealNotification(i, notification, str)) {
            z = true;
        }
        return z;
    }

    public int dealNotificationId(int i, String str, String str2, int i2) {
        try {
            return getService().dealNotificationId(i, str, str2, i2);
        } catch (RemoteException e) {
            e.printStackTrace();
            return i;
        }
    }

    public String dealNotificationTag(int i, String str, String str2, int i2) {
        try {
            return getService().dealNotificationTag(i, str, str2, i2);
        } catch (RemoteException e) {
            e.printStackTrace();
            return str2;
        }
    }

    public boolean areNotificationsEnabledForPackage(String str, int i) {
        try {
            return getService().areNotificationsEnabledForPackage(str, i);
        } catch (RemoteException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void setNotificationsEnabledForPackage(String str, boolean z, int i) {
        try {
            getService().setNotificationsEnabledForPackage(str, z, i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addNotification(int i, String str, String str2, int i2) {
        try {
            getService().addNotification(i, str, str2, i2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void cancelAllNotification(String str, int i) {
        try {
            getService().cancelAllNotification(str, i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
