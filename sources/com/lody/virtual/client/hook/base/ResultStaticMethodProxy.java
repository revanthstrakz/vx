package com.lody.virtual.client.hook.base;

import java.lang.reflect.Method;

public class ResultStaticMethodProxy extends StaticMethodProxy {
    Object mResult;

    public ResultStaticMethodProxy(String str, Object obj) {
        super(str);
        this.mResult = obj;
    }

    public Object getResult() {
        return this.mResult;
    }

    public Object call(Object obj, Method method, Object... objArr) throws Throwable {
        return this.mResult;
    }
}
