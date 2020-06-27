package com.lody.virtual.client.hook.proxies.mount;

import android.os.Build.VERSION;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import java.io.File;
import java.lang.reflect.Method;

class MethodProxies {

    static class GetVolumeList extends MethodProxy {
        public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
            return obj2;
        }

        public String getMethodName() {
            return "getVolumeList";
        }

        GetVolumeList() {
        }

        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            if (objArr == null || objArr.length == 0) {
                return super.beforeCall(obj, method, objArr);
            }
            if (objArr[0] instanceof Integer) {
                objArr[0] = Integer.valueOf(getRealUid());
            }
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return super.beforeCall(obj, method, objArr);
        }
    }

    static class Mkdirs extends MethodProxy {
        public String getMethodName() {
            return "mkdirs";
        }

        Mkdirs() {
        }

        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return super.beforeCall(obj, method, objArr);
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str;
            if (VERSION.SDK_INT < 19) {
                return super.call(obj, method, objArr);
            }
            if (objArr.length == 1) {
                str = objArr[0];
            } else {
                str = objArr[1];
            }
            File file = new File(str);
            if (file.exists() || file.mkdirs()) {
                return Integer.valueOf(0);
            }
            return Integer.valueOf(-1);
        }
    }

    MethodProxies() {
    }
}
