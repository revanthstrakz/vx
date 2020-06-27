package com.lody.virtual.client.hook.proxies.p005am;

import android.app.ActivityManager.RunningTaskInfo;
import android.os.IInterface;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.BinderInvocationStub;
import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.base.MethodInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastUidMethodProxy;
import com.lody.virtual.client.hook.base.ResultStaticMethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.compat.BuildCompat;
import com.lody.virtual.helper.compat.ParceledListSliceCompat;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.remote.AppTaskInfo;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import mirror.android.app.ActivityManagerNative;
import mirror.android.app.ActivityManagerOreo;
import mirror.android.app.IActivityManager;
import mirror.android.content.p016pm.ParceledListSlice;
import mirror.android.p017os.ServiceManager;
import mirror.android.util.Singleton;

@Inject(MethodProxies.class)
/* renamed from: com.lody.virtual.client.hook.proxies.am.ActivityManagerStub */
public class ActivityManagerStub extends MethodInvocationProxy<MethodInvocationStub<IInterface>> {

    /* renamed from: com.lody.virtual.client.hook.proxies.am.ActivityManagerStub$isUserRunning */
    private class isUserRunning extends MethodProxy {
        public String getMethodName() {
            return "isUserRunning";
        }

        private isUserRunning() {
        }

        public Object call(Object obj, Method method, Object... objArr) {
            boolean z = false;
            if (objArr[0].intValue() == 0) {
                z = true;
            }
            return Boolean.valueOf(z);
        }
    }

    public ActivityManagerStub() {
        super(new MethodInvocationStub(ActivityManagerNative.getDefault.call(new Object[0])));
    }

    public void inject() throws Throwable {
        if (BuildCompat.isOreo()) {
            Singleton.mInstance.set(ActivityManagerOreo.IActivityManagerSingleton.get(), getInvocationStub().getProxyInterface());
        } else if (ActivityManagerNative.gDefault.type() == IActivityManager.TYPE) {
            ActivityManagerNative.gDefault.set(getInvocationStub().getProxyInterface());
        } else if (ActivityManagerNative.gDefault.type() == Singleton.TYPE) {
            Singleton.mInstance.set(ActivityManagerNative.gDefault.get(), getInvocationStub().getProxyInterface());
        }
        BinderInvocationStub binderInvocationStub = new BinderInvocationStub((IInterface) getInvocationStub().getBaseInterface());
        binderInvocationStub.copyMethodProxies(getInvocationStub());
        ((Map) ServiceManager.sCache.get()).put(ServiceManagerNative.ACTIVITY, binderInvocationStub);
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        if (VirtualCore.get().isVAppProcess()) {
            addMethodProxy((MethodProxy) new StaticMethodProxy("navigateUpTo") {
                public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                    VLog.m87e("VA", "Call navigateUpTo!!!!", new Object[0]);
                    return method.invoke(obj, objArr);
                }
            });
            addMethodProxy((MethodProxy) new ReplaceLastUidMethodProxy("checkPermissionWithToken"));
            addMethodProxy((MethodProxy) new isUserRunning());
            addMethodProxy((MethodProxy) new ResultStaticMethodProxy("updateConfiguration", Integer.valueOf(0)));
            addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("setAppLockedVerifying"));
            addMethodProxy((MethodProxy) new StaticMethodProxy("checkUriPermission") {
                public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
                    return Integer.valueOf(0);
                }
            });
            addMethodProxy((MethodProxy) new StaticMethodProxy("getRecentTasks") {
                /* JADX WARNING: Can't wrap try/catch for region: R(5:9|(2:11|12)|13|14|20) */
                /* JADX WARNING: Failed to process nested try/catch */
                /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0044 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public java.lang.Object call(java.lang.Object r4, java.lang.reflect.Method r5, java.lang.Object... r6) throws java.lang.Throwable {
                    /*
                        r3 = this;
                        java.lang.Object r4 = r5.invoke(r4, r6)
                        boolean r5 = com.lody.virtual.helper.compat.ParceledListSliceCompat.isReturnParceledListSlice(r5)
                        if (r5 == 0) goto L_0x0016
                        mirror.RefMethod<java.util.List<?>> r5 = mirror.android.content.p016pm.ParceledListSlice.getList
                        r6 = 0
                        java.lang.Object[] r6 = new java.lang.Object[r6]
                        java.lang.Object r5 = r5.call(r4, r6)
                        java.util.List r5 = (java.util.List) r5
                        goto L_0x0019
                    L_0x0016:
                        r5 = r4
                        java.util.List r5 = (java.util.List) r5
                    L_0x0019:
                        java.util.Iterator r5 = r5.iterator()
                    L_0x001d:
                        boolean r6 = r5.hasNext()
                        if (r6 == 0) goto L_0x004f
                        java.lang.Object r6 = r5.next()
                        android.app.ActivityManager$RecentTaskInfo r6 = (android.app.ActivityManager.RecentTaskInfo) r6
                        com.lody.virtual.client.ipc.VActivityManager r0 = com.lody.virtual.client.ipc.VActivityManager.get()
                        int r1 = r6.id
                        com.lody.virtual.remote.AppTaskInfo r0 = r0.getTaskInfo(r1)
                        if (r0 != 0) goto L_0x0036
                        goto L_0x001d
                    L_0x0036:
                        int r1 = android.os.Build.VERSION.SDK_INT
                        r2 = 23
                        if (r1 < r2) goto L_0x0044
                        android.content.ComponentName r1 = r0.topActivity     // Catch:{ Throwable -> 0x0044 }
                        r6.topActivity = r1     // Catch:{ Throwable -> 0x0044 }
                        android.content.ComponentName r1 = r0.baseActivity     // Catch:{ Throwable -> 0x0044 }
                        r6.baseActivity = r1     // Catch:{ Throwable -> 0x0044 }
                    L_0x0044:
                        android.content.ComponentName r1 = r0.baseActivity     // Catch:{ Throwable -> 0x004d }
                        r6.origActivity = r1     // Catch:{ Throwable -> 0x004d }
                        android.content.Intent r0 = r0.baseIntent     // Catch:{ Throwable -> 0x004d }
                        r6.baseIntent = r0     // Catch:{ Throwable -> 0x004d }
                        goto L_0x001d
                    L_0x004d:
                        goto L_0x001d
                    L_0x004f:
                        return r4
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.client.hook.proxies.p005am.ActivityManagerStub.C09913.call(java.lang.Object, java.lang.reflect.Method, java.lang.Object[]):java.lang.Object");
                }
            });
            addMethodProxy((MethodProxy) new StaticMethodProxy("getRunningTasks") {
                public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                    Object invoke = method.invoke(obj, objArr);
                    for (RunningTaskInfo runningTaskInfo : ParceledListSliceCompat.isReturnParceledListSlice(method) ? (List) ParceledListSlice.getList.call(invoke, new Object[0]) : (List) invoke) {
                        AppTaskInfo taskInfo = VActivityManager.get().getTaskInfo(runningTaskInfo.id);
                        if (taskInfo != null) {
                            runningTaskInfo.description = "Virtual";
                            runningTaskInfo.topActivity = taskInfo.topActivity;
                            runningTaskInfo.baseActivity = taskInfo.baseActivity;
                        }
                    }
                    return invoke;
                }
            });
        }
    }

    public boolean isEnvBad() {
        return ActivityManagerNative.getDefault.call(new Object[0]) != getInvocationStub().getProxyInterface();
    }
}
