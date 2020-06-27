package com.lody.virtual.client.hook.proxies.phonesubinfo;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import mirror.com.android.internal.telephony.IPhoneSubInfo.Stub;

@Inject(MethodProxies.class)
public class PhoneSubInfoStub extends BinderInvocationProxy {
    public PhoneSubInfoStub() {
        super(Stub.asInterface, "iphonesubinfo");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getNaiForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getImeiForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getDeviceSvn"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getDeviceSvnUsingSubId"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getSubscriberId"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getSubscriberIdForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getGroupIdLevel1"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getGroupIdLevel1ForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getLine1Number"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getLine1NumberForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getLine1AlphaTag"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getLine1AlphaTagForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getMsisdn"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getMsisdnForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getVoiceMailNumber"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getVoiceMailNumberForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getVoiceMailAlphaTag"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getVoiceMailAlphaTagForSubscriber"));
    }
}
