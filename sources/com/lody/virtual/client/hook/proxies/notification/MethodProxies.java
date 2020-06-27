package com.lody.virtual.client.hook.proxies.notification;

import android.app.Notification;
import android.os.Build.VERSION;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.client.ipc.VNotificationManager;
import com.lody.virtual.helper.utils.ArrayUtils;
import java.lang.reflect.Method;

class MethodProxies {

    static class AreNotificationsEnabledForPackage extends MethodProxy {
        public String getMethodName() {
            return "areNotificationsEnabledForPackage";
        }

        AreNotificationsEnabledForPackage() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = objArr[0];
            if (getHostPkg().equals(str)) {
                return method.invoke(obj, objArr);
            }
            return Boolean.valueOf(VNotificationManager.get().areNotificationsEnabledForPackage(str, getAppUserId()));
        }
    }

    static class CancelAllNotifications extends MethodProxy {
        public String getMethodName() {
            return "cancelAllNotifications";
        }

        CancelAllNotifications() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String replaceFirstAppPkg = MethodParameterUtils.replaceFirstAppPkg(objArr);
            if (!VirtualCore.get().isAppInstalled(replaceFirstAppPkg)) {
                return method.invoke(obj, objArr);
            }
            VNotificationManager.get().cancelAllNotification(replaceFirstAppPkg, getAppUserId());
            return Integer.valueOf(0);
        }
    }

    static class CancelNotificationWithTag extends MethodProxy {
        public String getMethodName() {
            return "cancelNotificationWithTag";
        }

        CancelNotificationWithTag() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String replaceFirstAppPkg = MethodParameterUtils.replaceFirstAppPkg(objArr);
            if (getHostPkg().equals(replaceFirstAppPkg)) {
                return method.invoke(obj, objArr);
            }
            String str = objArr[1];
            int dealNotificationId = VNotificationManager.get().dealNotificationId(objArr[2].intValue(), replaceFirstAppPkg, str, getAppUserId());
            objArr[1] = VNotificationManager.get().dealNotificationTag(dealNotificationId, replaceFirstAppPkg, str, getAppUserId());
            objArr[2] = Integer.valueOf(dealNotificationId);
            return method.invoke(obj, objArr);
        }
    }

    static class EnqueueNotification extends MethodProxy {
        public String getMethodName() {
            return "enqueueNotification";
        }

        EnqueueNotification() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = objArr[0];
            if (getHostPkg().equals(str)) {
                return method.invoke(obj, objArr);
            }
            int indexOfFirst = ArrayUtils.indexOfFirst(objArr, Notification.class);
            int indexOfFirst2 = ArrayUtils.indexOfFirst(objArr, Integer.class);
            int dealNotificationId = VNotificationManager.get().dealNotificationId(objArr[indexOfFirst2].intValue(), str, null, getAppUserId());
            objArr[indexOfFirst2] = Integer.valueOf(dealNotificationId);
            if (!VNotificationManager.get().dealNotification(dealNotificationId, objArr[indexOfFirst], str)) {
                return Integer.valueOf(0);
            }
            VNotificationManager.get().addNotification(dealNotificationId, null, str, getAppUserId());
            objArr[0] = getHostPkg();
            return method.invoke(obj, objArr);
        }
    }

    static class EnqueueNotificationWithTag extends MethodProxy {
        public String getMethodName() {
            return "enqueueNotificationWithTag";
        }

        EnqueueNotificationWithTag() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = (String) objArr[0];
            if (getHostPkg().equals(str)) {
                return method.invoke(obj, objArr);
            }
            int indexOfFirst = ArrayUtils.indexOfFirst(objArr, Notification.class);
            int indexOfFirst2 = ArrayUtils.indexOfFirst(objArr, Integer.class);
            char c = VERSION.SDK_INT >= 18 ? (char) 2 : 1;
            String str2 = (String) objArr[c];
            int dealNotificationId = VNotificationManager.get().dealNotificationId(((Integer) objArr[indexOfFirst2]).intValue(), str, str2, getAppUserId());
            String dealNotificationTag = VNotificationManager.get().dealNotificationTag(dealNotificationId, str, str2, getAppUserId());
            objArr[indexOfFirst2] = Integer.valueOf(dealNotificationId);
            objArr[c] = dealNotificationTag;
            if (!VNotificationManager.get().dealNotification(dealNotificationId, (Notification) objArr[indexOfFirst], str)) {
                return Integer.valueOf(0);
            }
            VNotificationManager.get().addNotification(dealNotificationId, dealNotificationTag, str, getAppUserId());
            objArr[0] = getHostPkg();
            if (VERSION.SDK_INT >= 18 && (objArr[1] instanceof String)) {
                objArr[1] = getHostPkg();
            }
            return method.invoke(obj, objArr);
        }
    }

    static class EnqueueNotificationWithTagPriority extends EnqueueNotificationWithTag {
        public String getMethodName() {
            return "enqueueNotificationWithTagPriority";
        }

        EnqueueNotificationWithTagPriority() {
        }
    }

    static class SetNotificationsEnabledForPackage extends MethodProxy {
        public String getMethodName() {
            return "setNotificationsEnabledForPackage";
        }

        SetNotificationsEnabledForPackage() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = objArr[0];
            if (getHostPkg().equals(str)) {
                return method.invoke(obj, objArr);
            }
            VNotificationManager.get().setNotificationsEnabledForPackage(str, objArr[ArrayUtils.indexOfFirst(objArr, Boolean.class)].booleanValue(), getAppUserId());
            return Integer.valueOf(0);
        }
    }

    MethodProxies() {
    }
}
