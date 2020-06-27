package com.lody.virtual.server.notification;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.widget.RemoteViews;
import com.lody.virtual.client.core.VirtualCore;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import mirror.com.android.internal.R_Hide.layout;

public abstract class NotificationCompat {
    public static final String EXTRA_BIG_TEXT = "android.bigText";
    public static final String EXTRA_BUILDER_APPLICATION_INFO = "android.appInfo";
    public static final String EXTRA_INFO_TEXT = "android.infoText";
    public static final String EXTRA_PROGRESS = "android.progress";
    public static final String EXTRA_PROGRESS_MAX = "android.progressMax";
    public static final String EXTRA_SUB_TEXT = "android.subText";
    public static final String EXTRA_SUMMARY_TEXT = "android.summaryText";
    public static final String EXTRA_TEXT = "android.text";
    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_TITLE_BIG = "android.title.big";
    static final String SYSTEM_UI_PKG = "com.android.systemui";
    static final String TAG = "NotificationCompat";
    private NotificationFixer mNotificationFixer;
    private final List<Integer> sSystemLayoutResIds = new ArrayList(10);

    public abstract boolean dealNotification(int i, Notification notification, String str);

    NotificationCompat() {
        loadSystemLayoutRes();
        this.mNotificationFixer = new NotificationFixer(this);
    }

    public static NotificationCompat create() {
        if (VERSION.SDK_INT >= 21) {
            return new NotificationCompatCompatV21();
        }
        return new NotificationCompatCompatV14();
    }

    private void loadSystemLayoutRes() {
        Field[] fields;
        for (Field field : layout.TYPE.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                try {
                    this.sSystemLayoutResIds.add(Integer.valueOf(field.getInt(null)));
                } catch (Throwable unused) {
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public NotificationFixer getNotificationFixer() {
        return this.mNotificationFixer;
    }

    /* access modifiers changed from: 0000 */
    public boolean isSystemLayout(RemoteViews remoteViews) {
        return remoteViews != null && this.sSystemLayoutResIds.contains(Integer.valueOf(remoteViews.getLayoutId()));
    }

    public Context getHostContext() {
        return VirtualCore.get().getContext();
    }

    /* access modifiers changed from: 0000 */
    public PackageInfo getPackageInfo(String str) {
        try {
            return VirtualCore.get().getUnHookPackageManager().getPackageInfo(str, 0);
        } catch (NameNotFoundException unused) {
            return null;
        }
    }
}
