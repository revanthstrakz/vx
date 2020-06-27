package p011de.robv.android.xposed;

import p011de.robv.android.xposed.XC_MethodHook.MethodHookParam;

/* renamed from: de.robv.android.xposed.XC_MethodReplacement */
public abstract class XC_MethodReplacement extends XC_MethodHook {
    public static final XC_MethodReplacement DO_NOTHING = new XC_MethodReplacement(20000) {
        /* access modifiers changed from: protected */
        public Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
            return null;
        }
    };

    /* access modifiers changed from: protected */
    public final void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
    }

    /* access modifiers changed from: protected */
    public abstract Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable;

    public XC_MethodReplacement() {
    }

    public XC_MethodReplacement(int i) {
        super(i);
    }

    /* access modifiers changed from: protected */
    public final void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
        try {
            methodHookParam.setResult(replaceHookedMethod(methodHookParam));
        } catch (Throwable th) {
            methodHookParam.setThrowable(th);
        }
    }

    public static XC_MethodReplacement returnConstant(Object obj) {
        return returnConstant(50, obj);
    }

    public static XC_MethodReplacement returnConstant(int i, final Object obj) {
        return new XC_MethodReplacement(i) {
            /* access modifiers changed from: protected */
            public Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return obj;
            }
        };
    }
}
