package com.lody.virtual.client.hook.base;

import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import java.lang.reflect.Method;

public class ReplaceCallingPkgMethodProxy extends StaticMethodProxy {
    public ReplaceCallingPkgMethodProxy(String str) {
        super(str);
    }

    public boolean beforeCall(Object obj, Method method, Object... objArr) {
        MethodParameterUtils.replaceFirstAppPkg(objArr);
        return super.beforeCall(obj, method, objArr);
    }
}
