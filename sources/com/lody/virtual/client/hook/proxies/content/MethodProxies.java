package com.lody.virtual.client.hook.proxies.content;

import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.hook.base.MethodProxy;
import java.lang.reflect.Method;

class MethodProxies {

    static class NotifyChange extends MethodProxy {
        public String getMethodName() {
            return "notifyChange";
        }

        NotifyChange() {
        }

        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            if (VERSION.SDK_INT < 26) {
                return super.beforeCall(obj, method, objArr);
            }
            ApplicationInfo currentApplicationInfo = VClientImpl.get().getCurrentApplicationInfo();
            if (currentApplicationInfo == null) {
                return super.beforeCall(obj, method, objArr);
            }
            int i = currentApplicationInfo.targetSdkVersion;
            int length = objArr.length;
            int i2 = -1;
            for (int i3 = 0; i3 < length; i3++) {
                Integer num = objArr[length - 1];
                if (num != null && num.getClass() == Integer.class && num.intValue() == i) {
                    i2 = i3;
                }
            }
            if (i2 != -1) {
                objArr[i2] = Integer.valueOf(25);
            }
            return super.beforeCall(obj, method, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    MethodProxies() {
    }
}
