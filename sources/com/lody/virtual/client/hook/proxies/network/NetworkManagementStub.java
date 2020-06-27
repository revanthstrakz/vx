package com.lody.virtual.client.hook.proxies.network;

import android.annotation.TargetApi;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceUidMethodProxy;
import mirror.android.p017os.INetworkManagementService.Stub;

@TargetApi(23)
public class NetworkManagementStub extends BinderInvocationProxy {
    public NetworkManagementStub() {
        super(Stub.asInterface, "network_management");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceUidMethodProxy("setUidCleartextNetworkPolicy", 0));
    }
}
