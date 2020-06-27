package com.lody.virtual.client.hook.proxies.libcore;

import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.base.MethodInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceUidMethodProxy;
import mirror.libcore.p019io.ForwardingOs;
import mirror.libcore.p019io.Libcore;

@Inject(MethodProxies.class)
public class LibCoreStub extends MethodInvocationProxy<MethodInvocationStub<Object>> {
    public LibCoreStub() {
        super(new MethodInvocationStub(getOs()));
    }

    private static Object getOs() {
        Object obj = Libcore.f214os.get();
        if (ForwardingOs.f213os == null) {
            return obj;
        }
        Object obj2 = ForwardingOs.f213os.get(obj);
        return obj2 != null ? obj2 : obj;
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceUidMethodProxy("chown", 1));
        addMethodProxy((MethodProxy) new ReplaceUidMethodProxy("fchown", 1));
        addMethodProxy((MethodProxy) new ReplaceUidMethodProxy("getpwuid", 0));
        addMethodProxy((MethodProxy) new ReplaceUidMethodProxy("lchown", 1));
        addMethodProxy((MethodProxy) new ReplaceUidMethodProxy("setuid", 0));
    }

    public void inject() throws Throwable {
        Libcore.f214os.set(getInvocationStub().getProxyInterface());
    }

    public boolean isEnvBad() {
        return getOs() != getInvocationStub().getProxyInterface();
    }
}
