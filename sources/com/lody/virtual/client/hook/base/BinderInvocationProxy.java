package com.lody.virtual.client.hook.base;

import android.os.IBinder;
import android.os.IInterface;
import mirror.RefStaticMethod;
import mirror.android.p017os.ServiceManager;

public abstract class BinderInvocationProxy extends MethodInvocationProxy<BinderInvocationStub> {
    protected String mServiceName;

    public BinderInvocationProxy(IInterface iInterface, String str) {
        this(new BinderInvocationStub(iInterface), str);
    }

    public BinderInvocationProxy(RefStaticMethod<IInterface> refStaticMethod, String str) {
        this(new BinderInvocationStub(refStaticMethod, (IBinder) ServiceManager.getService.call(str)), str);
    }

    public BinderInvocationProxy(Class<?> cls, String str) {
        this(new BinderInvocationStub(cls, (IBinder) ServiceManager.getService.call(str)), str);
    }

    public BinderInvocationProxy(BinderInvocationStub binderInvocationStub, String str) {
        super(binderInvocationStub);
        this.mServiceName = str;
    }

    public void inject() throws Throwable {
        ((BinderInvocationStub) getInvocationStub()).replaceService(this.mServiceName);
    }

    public boolean isEnvBad() {
        IBinder iBinder = (IBinder) ServiceManager.getService.call(this.mServiceName);
        if (iBinder == null || getInvocationStub() == iBinder) {
            return false;
        }
        return true;
    }
}
