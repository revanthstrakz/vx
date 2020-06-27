package com.lody.virtual.client.hook.proxies.notification;

import android.os.Build.VERSION;
import android.os.IInterface;
import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.base.MethodInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.helper.compat.BuildCompat;
import com.lody.virtual.helper.utils.DeviceUtil;
import java.lang.reflect.Method;
import mirror.android.app.NotificationManager;
import mirror.android.widget.Toast;

@Inject(MethodProxies.class)
public class NotificationManagerStub extends MethodInvocationProxy<MethodInvocationStub<IInterface>> {
    public NotificationManagerStub() {
        super(new MethodInvocationStub(NotificationManager.getService.call(new Object[0])));
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("enqueueToast"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("cancelToast"));
        if (VERSION.SDK_INT >= 24) {
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("removeAutomaticZenRules"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getImportance"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("areNotificationsEnabled"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("setNotificationPolicy"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getNotificationPolicy"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("isNotificationPolicyAccessGrantedForPackage"));
        }
        if (VERSION.SDK_INT >= 26) {
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("createNotificationChannelGroups"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getNotificationChannelGroups"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("deleteNotificationChannelGroup"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("createNotificationChannels"));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getNotificationChannels") {
                public boolean beforeCall(Object obj, Method method, Object... objArr) {
                    MethodParameterUtils.replaceLastUid(objArr);
                    return super.beforeCall(obj, method, objArr);
                }
            });
            addMethodProxy((MethodProxy) new StaticMethodProxy("getNotificationChannel") {
                public boolean beforeCall(Object obj, Method method, Object... objArr) {
                    MethodParameterUtils.replaceLastUid(objArr);
                    MethodParameterUtils.replaceSequenceAppPkg(objArr, BuildCompat.isQ() ? 2 : 1);
                    return super.beforeCall(obj, method, objArr);
                }
            });
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("deleteNotificationChannel"));
        }
        if (DeviceUtil.isSamsung()) {
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("removeEdgeNotification"));
        }
    }

    public void inject() throws Throwable {
        NotificationManager.sService.set(getInvocationStub().getProxyInterface());
        Toast.sService.set(getInvocationStub().getProxyInterface());
    }

    public boolean isEnvBad() {
        return NotificationManager.getService.call(new Object[0]) != getInvocationStub().getProxyInterface();
    }
}
