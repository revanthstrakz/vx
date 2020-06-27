package com.lody.virtual.client.hook.proxies.window;

import android.os.Build.VERSION;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.BinderInvocationStub;
import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import mirror.android.view.Display;
import mirror.android.view.IWindowManager.Stub;
import mirror.android.view.WindowManagerGlobal;
import mirror.com.android.internal.policy.PhoneWindow;

@Inject(MethodProxies.class)
public class WindowManagerStub extends BinderInvocationProxy {
    public WindowManagerStub() {
        super(Stub.asInterface, "window");
    }

    public void inject() throws Throwable {
        super.inject();
        if (VERSION.SDK_INT >= 17) {
            if (WindowManagerGlobal.sWindowManagerService != null) {
                WindowManagerGlobal.sWindowManagerService.set(((BinderInvocationStub) getInvocationStub()).getProxyInterface());
            }
        } else if (Display.sWindowManager != null) {
            Display.sWindowManager.set(((BinderInvocationStub) getInvocationStub()).getProxyInterface());
        }
        if (PhoneWindow.TYPE != null) {
            PhoneWindow.sWindowManager.set(((BinderInvocationStub) getInvocationStub()).getProxyInterface());
        }
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new StaticMethodProxy("addAppToken"));
        addMethodProxy((MethodProxy) new StaticMethodProxy("setScreenCaptureDisabled"));
    }
}
