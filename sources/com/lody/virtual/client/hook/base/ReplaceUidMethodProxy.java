package com.lody.virtual.client.hook.base;

import java.lang.reflect.Method;

public class ReplaceUidMethodProxy extends StaticMethodProxy {
    private final int index;

    public ReplaceUidMethodProxy(String str, int i) {
        super(str);
        this.index = i;
    }

    public boolean beforeCall(Object obj, Method method, Object... objArr) {
        int intValue = objArr[this.index].intValue();
        if (intValue == getVUid() || intValue == getBaseVUid()) {
            objArr[this.index] = Integer.valueOf(getRealUid());
        }
        return super.beforeCall(obj, method, objArr);
    }
}
