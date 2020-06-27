package com.lody.virtual.client.hook.proxies.libcore;

import com.lody.virtual.client.NativeEngine;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.helper.utils.Reflect;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import mirror.libcore.p019io.C1315Os;

class MethodProxies {

    static class GetUid extends MethodProxy {
        public String getMethodName() {
            return "getuid";
        }

        GetUid() {
        }

        public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
            return Integer.valueOf(NativeEngine.onGetUid(((Integer) obj2).intValue()));
        }
    }

    static class Getpwnam extends MethodProxy {
        public String getMethodName() {
            return "getpwnam";
        }

        Getpwnam() {
        }

        public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
            if (obj2 != null) {
                Reflect on = Reflect.m80on(obj2);
                if (((Integer) on.get("pw_uid")).intValue() == VirtualCore.get().myUid()) {
                    on.set("pw_uid", Integer.valueOf(VClientImpl.get().getVUid()));
                }
            }
            return obj2;
        }
    }

    static class GetsockoptUcred extends MethodProxy {
        public String getMethodName() {
            return "getsockoptUcred";
        }

        GetsockoptUcred() {
        }

        public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
            if (obj2 != null) {
                Reflect on = Reflect.m80on(obj2);
                if (((Integer) on.get("uid")).intValue() == VirtualCore.get().myUid()) {
                    on.set("uid", Integer.valueOf(getBaseVUid()));
                }
            }
            return obj2;
        }
    }

    static class Lstat extends Stat {
        public String getMethodName() {
            return "lstat";
        }

        Lstat() {
        }
    }

    static class Stat extends MethodProxy {
        private static Field st_uid;

        public String getMethodName() {
            return "stat";
        }

        Stat() {
        }

        static {
            try {
                st_uid = C1315Os.TYPE.getMethod("stat", new Class[]{String.class}).getReturnType().getDeclaredField("st_uid");
                st_uid.setAccessible(true);
            } catch (Throwable th) {
                throw new IllegalStateException(th);
            }
        }

        public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
            if (((Integer) st_uid.get(obj2)).intValue() == VirtualCore.get().myUid()) {
                st_uid.set(obj2, Integer.valueOf(getBaseVUid()));
            }
            return obj2;
        }
    }

    MethodProxies() {
    }
}
