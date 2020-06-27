package com.lody.virtual.client.hook.proxies.graphics;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import mirror.android.view.IGraphicsStats.Stub;

public class GraphicsStatsStub extends BinderInvocationProxy {
    public GraphicsStatsStub() {
        super(Stub.asInterface, "graphicsstats");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("requestBufferForProcess"));
    }
}
