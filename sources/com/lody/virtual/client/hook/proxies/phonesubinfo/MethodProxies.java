package com.lody.virtual.client.hook.proxies.phonesubinfo;

import com.lody.virtual.client.hook.base.MethodProxy;
import java.lang.reflect.Method;

class MethodProxies {

    static class GetDeviceId extends MethodProxy {
        public String getMethodName() {
            return "getDeviceId";
        }

        GetDeviceId() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return getDeviceInfo().deviceId;
        }
    }

    static class GetDeviceIdForSubscriber extends GetDeviceId {
        public String getMethodName() {
            return "getDeviceIdForSubscriber";
        }

        GetDeviceIdForSubscriber() {
        }
    }

    static class GetIccSerialNumber extends MethodProxy {
        public String getMethodName() {
            return "getIccSerialNumber";
        }

        GetIccSerialNumber() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return getDeviceInfo().iccId;
        }
    }

    static class getIccSerialNumberForSubscriber extends GetIccSerialNumber {
        public String getMethodName() {
            return "getIccSerialNumberForSubscriber";
        }

        getIccSerialNumberForSubscriber() {
        }
    }

    MethodProxies() {
    }
}
