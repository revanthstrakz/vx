package com.lody.virtual.client.hook.proxies.clipboard;

import android.os.Build.VERSION;
import android.os.IInterface;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.BinderInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import com.lody.virtual.helper.compat.BuildCompat;
import com.lody.virtual.helper.utils.DeviceUtil;
import mirror.android.content.ClipboardManager;
import mirror.android.content.ClipboardManagerOreo;

public class ClipBoardStub extends BinderInvocationProxy {
    public ClipBoardStub() {
        super(getInterface(), "clipboard");
    }

    private static IInterface getInterface() {
        if (!isOreo()) {
            return (IInterface) ClipboardManager.getService.call(new Object[0]);
        }
        return (IInterface) ClipboardManagerOreo.mService.get((android.content.ClipboardManager) VirtualCore.get().getContext().getSystemService("clipboard"));
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getPrimaryClip"));
        if (VERSION.SDK_INT > 17) {
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setPrimaryClip"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getPrimaryClipDescription"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("hasPrimaryClip"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("addPrimaryClipChangedListener"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("removePrimaryClipChangedListener"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("hasClipboardText"));
        }
    }

    public void inject() throws Throwable {
        super.inject();
        if (isOreo()) {
            ClipboardManagerOreo.mService.set((android.content.ClipboardManager) VirtualCore.get().getContext().getSystemService("clipboard"), ((BinderInvocationStub) getInvocationStub()).getProxyInterface());
            return;
        }
        ClipboardManager.sService.set(((BinderInvocationStub) getInvocationStub()).getProxyInterface());
    }

    private static boolean isOreo() {
        return (BuildCompat.isOreo() && !DeviceUtil.isSamsung()) || ClipboardManager.getService == null;
    }
}
