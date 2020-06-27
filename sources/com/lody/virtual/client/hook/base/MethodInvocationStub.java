package com.lody.virtual.client.hook.base;

import android.text.TextUtils;
import com.lody.virtual.client.hook.base.LogInvocation.Condition;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.helper.utils.VLog;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class MethodInvocationStub<T> {
    /* access modifiers changed from: private */
    public static final String TAG = "MethodInvocationStub";
    /* access modifiers changed from: private */
    public T mBaseInterface;
    private String mIdentityName;
    private Map<String, MethodProxy> mInternalMethodProxies;
    /* access modifiers changed from: private */
    public Condition mInvocationLoggingCondition;
    private T mProxyInterface;

    private class HookInvocationHandler implements InvocationHandler {
        private HookInvocationHandler() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:33:0x007a  */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x00f8  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Object invoke(java.lang.Object r9, java.lang.reflect.Method r10, java.lang.Object[] r11) throws java.lang.Throwable {
            /*
                r8 = this;
                com.lody.virtual.client.hook.base.MethodInvocationStub r9 = com.lody.virtual.client.hook.base.MethodInvocationStub.this
                java.lang.String r0 = r10.getName()
                com.lody.virtual.client.hook.base.MethodProxy r9 = r9.getMethodProxy(r0)
                r0 = 1
                r1 = 0
                if (r9 == 0) goto L_0x0016
                boolean r2 = r9.isEnable()
                if (r2 == 0) goto L_0x0016
                r2 = 1
                goto L_0x0017
            L_0x0016:
                r2 = 0
            L_0x0017:
                com.lody.virtual.client.hook.base.MethodInvocationStub r3 = com.lody.virtual.client.hook.base.MethodInvocationStub.this
                com.lody.virtual.client.hook.base.LogInvocation$Condition r3 = r3.mInvocationLoggingCondition
                com.lody.virtual.client.hook.base.LogInvocation$Condition r4 = com.lody.virtual.client.hook.base.LogInvocation.Condition.NEVER
                if (r3 != r4) goto L_0x002e
                if (r9 == 0) goto L_0x002c
                com.lody.virtual.client.hook.base.LogInvocation$Condition r3 = r9.getInvocationLoggingCondition()
                com.lody.virtual.client.hook.base.LogInvocation$Condition r4 = com.lody.virtual.client.hook.base.LogInvocation.Condition.NEVER
                if (r3 == r4) goto L_0x002c
                goto L_0x002e
            L_0x002c:
                r3 = 0
                goto L_0x002f
            L_0x002e:
                r3 = 1
            L_0x002f:
                r4 = 0
                if (r3 == 0) goto L_0x0040
                java.lang.String r5 = java.util.Arrays.toString(r11)
                int r6 = r5.length()
                int r6 = r6 - r0
                java.lang.String r5 = r5.substring(r0, r6)
                goto L_0x0041
            L_0x0040:
                r5 = r4
            L_0x0041:
                if (r2 == 0) goto L_0x006e
                com.lody.virtual.client.hook.base.MethodInvocationStub r6 = com.lody.virtual.client.hook.base.MethodInvocationStub.this     // Catch:{ Throwable -> 0x006a, all -> 0x0066 }
                java.lang.Object r6 = r6.mBaseInterface     // Catch:{ Throwable -> 0x006a, all -> 0x0066 }
                boolean r6 = r9.beforeCall(r6, r10, r11)     // Catch:{ Throwable -> 0x006a, all -> 0x0066 }
                if (r6 == 0) goto L_0x006e
                com.lody.virtual.client.hook.base.MethodInvocationStub r6 = com.lody.virtual.client.hook.base.MethodInvocationStub.this     // Catch:{ Throwable -> 0x006a, all -> 0x0066 }
                java.lang.Object r6 = r6.mBaseInterface     // Catch:{ Throwable -> 0x006a, all -> 0x0066 }
                java.lang.Object r6 = r9.call(r6, r10, r11)     // Catch:{ Throwable -> 0x006a, all -> 0x0066 }
                com.lody.virtual.client.hook.base.MethodInvocationStub r7 = com.lody.virtual.client.hook.base.MethodInvocationStub.this     // Catch:{ Throwable -> 0x0064 }
                java.lang.Object r7 = r7.mBaseInterface     // Catch:{ Throwable -> 0x0064 }
                java.lang.Object r11 = r9.afterCall(r7, r10, r11, r6)     // Catch:{ Throwable -> 0x0064 }
                goto L_0x0078
            L_0x0064:
                r11 = move-exception
                goto L_0x006c
            L_0x0066:
                r11 = move-exception
                r6 = r4
                goto L_0x00f6
            L_0x006a:
                r11 = move-exception
                r6 = r4
            L_0x006c:
                r4 = r11
                goto L_0x00df
            L_0x006e:
                com.lody.virtual.client.hook.base.MethodInvocationStub r6 = com.lody.virtual.client.hook.base.MethodInvocationStub.this     // Catch:{ Throwable -> 0x006a, all -> 0x0066 }
                java.lang.Object r6 = r6.mBaseInterface     // Catch:{ Throwable -> 0x006a, all -> 0x0066 }
                java.lang.Object r11 = r10.invoke(r6, r11)     // Catch:{ Throwable -> 0x006a, all -> 0x0066 }
            L_0x0078:
                if (r3 == 0) goto L_0x00de
                com.lody.virtual.client.hook.base.MethodInvocationStub r0 = com.lody.virtual.client.hook.base.MethodInvocationStub.this
                com.lody.virtual.client.hook.base.LogInvocation$Condition r0 = r0.mInvocationLoggingCondition
                int r0 = r0.getLogLevel(r2, r1)
                if (r9 == 0) goto L_0x0092
                com.lody.virtual.client.hook.base.LogInvocation$Condition r9 = r9.getInvocationLoggingCondition()
                int r9 = r9.getLogLevel(r2, r1)
                int r0 = java.lang.Math.max(r0, r9)
            L_0x0092:
                if (r0 < 0) goto L_0x00de
                java.lang.Class r9 = r10.getReturnType()
                java.lang.Class r1 = java.lang.Void.TYPE
                boolean r9 = r9.equals(r1)
                if (r9 == 0) goto L_0x00a3
                java.lang.String r9 = "void"
                goto L_0x00a7
            L_0x00a3:
                java.lang.String r9 = java.lang.String.valueOf(r11)
            L_0x00a7:
                java.lang.String r1 = com.lody.virtual.client.hook.base.MethodInvocationStub.TAG
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.Class r3 = r10.getDeclaringClass()
                java.lang.String r3 = r3.getSimpleName()
                r2.append(r3)
                java.lang.String r3 = "."
                r2.append(r3)
                java.lang.String r10 = r10.getName()
                r2.append(r10)
                java.lang.String r10 = "("
                r2.append(r10)
                r2.append(r5)
                java.lang.String r10 = ") => "
                r2.append(r10)
                r2.append(r9)
                java.lang.String r9 = r2.toString()
                android.util.Log.println(r0, r1, r9)
            L_0x00de:
                return r11
            L_0x00df:
                boolean r11 = r4 instanceof java.lang.reflect.InvocationTargetException     // Catch:{ all -> 0x00f5 }
                if (r11 == 0) goto L_0x00f4
                r11 = r4
                java.lang.reflect.InvocationTargetException r11 = (java.lang.reflect.InvocationTargetException) r11     // Catch:{ all -> 0x00f5 }
                java.lang.Throwable r11 = r11.getTargetException()     // Catch:{ all -> 0x00f5 }
                if (r11 == 0) goto L_0x00f4
                r11 = r4
                java.lang.reflect.InvocationTargetException r11 = (java.lang.reflect.InvocationTargetException) r11     // Catch:{ all -> 0x00f5 }
                java.lang.Throwable r11 = r11.getTargetException()     // Catch:{ all -> 0x00f5 }
                r4 = r11
            L_0x00f4:
                throw r4     // Catch:{ all -> 0x00f5 }
            L_0x00f5:
                r11 = move-exception
            L_0x00f6:
                if (r3 == 0) goto L_0x016c
                com.lody.virtual.client.hook.base.MethodInvocationStub r3 = com.lody.virtual.client.hook.base.MethodInvocationStub.this
                com.lody.virtual.client.hook.base.LogInvocation$Condition r3 = r3.mInvocationLoggingCondition
                if (r4 == 0) goto L_0x0102
                r7 = 1
                goto L_0x0103
            L_0x0102:
                r7 = 0
            L_0x0103:
                int r3 = r3.getLogLevel(r2, r7)
                if (r9 == 0) goto L_0x0119
                com.lody.virtual.client.hook.base.LogInvocation$Condition r9 = r9.getInvocationLoggingCondition()
                if (r4 == 0) goto L_0x0110
                goto L_0x0111
            L_0x0110:
                r0 = 0
            L_0x0111:
                int r9 = r9.getLogLevel(r2, r0)
                int r3 = java.lang.Math.max(r3, r9)
            L_0x0119:
                if (r3 < 0) goto L_0x016c
                if (r4 != 0) goto L_0x0131
                java.lang.Class r9 = r10.getReturnType()
                java.lang.Class r0 = java.lang.Void.TYPE
                boolean r9 = r9.equals(r0)
                if (r9 == 0) goto L_0x012c
                java.lang.String r9 = "void"
                goto L_0x0135
            L_0x012c:
                java.lang.String r9 = java.lang.String.valueOf(r6)
                goto L_0x0135
            L_0x0131:
                java.lang.String r9 = r4.toString()
            L_0x0135:
                java.lang.String r0 = com.lody.virtual.client.hook.base.MethodInvocationStub.TAG
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.Class r2 = r10.getDeclaringClass()
                java.lang.String r2 = r2.getSimpleName()
                r1.append(r2)
                java.lang.String r2 = "."
                r1.append(r2)
                java.lang.String r10 = r10.getName()
                r1.append(r10)
                java.lang.String r10 = "("
                r1.append(r10)
                r1.append(r5)
                java.lang.String r10 = ") => "
                r1.append(r10)
                r1.append(r9)
                java.lang.String r9 = r1.toString()
                android.util.Log.println(r3, r0, r9)
            L_0x016c:
                throw r11
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.client.hook.base.MethodInvocationStub.HookInvocationHandler.invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[]):java.lang.Object");
        }
    }

    public Map<String, MethodProxy> getAllHooks() {
        return this.mInternalMethodProxies;
    }

    public MethodInvocationStub(T t, Class<?>... clsArr) {
        this.mInternalMethodProxies = new HashMap();
        this.mInvocationLoggingCondition = Condition.NEVER;
        this.mBaseInterface = t;
        if (t != null) {
            if (clsArr == null) {
                clsArr = MethodParameterUtils.getAllInterface(t.getClass());
            }
            this.mProxyInterface = Proxy.newProxyInstance(t.getClass().getClassLoader(), clsArr, new HookInvocationHandler());
            return;
        }
        VLog.m91w(TAG, "Unable to build HookDelegate: %s.", getIdentityName());
    }

    public Condition getInvocationLoggingCondition() {
        return this.mInvocationLoggingCondition;
    }

    public void setInvocationLoggingCondition(Condition condition) {
        this.mInvocationLoggingCondition = condition;
    }

    public void setIdentityName(String str) {
        this.mIdentityName = str;
    }

    public String getIdentityName() {
        if (this.mIdentityName != null) {
            return this.mIdentityName;
        }
        return getClass().getSimpleName();
    }

    public MethodInvocationStub(T t) {
        this(t, null);
    }

    public void copyMethodProxies(MethodInvocationStub methodInvocationStub) {
        this.mInternalMethodProxies.putAll(methodInvocationStub.getAllHooks());
    }

    public MethodProxy addMethodProxy(MethodProxy methodProxy) {
        if (methodProxy != null && !TextUtils.isEmpty(methodProxy.getMethodName())) {
            if (this.mInternalMethodProxies.containsKey(methodProxy.getMethodName())) {
                VLog.m91w(TAG, "The Hook(%s, %s) you added has been in existence.", methodProxy.getMethodName(), methodProxy.getClass().getName());
                return methodProxy;
            }
            this.mInternalMethodProxies.put(methodProxy.getMethodName(), methodProxy);
        }
        return methodProxy;
    }

    public MethodProxy removeMethodProxy(String str) {
        return (MethodProxy) this.mInternalMethodProxies.remove(str);
    }

    public void removeMethodProxy(MethodProxy methodProxy) {
        if (methodProxy != null) {
            removeMethodProxy(methodProxy.getMethodName());
        }
    }

    public void removeAllMethodProxies() {
        this.mInternalMethodProxies.clear();
    }

    public <H extends MethodProxy> H getMethodProxy(String str) {
        return (MethodProxy) this.mInternalMethodProxies.get(str);
    }

    public T getProxyInterface() {
        return this.mProxyInterface;
    }

    public T getBaseInterface() {
        return this.mBaseInterface;
    }

    public int getMethodProxiesCount() {
        return this.mInternalMethodProxies.size();
    }

    private void dumpMethodProxies() {
        StringBuilder sb = new StringBuilder(50);
        sb.append("*********************");
        for (MethodProxy methodName : this.mInternalMethodProxies.values()) {
            sb.append(methodName.getMethodName());
            sb.append("\n");
        }
        sb.append("*********************");
        VLog.m87e(TAG, sb.toString(), new Object[0]);
    }
}
