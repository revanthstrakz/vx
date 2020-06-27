package com.lody.virtual.client.hook.proxies.connectivity;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import mirror.android.net.IConnectivityManager.Stub;

public class ConnectivityStub extends BinderInvocationProxy {
    public ConnectivityStub() {
        super(Stub.asInterface, "connectivity");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
    }
}
