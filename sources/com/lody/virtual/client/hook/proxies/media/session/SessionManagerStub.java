package com.lody.virtual.client.hook.proxies.media.session;

import android.annotation.TargetApi;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import mirror.android.media.session.ISessionManager.Stub;

@TargetApi(21)
public class SessionManagerStub extends BinderInvocationProxy {
    public SessionManagerStub() {
        super(Stub.asInterface, "media_session");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("createSession"));
    }
}
