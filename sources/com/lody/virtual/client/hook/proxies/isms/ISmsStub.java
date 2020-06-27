package com.lody.virtual.client.hook.proxies.isms;

import android.os.Build.VERSION;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.ReplaceSpecPkgMethodProxy;
import mirror.com.android.internal.telephony.ISms.Stub;

public class ISmsStub extends BinderInvocationProxy {
    public ISmsStub() {
        super(Stub.asInterface, "isms");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        if (VERSION.SDK_INT >= 23) {
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("getAllMessagesFromIccEfForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("updateMessageOnIccEfForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("copyMessageToIccEfForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendDataForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendDataForSubscriberWithSelfPermissions", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendTextForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendTextForSubscriberWithSelfPermissions", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendMultipartTextForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendStoredText", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendStoredMultipartText", 1));
        } else if (VERSION.SDK_INT >= 21) {
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getAllMessagesFromIccEf"));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("getAllMessagesFromIccEfForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("updateMessageOnIccEf"));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("updateMessageOnIccEfForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("copyMessageToIccEf"));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("copyMessageToIccEfForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("sendData"));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendDataForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("sendText"));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendTextForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("sendMultipartText"));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendMultipartTextForSubscriber", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendStoredText", 1));
            addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendStoredMultipartText", 1));
        } else if (VERSION.SDK_INT >= 18) {
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getAllMessagesFromIccEf"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("updateMessageOnIccEf"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("copyMessageToIccEf"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("sendData"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("sendText"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("sendMultipartText"));
        }
    }
}
