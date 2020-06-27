package com.lody.virtual.client.hook.base;

import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import java.lang.reflect.Method;

public class ReplaceSequencePkgMethodProxy extends StaticMethodProxy {
    private int sequence;

    public ReplaceSequencePkgMethodProxy(String str, int i) {
        super(str);
        this.sequence = i;
    }

    public boolean beforeCall(Object obj, Method method, Object... objArr) {
        MethodParameterUtils.replaceSequenceAppPkg(objArr, this.sequence);
        return super.beforeCall(obj, method, objArr);
    }
}
