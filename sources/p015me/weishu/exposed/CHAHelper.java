package p015me.weishu.exposed;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import java.lang.reflect.Member;
import p011de.robv.android.xposed.DexposedBridge;
import p011de.robv.android.xposed.ExposedHelper;
import p011de.robv.android.xposed.XC_MethodHook;
import p011de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import p011de.robv.android.xposed.XC_MethodHook.Unhook;
import p011de.robv.android.xposed.XposedBridge;
import p011de.robv.android.xposed.XposedHelpers;

/* renamed from: me.weishu.exposed.CHAHelper */
public final class CHAHelper {
    private static final String TAG = "CHAHelper";

    /* renamed from: me.weishu.exposed.CHAHelper$ApplicationHookProxy */
    static class ApplicationHookProxy extends XC_MethodHook {
        XC_MethodHook original;

        ApplicationHookProxy(XC_MethodHook xC_MethodHook) {
            this.original = xC_MethodHook;
        }

        /* access modifiers changed from: protected */
        public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
            super.beforeHookedMethod(methodHookParam);
            if (methodHookParam.thisObject == null) {
                throw new IllegalArgumentException("can not use static method!!");
            } else if (methodHookParam.thisObject instanceof Application) {
                ExposedHelper.beforeHookedMethod(this.original, methodHookParam);
            } else {
                String str = CHAHelper.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("ignore non-application of ContextWrapper: ");
                sb.append(methodHookParam.thisObject);
                Log.d(str, sb.toString());
            }
        }

        /* access modifiers changed from: protected */
        public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
            super.afterHookedMethod(methodHookParam);
            if (methodHookParam.thisObject == null) {
                throw new IllegalArgumentException("can not use static method!!");
            } else if (methodHookParam.thisObject instanceof Application) {
                ExposedHelper.afterHookedMethod(this.original, methodHookParam);
            } else {
                String str = CHAHelper.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("ignore non-application of ContextWrapper: ");
                sb.append(methodHookParam.thisObject);
                Log.d(str, sb.toString());
            }
        }
    }

    static Unhook replaceForCHA(Member member, XC_MethodHook xC_MethodHook) {
        if (member.getDeclaringClass() == Application.class && "attach".equals(member.getName())) {
            XposedBridge.log("replace Application.attach with ContextWrapper.attachBaseContext for CHA");
            return DexposedBridge.hookMethod(XposedHelpers.findMethodExact(ContextWrapper.class, "attachBaseContext", (Class<?>[]) new Class[]{Context.class}), new ApplicationHookProxy(xC_MethodHook));
        } else if (member.getDeclaringClass() != Application.class || !"onCreate".equals(member.getName())) {
            return null;
        } else {
            XposedBridge.log("replace Application.onCreate with ContextWrapper.attachBaseContext for CHA");
            return DexposedBridge.hookMethod(XposedHelpers.findMethodExact(ContextWrapper.class, "attachBaseContext", (Class<?>[]) new Class[]{Context.class}), new ApplicationHookProxy(xC_MethodHook));
        }
    }
}
