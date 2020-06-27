package com.lody.virtual.server.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.server.INotificationManager.Stub;
import com.microsoft.appcenter.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import p013io.virtualapp.utils.HanziToPinyin.Token;

public class VNotificationManagerService extends Stub {
    static final String TAG = NotificationCompat.class.getSimpleName();
    private static final AtomicReference<VNotificationManagerService> gService = new AtomicReference<>();
    private Context mContext;
    private final List<String> mDisables = new ArrayList();
    private NotificationManager mNotificationManager;
    private final HashMap<String, List<NotificationInfo>> mNotifications = new HashMap<>();

    private static class NotificationInfo {

        /* renamed from: id */
        int f184id;
        String packageName;
        String tag;
        int userId;

        NotificationInfo(int i, String str, String str2, int i2) {
            this.f184id = i;
            this.tag = str;
            this.packageName = str2;
            this.userId = i2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof NotificationInfo)) {
                return super.equals(obj);
            }
            NotificationInfo notificationInfo = (NotificationInfo) obj;
            return notificationInfo.f184id == this.f184id && TextUtils.equals(notificationInfo.tag, this.tag) && TextUtils.equals(this.packageName, notificationInfo.packageName) && notificationInfo.userId == this.userId;
        }
    }

    public int dealNotificationId(int i, String str, String str2, int i2) {
        return i;
    }

    private VNotificationManagerService(Context context) {
        this.mContext = context;
        this.mNotificationManager = (NotificationManager) context.getSystemService(ServiceManagerNative.NOTIFICATION);
    }

    public static void systemReady(Context context) {
        gService.set(new VNotificationManagerService(context));
    }

    public static VNotificationManagerService get() {
        return (VNotificationManagerService) gService.get();
    }

    public String dealNotificationTag(int i, String str, String str2, int i2) {
        if (TextUtils.equals(this.mContext.getPackageName(), str)) {
            return str2;
        }
        if (str2 == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("@");
            sb.append(i2);
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb2.append(str2);
        sb2.append("@");
        sb2.append(i2);
        return sb2.toString();
    }

    public boolean areNotificationsEnabledForPackage(String str, int i) {
        List<String> list = this.mDisables;
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(i);
        return !list.contains(sb.toString());
    }

    public void setNotificationsEnabledForPackage(String str, boolean z, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(i);
        String sb2 = sb.toString();
        if (z) {
            if (this.mDisables.contains(sb2)) {
                this.mDisables.remove(sb2);
            }
        } else if (!this.mDisables.contains(sb2)) {
            this.mDisables.add(sb2);
        }
    }

    public void addNotification(int i, String str, String str2, int i2) {
        NotificationInfo notificationInfo = new NotificationInfo(i, str, str2, i2);
        synchronized (this.mNotifications) {
            List list = (List) this.mNotifications.get(str2);
            if (list == null) {
                list = new ArrayList();
                this.mNotifications.put(str2, list);
            }
            if (!list.contains(notificationInfo)) {
                list.add(notificationInfo);
            }
        }
    }

    public void cancelAllNotification(String str, int i) {
        ArrayList<NotificationInfo> arrayList = new ArrayList<>();
        synchronized (this.mNotifications) {
            List list = (List) this.mNotifications.get(str);
            if (list != null) {
                for (int size = list.size() - 1; size >= 0; size--) {
                    NotificationInfo notificationInfo = (NotificationInfo) list.get(size);
                    if (notificationInfo.userId == i) {
                        arrayList.add(notificationInfo);
                        list.remove(size);
                    }
                }
            }
        }
        for (NotificationInfo notificationInfo2 : arrayList) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("cancel ");
            sb.append(notificationInfo2.tag);
            sb.append(Token.SEPARATOR);
            sb.append(notificationInfo2.f184id);
            VLog.m86d(str2, sb.toString(), new Object[0]);
            this.mNotificationManager.cancel(notificationInfo2.tag, notificationInfo2.f184id);
        }
    }
}
