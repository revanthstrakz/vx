package com.lody.virtual.server.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.lody.virtual.helper.compat.SystemPropertiesCompat;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.p007os.VEnvironment;

@TargetApi(21)
class NotificationCompatCompatV21 extends NotificationCompatCompatV14 {
    private static final String TAG = "NotificationCompatCompatV21";

    NotificationCompatCompatV21() {
    }

    public boolean dealNotification(int i, Notification notification, String str) {
        Context appContext = getAppContext(str);
        return resolveRemoteViews(appContext, str, notification) || resolveRemoteViews(appContext, str, notification.publicVersion);
    }

    private boolean resolveRemoteViews(Context context, String str, Notification notification) {
        boolean z = false;
        if (notification == null) {
            return false;
        }
        String str2 = null;
        PackageInfo packageInfo = getPackageInfo(str);
        ApplicationInfo applicationInfo = getHostContext().getApplicationInfo();
        if (packageInfo != null) {
            str2 = packageInfo.applicationInfo.sourceDir;
        }
        if (TextUtils.isEmpty(str2)) {
            str2 = VEnvironment.getPackageResourcePath(str).getAbsolutePath();
        }
        getNotificationFixer().fixNotificationRemoteViews(context, notification);
        if (VERSION.SDK_INT >= 23) {
            getNotificationFixer().fixIcon(notification.getSmallIcon(), context, packageInfo != null);
            NotificationFixer notificationFixer = getNotificationFixer();
            Icon largeIcon = notification.getLargeIcon();
            if (packageInfo != null) {
                z = true;
            }
            notificationFixer.fixIcon(largeIcon, context, z);
        } else {
            getNotificationFixer().fixIconImage(context.getResources(), notification.contentView, false, notification);
        }
        notification.icon = applicationInfo.icon;
        ApplicationInfo applicationInfo2 = new ApplicationInfo(applicationInfo);
        applicationInfo2.packageName = str;
        applicationInfo2.publicSourceDir = str2;
        applicationInfo2.sourceDir = str2;
        fixApplicationInfo(notification.tickerView, applicationInfo2);
        fixApplicationInfo(notification.contentView, applicationInfo2);
        fixApplicationInfo(notification.bigContentView, applicationInfo2);
        fixApplicationInfo(notification.headsUpContentView, applicationInfo2);
        fixCustomNotificationOnColorOs(notification);
        Bundle bundle = (Bundle) Reflect.m80on((Object) notification).get("extras");
        if (bundle != null) {
            bundle.putParcelable(NotificationCompat.EXTRA_BUILDER_APPLICATION_INFO, applicationInfo2);
        }
        return true;
    }

    private ApplicationInfo getApplicationInfo(Notification notification) {
        ApplicationInfo applicationInfo = getApplicationInfo(notification.tickerView);
        if (applicationInfo != null) {
            return applicationInfo;
        }
        ApplicationInfo applicationInfo2 = getApplicationInfo(notification.contentView);
        if (applicationInfo2 != null) {
            return applicationInfo2;
        }
        if (VERSION.SDK_INT >= 16) {
            ApplicationInfo applicationInfo3 = getApplicationInfo(notification.bigContentView);
            if (applicationInfo3 != null) {
                return applicationInfo3;
            }
        }
        if (VERSION.SDK_INT >= 21) {
            ApplicationInfo applicationInfo4 = getApplicationInfo(notification.headsUpContentView);
            if (applicationInfo4 != null) {
                return applicationInfo4;
            }
        }
        return null;
    }

    private ApplicationInfo getApplicationInfo(RemoteViews remoteViews) {
        if (remoteViews != null) {
            return (ApplicationInfo) mirror.android.widget.RemoteViews.mApplication.get(remoteViews);
        }
        return null;
    }

    private void fixApplicationInfo(RemoteViews remoteViews, ApplicationInfo applicationInfo) {
        if (remoteViews != null) {
            mirror.android.widget.RemoteViews.mApplication.set(remoteViews, applicationInfo);
        }
    }

    private void fixCustomNotificationOnColorOs(Notification notification) {
        String str = SystemPropertiesCompat.get("ro.build.version.opporom", "");
        if (!TextUtils.isEmpty(str) && str.toLowerCase().startsWith("v3")) {
            if (notification.contentView != null) {
                notification.contentView = null;
            }
            if (notification.headsUpContentView != null) {
                notification.headsUpContentView = null;
            }
            if (notification.bigContentView != null) {
                notification.bigContentView = null;
            }
            if (notification.tickerView != null) {
                notification.tickerView = null;
            }
        }
    }
}
