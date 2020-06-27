package com.lody.virtual.client.hook.proxies.imms;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.ReplaceSpecPkgMethodProxy;
import mirror.com.android.internal.telephony.IMms.Stub;

public class MmsStub extends BinderInvocationProxy {
    public MmsStub() {
        super(Stub.asInterface, "imms");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendMessage", 1));
        addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("downloadMessage", 1));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("importTextMessage"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("importMultimediaMessage"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("deleteStoredMessage"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("deleteStoredConversation"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("updateStoredMessageStatus"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("archiveStoredConversation"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("addTextMessageDraft"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("addMultimediaMessageDraft"));
        addMethodProxy((MethodProxy) new ReplaceSpecPkgMethodProxy("sendStoredMessage", 1));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("setAutoPersisting"));
    }
}
