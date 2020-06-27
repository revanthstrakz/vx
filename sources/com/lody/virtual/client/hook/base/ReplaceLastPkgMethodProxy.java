package com.lody.virtual.client.hook.base;

import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import java.lang.reflect.Method;

public class ReplaceLastPkgMethodProxy extends StaticMethodProxy {
    public ReplaceLastPkgMethodProxy(String str) {
        super(str);
    }

    public boolean beforeCall(Object obj, Method method, Object... objArr) {
        MethodParameterUtils.replaceLastAppPkg(objArr);
        return super.beforeCall(obj, method, objArr);
    }
}
