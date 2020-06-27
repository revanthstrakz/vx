package com.lody.virtual.client.hook.proxies.power;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import com.lody.virtual.client.hook.base.ReplaceSequencePkgMethodProxy;
import com.lody.virtual.client.hook.base.ResultStaticMethodProxy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import mirror.android.p017os.IPowerManager.Stub;

public class PowerManagerStub extends BinderInvocationProxy {
    public PowerManagerStub() {
        super(Stub.asInterface, "power");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceSequencePkgMethodProxy("acquireWakeLock", 2) {
            public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                try {
                    return super.call(obj, method, objArr);
                } catch (InvocationTargetException e) {
                    return PowerManagerStub.this.onHandleError(e);
                }
            }
        });
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("acquireWakeLockWithUid") {
            public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                try {
                    return super.call(obj, method, objArr);
                } catch (InvocationTargetException e) {
                    return PowerManagerStub.this.onHandleError(e);
                }
            }
        });
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("updateWakeLockWorkSource", Integer.valueOf(0)));
    }

    /* access modifiers changed from: private */
    public Object onHandleError(InvocationTargetException invocationTargetException) throws Throwable {
        if (invocationTargetException.getCause() instanceof SecurityException) {
            return Integer.valueOf(0);
        }
        throw invocationTargetException.getCause();
    }
}
