package com.lody.virtual.client.fixer;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build.VERSION;
import android.os.DropBoxManager;
import com.lody.virtual.client.core.InvocationStubManager;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.BinderInvocationStub;
import com.lody.virtual.client.hook.proxies.dropbox.DropBoxManagerStub;
import com.lody.virtual.client.hook.proxies.graphics.GraphicsStatsStub;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.ReflectException;
import mirror.android.app.ContextImpl;
import mirror.android.app.ContextImplKitkat;
import mirror.android.content.ContentResolverJBMR2;

public class ContextFixer {
    private static final String TAG = "ContextFixer";

    public static void fixContext(Context context) {
        try {
            context.getPackageName();
            InvocationStubManager.getInstance().checkEnv(GraphicsStatsStub.class);
            int i = 0;
            while (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
                i++;
                if (i >= 10) {
                    return;
                }
            }
            ContextImpl.mPackageManager.set(context, null);
            try {
                context.getPackageManager();
            } catch (Throwable th) {
                th.printStackTrace();
            }
            if (VirtualCore.get().isVAppProcess()) {
                DropBoxManager dropBoxManager = (DropBoxManager) context.getSystemService("dropbox");
                BinderInvocationStub binderInvocationStub = (BinderInvocationStub) InvocationStubManager.getInstance().getInvocationStub(DropBoxManagerStub.class);
                if (binderInvocationStub != null) {
                    try {
                        Reflect.m80on((Object) dropBoxManager).set("mService", binderInvocationStub.getProxyInterface());
                    } catch (ReflectException e) {
                        e.printStackTrace();
                    }
                }
                String hostPkg = VirtualCore.get().getHostPkg();
                ContextImpl.mBasePackageName.set(context, hostPkg);
                if (VERSION.SDK_INT >= 19) {
                    ContextImplKitkat.mOpPackageName.set(context, hostPkg);
                }
                if (VERSION.SDK_INT >= 18) {
                    ContentResolverJBMR2.mPackageName.set(context.getContentResolver(), hostPkg);
                }
            }
        } catch (Throwable unused) {
        }
    }
}
