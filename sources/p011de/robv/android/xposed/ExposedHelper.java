package p011de.robv.android.xposed;

import android.util.Log;
import java.lang.reflect.Member;
import p011de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import p011de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import p011de.robv.android.xposed.XC_MethodHook.Unhook;

/* renamed from: de.robv.android.xposed.ExposedHelper */
public class ExposedHelper {
    private static final String TAG = "ExposedHelper";

    public static void initSeLinux(String str) {
        SELinuxHelper.initOnce();
        SELinuxHelper.initForProcess(str);
    }

    public static boolean isIXposedMod(Class<?> cls) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("module's classLoader : ");
        sb.append(cls.getClassLoader());
        sb.append(", super: ");
        sb.append(cls.getSuperclass());
        Log.d(str, sb.toString());
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("IXposedMod's classLoader : ");
        sb2.append(IXposedMod.class.getClassLoader());
        Log.d(str2, sb2.toString());
        return IXposedMod.class.isAssignableFrom(cls);
    }

    public static Unhook newUnHook(XC_MethodHook xC_MethodHook, Member member) {
        xC_MethodHook.getClass();
        return new Unhook(member);
    }

    public static void callInitZygote(String str, Object obj) throws Throwable {
        StartupParam startupParam = new StartupParam();
        startupParam.modulePath = str;
        startupParam.startsSystemServer = false;
        ((IXposedHookZygoteInit) obj).initZygote(startupParam);
    }

    public static void beforeHookedMethod(XC_MethodHook xC_MethodHook, MethodHookParam methodHookParam) throws Throwable {
        xC_MethodHook.beforeHookedMethod(methodHookParam);
    }

    public static void afterHookedMethod(XC_MethodHook xC_MethodHook, MethodHookParam methodHookParam) throws Throwable {
        xC_MethodHook.afterHookedMethod(methodHookParam);
    }
}
