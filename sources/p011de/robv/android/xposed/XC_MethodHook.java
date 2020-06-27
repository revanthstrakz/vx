package p011de.robv.android.xposed;

import java.lang.reflect.Member;
import p011de.robv.android.xposed.callbacks.IXUnhook;
import p011de.robv.android.xposed.callbacks.XCallback;
import p011de.robv.android.xposed.callbacks.XCallback.Param;

/* renamed from: de.robv.android.xposed.XC_MethodHook */
public abstract class XC_MethodHook extends XCallback {

    /* renamed from: de.robv.android.xposed.XC_MethodHook$MethodHookParam */
    public static class MethodHookParam extends Param {
        public Object[] args;
        public Member method;
        private Object result = null;
        boolean returnEarly = false;
        public Object thisObject;
        private Throwable throwable = null;

        public Object getResult() {
            return this.result;
        }

        public void setResult(Object obj) {
            this.result = obj;
            this.throwable = null;
            this.returnEarly = true;
        }

        public Throwable getThrowable() {
            return this.throwable;
        }

        public boolean hasThrowable() {
            return this.throwable != null;
        }

        public void setThrowable(Throwable th) {
            this.throwable = th;
            this.result = null;
            this.returnEarly = true;
        }

        public Object getResultOrThrowable() throws Throwable {
            if (this.throwable == null) {
                return this.result;
            }
            throw this.throwable;
        }
    }

    /* renamed from: de.robv.android.xposed.XC_MethodHook$Unhook */
    public class Unhook implements IXUnhook<XC_MethodHook> {
        private final Member hookMethod;

        Unhook(Member member) {
            this.hookMethod = member;
        }

        public Member getHookedMethod() {
            return this.hookMethod;
        }

        public XC_MethodHook getCallback() {
            return XC_MethodHook.this;
        }

        public void unhook() {
            XposedBridge.unhookMethod(this.hookMethod, XC_MethodHook.this);
        }
    }

    /* access modifiers changed from: protected */
    public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
    }

    /* access modifiers changed from: protected */
    public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
    }

    public XC_MethodHook() {
    }

    public XC_MethodHook(int i) {
        super(i);
    }
}
