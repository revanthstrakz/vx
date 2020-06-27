package com.lody.virtual.client.hook.base;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.LogInvocation.Condition;
import com.lody.virtual.client.ipc.VirtualLocationManager;
import com.lody.virtual.helper.utils.ComponentUtils;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.VDeviceInfo;
import java.lang.reflect.Method;

public abstract class MethodProxy {
    private boolean enable = true;
    private Condition mInvocationLoggingCondition = Condition.NEVER;

    public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
        return obj2;
    }

    public boolean beforeCall(Object obj, Method method, Object... objArr) {
        return true;
    }

    public abstract String getMethodName();

    public MethodProxy() {
        LogInvocation logInvocation = (LogInvocation) getClass().getAnnotation(LogInvocation.class);
        if (logInvocation != null) {
            this.mInvocationLoggingCondition = logInvocation.value();
        }
    }

    public static String getHostPkg() {
        return VirtualCore.get().getHostPkg();
    }

    public static String getAppPkg() {
        return VClientImpl.get().getCurrentPackage();
    }

    protected static Context getHostContext() {
        return VirtualCore.get().getContext();
    }

    protected static boolean isAppProcess() {
        return VirtualCore.get().isVAppProcess();
    }

    protected static boolean isServerProcess() {
        return VirtualCore.get().isServerProcess();
    }

    protected static boolean isMainProcess() {
        return VirtualCore.get().isMainProcess();
    }

    protected static int getVUid() {
        return VClientImpl.get().getVUid();
    }

    public static int getAppUserId() {
        return VUserHandle.getUserId(getVUid());
    }

    protected static int getBaseVUid() {
        return VClientImpl.get().getBaseVUid();
    }

    protected static int getRealUid() {
        return VirtualCore.get().myUid();
    }

    protected static VDeviceInfo getDeviceInfo() {
        return VClientImpl.get().getDeviceInfo();
    }

    protected static boolean isFakeLocationEnable() {
        return VirtualLocationManager.get().getMode(VUserHandle.myUserId(), VClientImpl.get().getCurrentPackage()) != 0;
    }

    public static boolean isVisiblePackage(ApplicationInfo applicationInfo) {
        return getHostPkg().equals(applicationInfo.packageName) || ComponentUtils.isSystemApp(applicationInfo) || VirtualCore.get().isOutsidePackageVisible(applicationInfo.packageName);
    }

    public Object call(Object obj, Method method, Object... objArr) throws Throwable {
        return method.invoke(obj, objArr);
    }

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean z) {
        this.enable = z;
    }

    public Condition getInvocationLoggingCondition() {
        return this.mInvocationLoggingCondition;
    }

    public void setInvocationloggingCondition(Condition condition) {
        this.mInvocationLoggingCondition = condition;
    }

    public boolean isAppPkg(String str) {
        return VirtualCore.get().isAppInstalled(str);
    }

    /* access modifiers changed from: protected */
    public PackageManager getPM() {
        return VirtualCore.getPM();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Method : ");
        sb.append(getMethodName());
        return sb.toString();
    }
}
