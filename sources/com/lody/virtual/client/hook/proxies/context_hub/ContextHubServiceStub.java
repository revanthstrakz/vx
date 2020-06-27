package com.lody.virtual.client.hook.proxies.context_hub;

import android.os.Build.VERSION;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ResultStaticMethodProxy;
import mirror.android.hardware.location.IContextHubService.Stub;

public class ContextHubServiceStub extends BinderInvocationProxy {
    public ContextHubServiceStub() {
        super(Stub.asInterface, getServiceName());
    }

    private static String getServiceName() {
        return VERSION.SDK_INT >= 26 ? "contexthub" : "contexthub_service";
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("registerCallback", Integer.valueOf(0)));
    }
}
