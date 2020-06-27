package p015me.weishu.reflection;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;
import java.lang.reflect.Method;

/* renamed from: me.weishu.reflection.Reflection */
public class Reflection {
    private static final int ERROR_EXEMPT_FAILED = -21;
    private static final int ERROR_SET_APPLICATION_FAILED = -20;
    private static final String TAG = "Reflection";
    private static int UNKNOWN = -9999;
    private static Object sVmRuntime;
    private static Method setHiddenApiExemptions;
    private static int unsealed = UNKNOWN;

    private static native int unsealNative(int i);

    static {
        if (VERSION.SDK_INT >= 28) {
            try {
                Method declaredMethod = Class.class.getDeclaredMethod("forName", new Class[]{String.class});
                Method declaredMethod2 = Class.class.getDeclaredMethod("getDeclaredMethod", new Class[]{String.class, Class[].class});
                Class cls = (Class) declaredMethod.invoke(null, new Object[]{"dalvik.system.VMRuntime"});
                Method method = (Method) declaredMethod2.invoke(cls, new Object[]{"getRuntime", null});
                setHiddenApiExemptions = (Method) declaredMethod2.invoke(cls, new Object[]{"setHiddenApiExemptions", new Class[]{String[].class}});
                sVmRuntime = method.invoke(null, new Object[0]);
            } catch (Throwable th) {
                Log.e(TAG, "reflect bootstrap failed:", th);
            }
        }
    }

    public static int unseal(Context context) {
        if (VERSION.SDK_INT >= 28 && !exemptAll()) {
            return -21;
        }
        return 0;
    }

    public static boolean exempt(String str) {
        return exempt(str);
    }

    public static boolean exempt(String... strArr) {
        if (sVmRuntime == null || setHiddenApiExemptions == null) {
            return false;
        }
        try {
            setHiddenApiExemptions.invoke(sVmRuntime, new Object[]{strArr});
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    public static boolean exemptAll() {
        return exempt("L");
    }
}
