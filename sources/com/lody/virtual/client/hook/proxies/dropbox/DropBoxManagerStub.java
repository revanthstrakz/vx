package com.lody.virtual.client.hook.proxies.dropbox;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ResultStaticMethodProxy;
import mirror.com.android.internal.p018os.IDropBoxManagerService.Stub;

public class DropBoxManagerStub extends BinderInvocationProxy {
    public DropBoxManagerStub() {
        super(Stub.asInterface, "dropbox");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getNextEntry", null));
    }
}
