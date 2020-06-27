package com.lody.virtual.server.notification;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import com.lody.virtual.client.core.VirtualCore;

class NotificationCompatCompatV14 extends NotificationCompat {
    private final RemoteViewsFixer mRemoteViewsFixer = new RemoteViewsFixer(this);

    NotificationCompatCompatV14() {
    }

    private RemoteViewsFixer getRemoteViewsFixer() {
        return this.mRemoteViewsFixer;
    }

    public boolean dealNotification(int i, Notification notification, String str) {
        Context appContext = getAppContext(str);
        if (appContext == null) {
            return false;
        }
        if (VirtualCore.get().isOutsideInstalled(str)) {
            if (notification.icon != 0) {
                getNotificationFixer().fixIconImage(appContext.getResources(), notification.contentView, false, notification);
                if (VERSION.SDK_INT >= 16) {
                    getNotificationFixer().fixIconImage(appContext.getResources(), notification.bigContentView, false, notification);
                }
                notification.icon = getHostContext().getApplicationInfo().icon;
            }
            return true;
        }
        if (notification.tickerView != null) {
            if (isSystemLayout(notification.tickerView)) {
                getNotificationFixer().fixRemoteViewActions(appContext, false, notification.tickerView);
            } else {
                RemoteViewsFixer remoteViewsFixer = getRemoteViewsFixer();
                StringBuilder sb = new StringBuilder();
                sb.append(i);
                sb.append(":tickerView");
                notification.tickerView = remoteViewsFixer.makeRemoteViews(sb.toString(), appContext, notification.tickerView, false, false);
            }
        }
        if (notification.contentView != null) {
            if (isSystemLayout(notification.contentView)) {
                getNotificationFixer().fixIconImage(appContext.getResources(), notification.contentView, getNotificationFixer().fixRemoteViewActions(appContext, false, notification.contentView), notification);
            } else {
                RemoteViewsFixer remoteViewsFixer2 = getRemoteViewsFixer();
                StringBuilder sb2 = new StringBuilder();
                sb2.append(i);
                sb2.append(":contentView");
                notification.contentView = remoteViewsFixer2.makeRemoteViews(sb2.toString(), appContext, notification.contentView, false, true);
            }
        }
        if (VERSION.SDK_INT >= 16 && notification.bigContentView != null) {
            if (isSystemLayout(notification.bigContentView)) {
                getNotificationFixer().fixRemoteViewActions(appContext, false, notification.bigContentView);
            } else {
                RemoteViewsFixer remoteViewsFixer3 = getRemoteViewsFixer();
                StringBuilder sb3 = new StringBuilder();
                sb3.append(i);
                sb3.append(":bigContentView");
                notification.bigContentView = remoteViewsFixer3.makeRemoteViews(sb3.toString(), appContext, notification.bigContentView, true, true);
            }
        }
        if (VERSION.SDK_INT >= 21 && notification.headsUpContentView != null) {
            if (isSystemLayout(notification.headsUpContentView)) {
                getNotificationFixer().fixIconImage(appContext.getResources(), notification.contentView, getNotificationFixer().fixRemoteViewActions(appContext, false, notification.headsUpContentView), notification);
            } else {
                RemoteViewsFixer remoteViewsFixer4 = getRemoteViewsFixer();
                StringBuilder sb4 = new StringBuilder();
                sb4.append(i);
                sb4.append(":headsUpContentView");
                notification.headsUpContentView = remoteViewsFixer4.makeRemoteViews(sb4.toString(), appContext, notification.headsUpContentView, false, false);
            }
        }
        if (notification.icon != 0) {
            notification.icon = getHostContext().getApplicationInfo().icon;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public Context getAppContext(String str) {
        try {
            return getHostContext().createPackageContext(str, 3);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
