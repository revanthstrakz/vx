package com.lody.virtual.client.hook.proxies.window;

import android.os.IInterface;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.proxies.window.session.WindowSessionPatch;
import java.lang.reflect.Method;

class MethodProxies {

    static abstract class BasePatchSession extends MethodProxy {
        BasePatchSession() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            Object invoke = method.invoke(obj, objArr);
            return invoke instanceof IInterface ? proxySession((IInterface) invoke) : invoke;
        }

        private Object proxySession(IInterface iInterface) {
            return new WindowSessionPatch(iInterface).getInvocationStub().getProxyInterface();
        }
    }

    static class OpenSession extends BasePatchSession {
        public String getMethodName() {
            return "openSession";
        }

        OpenSession() {
        }
    }

    static class OverridePendingAppTransition extends BasePatchSession {
        public String getMethodName() {
            return "overridePendingAppTransition";
        }

        OverridePendingAppTransition() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (objArr[0] instanceof String) {
                objArr[0] = getHostPkg();
            }
            return super.call(obj, method, objArr);
        }
    }

    static class OverridePendingAppTransitionInPlace extends MethodProxy {
        public String getMethodName() {
            return "overridePendingAppTransitionInPlace";
        }

        OverridePendingAppTransitionInPlace() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (objArr[0] instanceof String) {
                objArr[0] = getHostPkg();
            }
            return method.invoke(obj, objArr);
        }
    }

    static class SetAppStartingWindow extends BasePatchSession {
        public String getMethodName() {
            return "setAppStartingWindow";
        }

        SetAppStartingWindow() {
        }
    }

    MethodProxies() {
    }
}
