package com.lody.virtual.client.hook.base;

import android.content.Context;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.MethodInvocationStub;
import com.lody.virtual.client.interfaces.IInjector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public abstract class MethodInvocationProxy<T extends MethodInvocationStub> implements IInjector {
    protected T mInvocationStub;

    /* access modifiers changed from: protected */
    public void afterHookApply(T t) {
    }

    public abstract void inject() throws Throwable;

    public MethodInvocationProxy(T t) {
        this.mInvocationStub = t;
        onBindMethods();
        afterHookApply(t);
        LogInvocation logInvocation = (LogInvocation) getClass().getAnnotation(LogInvocation.class);
        if (logInvocation != null) {
            t.setInvocationLoggingCondition(logInvocation.value());
        }
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        Class[] declaredClasses;
        if (this.mInvocationStub != null) {
            Inject inject = (Inject) getClass().getAnnotation(Inject.class);
            if (inject != null) {
                for (Class cls : inject.value().getDeclaredClasses()) {
                    if (!Modifier.isAbstract(cls.getModifiers()) && MethodProxy.class.isAssignableFrom(cls) && cls.getAnnotation(SkipInject.class) == null) {
                        addMethodProxy(cls);
                    }
                }
            }
        }
    }

    private void addMethodProxy(Class<?> cls) {
        MethodProxy methodProxy;
        try {
            Constructor constructor = cls.getDeclaredConstructors()[0];
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            if (constructor.getParameterTypes().length == 0) {
                methodProxy = (MethodProxy) constructor.newInstance(new Object[0]);
            } else {
                methodProxy = (MethodProxy) constructor.newInstance(new Object[]{this});
            }
            this.mInvocationStub.addMethodProxy(methodProxy);
        } catch (Throwable th) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to instance Hook : ");
            sb.append(cls);
            sb.append(" : ");
            sb.append(th.getMessage());
            throw new RuntimeException(sb.toString());
        }
    }

    public MethodProxy addMethodProxy(MethodProxy methodProxy) {
        return this.mInvocationStub.addMethodProxy(methodProxy);
    }

    public Context getContext() {
        return VirtualCore.get().getContext();
    }

    public T getInvocationStub() {
        return this.mInvocationStub;
    }
}
