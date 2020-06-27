package com.lody.virtual.client.hook.proxies.window.session;

import android.view.WindowManager.LayoutParams;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import com.lody.virtual.helper.utils.ArrayUtils;
import java.lang.reflect.Method;

class BaseMethodProxy extends StaticMethodProxy {
    public BaseMethodProxy(String str) {
        super(str);
    }

    public Object call(Object obj, Method method, Object... objArr) throws Throwable {
        int indexOfFirst = ArrayUtils.indexOfFirst(objArr, LayoutParams.class);
        if (indexOfFirst != -1) {
            LayoutParams layoutParams = objArr[indexOfFirst];
            if (layoutParams != null) {
                layoutParams.packageName = getHostPkg();
            }
        }
        return method.invoke(obj, objArr);
    }
}
