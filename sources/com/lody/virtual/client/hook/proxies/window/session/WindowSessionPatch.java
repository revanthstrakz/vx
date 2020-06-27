package com.lody.virtual.client.hook.proxies.window.session;

import android.os.IInterface;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.base.MethodInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;

public class WindowSessionPatch extends MethodInvocationProxy<MethodInvocationStub<IInterface>> {
    public void inject() throws Throwable {
    }

    public WindowSessionPatch(IInterface iInterface) {
        super(new MethodInvocationStub(iInterface));
    }

    public void onBindMethods() {
        addMethodProxy((MethodProxy) new BaseMethodProxy("add"));
        addMethodProxy((MethodProxy) new BaseMethodProxy("addToDisplay"));
        addMethodProxy((MethodProxy) new BaseMethodProxy("addToDisplayWithoutInputChannel"));
        addMethodProxy((MethodProxy) new BaseMethodProxy("addWithoutInputChannel"));
        addMethodProxy((MethodProxy) new BaseMethodProxy("relayout"));
    }

    public boolean isEnvBad() {
        return getInvocationStub().getProxyInterface() != null;
    }
}
