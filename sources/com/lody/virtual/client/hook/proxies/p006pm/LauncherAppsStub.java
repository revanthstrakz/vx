package com.lody.virtual.client.hook.proxies.p006pm;

import android.os.IInterface;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.BinderInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import mirror.android.content.p016pm.LauncherApps;

/* renamed from: com.lody.virtual.client.hook.proxies.pm.LauncherAppsStub */
public class LauncherAppsStub extends BinderInvocationProxy {
    public LauncherAppsStub() {
        super(getInterface(), "launcherapps");
    }

    private static IInterface getInterface() {
        return (IInterface) LauncherApps.mService.get((android.content.pm.LauncherApps) VirtualCore.get().getContext().getSystemService("launcherapps"));
    }

    public void inject() throws Throwable {
        super.inject();
        LauncherApps.mService.set((android.content.pm.LauncherApps) VirtualCore.get().getContext().getSystemService("launcherapps"), ((BinderInvocationStub) getInvocationStub()).getProxyInterface());
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("addOnAppsChangedListener"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getLauncherActivities"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("resolveActivity"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("startActivityAsUser"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("showAppDetailsAsUser"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("isPackageEnabled"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("isActivityEnabled"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getApplicationInfo"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getShortcuts"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("pinShortcuts"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("startShortcut"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getShortcutIconResId"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getShortcutIconFd"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("hasShortcutHostPermission"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getShortcutConfigActivities"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getShortcutConfigActivityIntent"));
    }
}
