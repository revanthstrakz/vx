package com.android.launcher3.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.SettingsActivity;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.SettingsObserver;
import com.android.launcher3.util.SettingsObserver.Secure;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@TargetApi(26)
public class NotificationListener extends NotificationListenerService {
    private static final int MSG_NOTIFICATION_FULL_REFRESH = 3;
    private static final int MSG_NOTIFICATION_POSTED = 1;
    private static final int MSG_NOTIFICATION_REMOVED = 2;
    public static final String TAG = "NotificationListener";
    /* access modifiers changed from: private */
    public static boolean sIsConnected;
    private static boolean sIsCreated;
    private static NotificationListener sNotificationListenerInstance;
    /* access modifiers changed from: private */
    public static NotificationsChangedListener sNotificationsChangedListener;
    private SettingsObserver mNotificationBadgingObserver;
    private final Ranking mTempRanking = new Ranking();
    private final Callback mUiCallback = new Callback() {
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    if (NotificationListener.sNotificationsChangedListener != null) {
                        NotificationPostedMsg notificationPostedMsg = (NotificationPostedMsg) message.obj;
                        NotificationListener.sNotificationsChangedListener.onNotificationPosted(notificationPostedMsg.packageUserKey, notificationPostedMsg.notificationKey, notificationPostedMsg.shouldBeFilteredOut);
                        break;
                    }
                    break;
                case 2:
                    if (NotificationListener.sNotificationsChangedListener != null) {
                        Pair pair = (Pair) message.obj;
                        NotificationListener.sNotificationsChangedListener.onNotificationRemoved((PackageUserKey) pair.first, (NotificationKeyData) pair.second);
                        break;
                    }
                    break;
                case 3:
                    if (NotificationListener.sNotificationsChangedListener != null) {
                        NotificationListener.sNotificationsChangedListener.onNotificationFullRefresh((List) message.obj);
                        break;
                    }
                    break;
            }
            return true;
        }
    };
    /* access modifiers changed from: private */
    public final Handler mUiHandler = new Handler(Looper.getMainLooper(), this.mUiCallback);
    private final Callback mWorkerCallback = new Callback() {
        public boolean handleMessage(Message message) {
            Object obj;
            switch (message.what) {
                case 1:
                    NotificationListener.this.mUiHandler.obtainMessage(message.what, message.obj).sendToTarget();
                    break;
                case 2:
                    NotificationListener.this.mUiHandler.obtainMessage(message.what, message.obj).sendToTarget();
                    break;
                case 3:
                    if (NotificationListener.sIsConnected) {
                        try {
                            obj = NotificationListener.this.filterNotifications(NotificationListener.this.getActiveNotifications());
                        } catch (SecurityException unused) {
                            Log.e(NotificationListener.TAG, "SecurityException: failed to fetch notifications");
                            obj = new ArrayList();
                        }
                    } else {
                        obj = new ArrayList();
                    }
                    NotificationListener.this.mUiHandler.obtainMessage(message.what, obj).sendToTarget();
                    break;
            }
            return true;
        }
    };
    private final Handler mWorkerHandler = new Handler(LauncherModel.getWorkerLooper(), this.mWorkerCallback);

    private class NotificationPostedMsg {
        final NotificationKeyData notificationKey;
        final PackageUserKey packageUserKey;
        final boolean shouldBeFilteredOut;

        NotificationPostedMsg(StatusBarNotification statusBarNotification) {
            this.packageUserKey = PackageUserKey.fromNotification(statusBarNotification);
            this.notificationKey = NotificationKeyData.fromNotification(statusBarNotification);
            this.shouldBeFilteredOut = NotificationListener.this.shouldBeFilteredOut(statusBarNotification);
        }
    }

    public interface NotificationsChangedListener {
        void onNotificationFullRefresh(List<StatusBarNotification> list);

        void onNotificationPosted(PackageUserKey packageUserKey, NotificationKeyData notificationKeyData, boolean z);

        void onNotificationRemoved(PackageUserKey packageUserKey, NotificationKeyData notificationKeyData);
    }

    public NotificationListener() {
        sNotificationListenerInstance = this;
    }

    public void onCreate() {
        super.onCreate();
        sIsCreated = true;
        this.mNotificationBadgingObserver = new Secure(getContentResolver()) {
            public void onSettingChanged(boolean z) {
                if (!z) {
                    NotificationListener.this.requestUnbind();
                }
            }
        };
        this.mNotificationBadgingObserver.register(SettingsActivity.NOTIFICATION_BADGING, new String[0]);
    }

    public void onDestroy() {
        super.onDestroy();
        sIsCreated = false;
        this.mNotificationBadgingObserver.unregister();
    }

    @Nullable
    public static NotificationListener getInstanceIfConnected() {
        if (sIsConnected) {
            return sNotificationListenerInstance;
        }
        return null;
    }

    public static void setNotificationsChangedListener(NotificationsChangedListener notificationsChangedListener) {
        sNotificationsChangedListener = notificationsChangedListener;
        NotificationListener instanceIfConnected = getInstanceIfConnected();
        if (instanceIfConnected != null) {
            instanceIfConnected.onNotificationFullRefresh();
        } else if (!sIsCreated && sNotificationsChangedListener != null) {
            sNotificationsChangedListener.onNotificationFullRefresh(Collections.emptyList());
        }
    }

    public static void removeNotificationsChangedListener() {
        sNotificationsChangedListener = null;
    }

    public void onListenerConnected() {
        super.onListenerConnected();
        sIsConnected = true;
        onNotificationFullRefresh();
    }

    private void onNotificationFullRefresh() {
        this.mWorkerHandler.obtainMessage(3).sendToTarget();
    }

    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        sIsConnected = false;
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        super.onNotificationPosted(statusBarNotification);
        this.mWorkerHandler.obtainMessage(1, new NotificationPostedMsg(statusBarNotification)).sendToTarget();
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        super.onNotificationRemoved(statusBarNotification);
        this.mWorkerHandler.obtainMessage(2, new Pair(PackageUserKey.fromNotification(statusBarNotification), NotificationKeyData.fromNotification(statusBarNotification))).sendToTarget();
    }

    public List<StatusBarNotification> getNotificationsForKeys(List<NotificationKeyData> list) {
        StatusBarNotification[] activeNotifications = getActiveNotifications((String[]) NotificationKeyData.extractKeysOnly(list).toArray(new String[list.size()]));
        return activeNotifications == null ? Collections.emptyList() : Arrays.asList(activeNotifications);
    }

    /* access modifiers changed from: private */
    public List<StatusBarNotification> filterNotifications(StatusBarNotification[] statusBarNotificationArr) {
        if (statusBarNotificationArr == null) {
            return null;
        }
        ArraySet arraySet = new ArraySet();
        for (int i = 0; i < statusBarNotificationArr.length; i++) {
            if (shouldBeFilteredOut(statusBarNotificationArr[i])) {
                arraySet.add(Integer.valueOf(i));
            }
        }
        ArrayList arrayList = new ArrayList(statusBarNotificationArr.length - arraySet.size());
        for (int i2 = 0; i2 < statusBarNotificationArr.length; i2++) {
            if (!arraySet.contains(Integer.valueOf(i2))) {
                arrayList.add(statusBarNotificationArr[i2]);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public boolean shouldBeFilteredOut(StatusBarNotification statusBarNotification) {
        Notification notification = statusBarNotification.getNotification();
        if (Utilities.ATLEAST_OREO) {
            getCurrentRanking().getRanking(statusBarNotification.getKey(), this.mTempRanking);
            if (!this.mTempRanking.canShowBadge()) {
                return true;
            }
            if (this.mTempRanking.getChannel().getId().equals("miscellaneous") && (notification.flags & 2) != 0) {
                return true;
            }
        } else if ((notification.flags & 2) != 0) {
            return true;
        }
        boolean z = false;
        boolean z2 = (notification.flags & 512) != 0;
        boolean z3 = TextUtils.isEmpty(notification.extras.getCharSequence("android.title")) && TextUtils.isEmpty(notification.extras.getCharSequence("android.text"));
        if (z2 || z3) {
            z = true;
        }
        return z;
    }
}
