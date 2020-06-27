package com.lody.virtual.client.hook.proxies.restriction;

import android.annotation.TargetApi;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import mirror.android.content.IRestrictionsManager.Stub;

@TargetApi(21)
public class RestrictionStub extends BinderInvocationProxy {
    public RestrictionStub() {
        super(Stub.asInterface, "restrictions");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getApplicationRestrictions"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("notifyPermissionResponse"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("requestPermission"));
    }
}
