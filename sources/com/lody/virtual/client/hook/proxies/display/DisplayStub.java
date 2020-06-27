package com.lody.virtual.client.hook.proxies.display;

import android.annotation.TargetApi;
import android.os.IInterface;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.base.MethodInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import mirror.android.hardware.display.DisplayManagerGlobal;

@TargetApi(17)
public class DisplayStub extends MethodInvocationProxy<MethodInvocationStub<IInterface>> {
    public DisplayStub() {
        super(new MethodInvocationStub(DisplayManagerGlobal.mDm.get(DisplayManagerGlobal.getInstance.call(new Object[0]))));
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("createVirtualDisplay"));
    }

    public void inject() throws Throwable {
        DisplayManagerGlobal.mDm.set(DisplayManagerGlobal.getInstance.call(new Object[0]), getInvocationStub().getProxyInterface());
    }

    public boolean isEnvBad() {
        if (((IInterface) DisplayManagerGlobal.mDm.get(DisplayManagerGlobal.getInstance.call(new Object[0]))) != getInvocationStub().getProxyInterface()) {
            return true;
        }
        return false;
    }
}
