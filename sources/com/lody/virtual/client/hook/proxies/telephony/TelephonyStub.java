package com.lody.virtual.client.hook.proxies.telephony;

import android.support.p001v4.app.NotificationCompat;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import mirror.com.android.internal.telephony.ITelephony.Stub;

@Inject(MethodProxies.class)
public class TelephonyStub extends BinderInvocationProxy {
    public TelephonyStub() {
        super(Stub.asInterface, "phone");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("isOffhook"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getLine1NumberForDisplay"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("isOffhookForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("isRingingForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy(NotificationCompat.CATEGORY_CALL));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("isRinging"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("isIdle"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("isIdleForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("isRadioOn"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("isRadioOnForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("isSimPinEnabled"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getCdmaEriIconIndex"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getCdmaEriIconIndexForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getCdmaEriIconMode"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getCdmaEriIconModeForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getCdmaEriText"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getCdmaEriTextForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getNetworkTypeForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getDataNetworkType"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getDataNetworkTypeForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getVoiceNetworkTypeForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getLteOnCdmaMode"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getLteOnCdmaModeForSubscriber"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getCalculatedPreferredNetworkType"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getPcscfAddress"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getLine1AlphaTagForDisplay"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getMergedSubscriberIds"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getRadioAccessFamily"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("isVideoCallingEnabled"));
    }
}
