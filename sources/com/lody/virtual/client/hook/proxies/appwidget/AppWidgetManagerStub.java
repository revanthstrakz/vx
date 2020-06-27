package com.lody.virtual.client.hook.proxies.appwidget;

import android.annotation.TargetApi;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ResultStaticMethodProxy;
import mirror.com.android.internal.appwidget.IAppWidgetService.Stub;

@TargetApi(21)
public class AppWidgetManagerStub extends BinderInvocationProxy {
    public AppWidgetManagerStub() {
        super(Stub.asInterface, "appwidget");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("startListening", new int[0]));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("stopListening", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("allocateAppWidgetId", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("deleteAppWidgetId", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("deleteHost", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("deleteAllHosts", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getAppWidgetViews", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getAppWidgetIdsForHost", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("createAppWidgetConfigIntentSender", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("updateAppWidgetIds", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("updateAppWidgetOptions", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getAppWidgetOptions", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("partiallyUpdateAppWidgetIds", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("updateAppWidgetProvider", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("notifyAppWidgetViewDataChanged", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getInstalledProvidersForProfile", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getAppWidgetInfo", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("hasBindAppWidgetPermission", Boolean.valueOf(false)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("setBindAppWidgetPermission", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("bindAppWidgetId", Boolean.valueOf(false)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("bindRemoteViewsService", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("unbindRemoteViewsService", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getAppWidgetIds", new int[0]));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("isBoundWidgetPackage", Boolean.valueOf(false)));
    }
}
