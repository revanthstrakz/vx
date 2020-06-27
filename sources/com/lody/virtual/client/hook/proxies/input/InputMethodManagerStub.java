package com.lody.virtual.client.hook.proxies.input;

import android.annotation.TargetApi;
import android.os.IInterface;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.BinderInvocationStub;
import com.lody.virtual.client.hook.base.Inject;
import mirror.com.android.internal.view.inputmethod.InputMethodManager;

@Inject(MethodProxies.class)
@TargetApi(16)
public class InputMethodManagerStub extends BinderInvocationProxy {
    public InputMethodManagerStub() {
        super((IInterface) InputMethodManager.mService.get(VirtualCore.get().getContext().getSystemService("input_method")), "input_method");
    }

    public void inject() throws Throwable {
        InputMethodManager.mService.set(getContext().getSystemService("input_method"), ((BinderInvocationStub) getInvocationStub()).getProxyInterface());
        ((BinderInvocationStub) getInvocationStub()).replaceService("input_method");
    }

    public boolean isEnvBad() {
        return InputMethodManager.mService.get(getContext().getSystemService("input_method")) != ((BinderInvocationStub) getInvocationStub()).getBaseInterface();
    }
}
