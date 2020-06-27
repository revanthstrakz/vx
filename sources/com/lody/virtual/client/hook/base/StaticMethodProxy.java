package com.lody.virtual.client.hook.base;

public class StaticMethodProxy extends MethodProxy {
    private String mName;

    public StaticMethodProxy(String str) {
        this.mName = str;
    }

    public String getMethodName() {
        return this.mName;
    }
}
