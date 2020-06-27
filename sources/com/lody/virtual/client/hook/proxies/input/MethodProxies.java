package com.lody.virtual.client.hook.proxies.input;

import android.view.inputmethod.EditorInfo;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.helper.utils.ArrayUtils;
import java.lang.reflect.Method;

class MethodProxies {

    static class StartInput extends StartInputOrWindowGainedFocus {
        public String getMethodName() {
            return "startInput";
        }

        StartInput() {
        }
    }

    static class StartInputOrWindowGainedFocus extends MethodProxy {
        public String getMethodName() {
            return "startInputOrWindowGainedFocus";
        }

        StartInputOrWindowGainedFocus() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            int indexOfFirst = ArrayUtils.indexOfFirst(objArr, EditorInfo.class);
            if (indexOfFirst != -1) {
                objArr[indexOfFirst].packageName = getHostPkg();
            }
            return method.invoke(obj, objArr);
        }
    }

    static class WindowGainedFocus extends StartInputOrWindowGainedFocus {
        public String getMethodName() {
            return "windowGainedFocus";
        }

        WindowGainedFocus() {
        }
    }

    MethodProxies() {
    }
}
