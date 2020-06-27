package com.android.launcher3.notification;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import com.android.launcher3.Utilities;
import java.util.ArrayList;
import java.util.List;

public class NotificationKeyData {
    public int count;
    public final String notificationKey;
    public final String shortcutId;

    private NotificationKeyData(String str, String str2, int i) {
        this.notificationKey = str;
        this.shortcutId = str2;
        this.count = Math.max(1, i);
    }

    public static NotificationKeyData fromNotification(StatusBarNotification statusBarNotification) {
        Notification notification = statusBarNotification.getNotification();
        return new NotificationKeyData(statusBarNotification.getKey(), Utilities.ATLEAST_OREO ? notification.getShortcutId() : null, notification.number);
    }

    public static List<String> extractKeysOnly(@NonNull List<NotificationKeyData> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (NotificationKeyData notificationKeyData : list) {
            arrayList.add(notificationKeyData.notificationKey);
        }
        return arrayList;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NotificationKeyData)) {
            return false;
        }
        return ((NotificationKeyData) obj).notificationKey.equals(this.notificationKey);
    }
}
