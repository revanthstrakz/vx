package com.lody.virtual.client.hook.proxies.isub;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import mirror.com.android.internal.telephony.ISub.Stub;

public class ISubStub extends BinderInvocationProxy {
    public ISubStub() {
        super(Stub.asInterface, "isub");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getAllSubInfoList"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getAllSubInfoCount"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getActiveSubscriptionInfo"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getActiveSubscriptionInfoForIccId"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getActiveSubscriptionInfoForSimSlotIndex"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getActiveSubscriptionInfoList"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getActiveSubInfoCount"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getSubscriptionProperty"));
    }
}
