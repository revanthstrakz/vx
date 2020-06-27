package p011de.robv.android.xposed;

import android.app.AndroidAppHelper;
import android.os.Build.VERSION;
import android.util.Log;
import com.taobao.android.dexposed.utility.Logger;
import com.taobao.android.dexposed.utility.Runtime;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import p011de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import p011de.robv.android.xposed.XC_MethodHook.Unhook;
import p011de.robv.android.xposed.XposedHelpers.InvocationTargetError;
import p015me.weishu.epic.art.Epic;
import p015me.weishu.epic.art.method.ArtMethod;
import p015me.weishu.reflection.Reflection;

/* renamed from: de.robv.android.xposed.DexposedBridge */
public final class DexposedBridge {
    public static final ClassLoader BOOTCLASSLOADER = ClassLoader.getSystemClassLoader();
    /* access modifiers changed from: private */
    public static final Object[] EMPTY_ARRAY = new Object[0];
    private static final String TAG = "DexposedBridge";
    private static final ArrayList<Unhook> allUnhookCallbacks = new ArrayList<>();
    private static final Map<Member, CopyOnWriteSortedSet<XC_MethodHook>> hookedMethodCallbacks = new HashMap();

    /* renamed from: de.robv.android.xposed.DexposedBridge$AdditionalHookInfo */
    private static class AdditionalHookInfo {
        final CopyOnWriteSortedSet<XC_MethodHook> callbacks;
        final Class<?>[] parameterTypes;
        final Class<?> returnType;

        private AdditionalHookInfo(CopyOnWriteSortedSet<XC_MethodHook> copyOnWriteSortedSet, Class<?>[] clsArr, Class<?> cls) {
            this.callbacks = copyOnWriteSortedSet;
            this.parameterTypes = clsArr;
            this.returnType = cls;
        }
    }

    /* renamed from: de.robv.android.xposed.DexposedBridge$CopyOnWriteSortedSet */
    public static class CopyOnWriteSortedSet<E> {
        private volatile transient Object[] elements = DexposedBridge.EMPTY_ARRAY;

        public synchronized boolean add(E e) {
            if (indexOf(e) >= 0) {
                return false;
            }
            Object[] objArr = new Object[(this.elements.length + 1)];
            System.arraycopy(this.elements, 0, objArr, 0, this.elements.length);
            objArr[this.elements.length] = e;
            Arrays.sort(objArr);
            this.elements = objArr;
            return true;
        }

        public synchronized boolean remove(E e) {
            int indexOf = indexOf(e);
            if (indexOf == -1) {
                return false;
            }
            Object[] objArr = new Object[(this.elements.length - 1)];
            System.arraycopy(this.elements, 0, objArr, 0, indexOf);
            System.arraycopy(this.elements, indexOf + 1, objArr, indexOf, (this.elements.length - indexOf) - 1);
            this.elements = objArr;
            return true;
        }

        public synchronized void clear() {
            this.elements = DexposedBridge.EMPTY_ARRAY;
        }

        private int indexOf(Object obj) {
            for (int i = 0; i < this.elements.length; i++) {
                if (obj.equals(this.elements[i])) {
                    return i;
                }
            }
            return -1;
        }

        public Object[] getSnapshot() {
            return this.elements;
        }
    }

    private static native synchronized void hookMethodNative(Member member, Class<?> cls, int i, Object obj);

    private static native Object invokeOriginalMethodNative(Member member, int i, Class<?>[] clsArr, Class<?> cls, Object obj, Object[] objArr) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    private static native Object invokeSuperNative(Object obj, Object[] objArr, Member member, Class<?> cls, Class<?>[] clsArr, Class<?> cls2, int i) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    static {
        try {
            if (VERSION.SDK_INT > 19) {
                System.loadLibrary("epic");
            } else if (VERSION.SDK_INT > 14) {
                System.loadLibrary("dexposed");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("unsupported api level: ");
                sb.append(VERSION.SDK_INT);
                throw new RuntimeException(sb.toString());
            }
            Reflection.unseal(AndroidAppHelper.currentApplication());
        } catch (Throwable th) {
            log(th);
        }
    }

    public static synchronized void log(String str) {
        synchronized (DexposedBridge.class) {
            Log.i(TAG, str);
        }
    }

    public static synchronized void log(Throwable th) {
        synchronized (DexposedBridge.class) {
            log(Log.getStackTraceString(th));
        }
    }

    public static Unhook hookMethod(Member member, XC_MethodHook xC_MethodHook) {
        CopyOnWriteSortedSet copyOnWriteSortedSet;
        Class[] clsArr;
        Class cls;
        boolean z = member instanceof Method;
        if (z || (member instanceof Constructor)) {
            boolean z2 = false;
            synchronized (hookedMethodCallbacks) {
                copyOnWriteSortedSet = (CopyOnWriteSortedSet) hookedMethodCallbacks.get(member);
                if (copyOnWriteSortedSet == null) {
                    copyOnWriteSortedSet = new CopyOnWriteSortedSet();
                    hookedMethodCallbacks.put(member, copyOnWriteSortedSet);
                    z2 = true;
                }
            }
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("hook: ");
            sb.append(member);
            sb.append(", newMethod ? ");
            sb.append(z2);
            Logger.m96w(str, sb.toString());
            copyOnWriteSortedSet.add(xC_MethodHook);
            if (z2) {
                if (!Runtime.isArt()) {
                    Class declaringClass = member.getDeclaringClass();
                    int intField = XposedHelpers.getIntField(member, "slot");
                    if (z) {
                        Method method = (Method) member;
                        clsArr = method.getParameterTypes();
                        cls = method.getReturnType();
                    } else {
                        clsArr = ((Constructor) member).getParameterTypes();
                        cls = null;
                    }
                    hookMethodNative(member, declaringClass, intField, new AdditionalHookInfo(copyOnWriteSortedSet, clsArr, cls));
                } else if (z) {
                    Epic.hookMethod((Method) member);
                } else {
                    Epic.hookMethod((Constructor) member);
                }
            }
            xC_MethodHook.getClass();
            return new Unhook(member);
        }
        throw new IllegalArgumentException("only methods and constructors can be hooked");
    }

    public static void unhookMethod(Member member, XC_MethodHook xC_MethodHook) {
        synchronized (hookedMethodCallbacks) {
            CopyOnWriteSortedSet copyOnWriteSortedSet = (CopyOnWriteSortedSet) hookedMethodCallbacks.get(member);
            if (copyOnWriteSortedSet != null) {
                copyOnWriteSortedSet.remove(xC_MethodHook);
            }
        }
    }

    public static Set<Unhook> hookAllMethods(Class<?> cls, String str, XC_MethodHook xC_MethodHook) {
        Method[] declaredMethods;
        HashSet hashSet = new HashSet();
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getName().equals(str)) {
                hashSet.add(hookMethod(method, xC_MethodHook));
            }
        }
        return hashSet;
    }

    public static Unhook findAndHookMethod(Class<?> cls, String str, Object... objArr) {
        if (objArr.length == 0 || !(objArr[objArr.length - 1] instanceof XC_MethodHook)) {
            throw new IllegalArgumentException("no callback defined");
        }
        Unhook hookMethod = hookMethod(XposedHelpers.findMethodExact(cls, str, objArr), objArr[objArr.length - 1]);
        synchronized (allUnhookCallbacks) {
            allUnhookCallbacks.add(hookMethod);
        }
        return hookMethod;
    }

    public static void unhookAllMethods() {
        synchronized (allUnhookCallbacks) {
            for (int i = 0; i < allUnhookCallbacks.size(); i++) {
                ((Unhook) allUnhookCallbacks.get(i)).unhook();
            }
            allUnhookCallbacks.clear();
        }
    }

    public static Set<Unhook> hookAllConstructors(Class<?> cls, XC_MethodHook xC_MethodHook) {
        HashSet hashSet = new HashSet();
        for (Constructor hookMethod : cls.getDeclaredConstructors()) {
            hashSet.add(hookMethod(hookMethod, xC_MethodHook));
        }
        return hashSet;
    }

    public static Object handleHookedArtMethod(Object obj, Object obj2, Object[] objArr) {
        CopyOnWriteSortedSet copyOnWriteSortedSet;
        ArtMethod artMethod = (ArtMethod) obj;
        synchronized (hookedMethodCallbacks) {
            copyOnWriteSortedSet = (CopyOnWriteSortedSet) hookedMethodCallbacks.get(artMethod.getExecutable());
        }
        Object[] snapshot = copyOnWriteSortedSet.getSnapshot();
        int length = snapshot.length;
        if (length == 0) {
            try {
                return Epic.getBackMethod(artMethod).invoke(obj2, objArr);
            } catch (Exception e) {
                log(e.getCause());
            }
        }
        MethodHookParam methodHookParam = new MethodHookParam();
        methodHookParam.method = (Member) artMethod.getExecutable();
        methodHookParam.thisObject = obj2;
        methodHookParam.args = objArr;
        int i = 0;
        while (true) {
            try {
                ((XC_MethodHook) snapshot[i]).beforeHookedMethod(methodHookParam);
                if (methodHookParam.returnEarly) {
                    i++;
                    break;
                }
            } catch (Throwable th) {
                log(th);
                methodHookParam.setResult(null);
                methodHookParam.returnEarly = false;
            }
            i++;
            if (i >= length) {
                break;
            }
        }
        if (!methodHookParam.returnEarly) {
            try {
                methodHookParam.setResult(Epic.getBackMethod(artMethod).invoke(obj2, objArr));
            } catch (Exception e2) {
                methodHookParam.setThrowable(e2);
            }
        }
        int i2 = i - 1;
        do {
            Object result = methodHookParam.getResult();
            Throwable throwable = methodHookParam.getThrowable();
            try {
                ((XC_MethodHook) snapshot[i2]).afterHookedMethod(methodHookParam);
            } catch (Throwable th2) {
                log(th2);
                if (throwable == null) {
                    methodHookParam.setResult(result);
                } else {
                    methodHookParam.setThrowable(throwable);
                }
            }
            i2--;
        } while (i2 >= 0);
        if (!methodHookParam.hasThrowable()) {
            return methodHookParam.getResult();
        }
        Throwable throwable2 = methodHookParam.getThrowable();
        if ((throwable2 instanceof IllegalAccessException) || (throwable2 instanceof InvocationTargetException) || (throwable2 instanceof InstantiationException)) {
            throwable2.getCause();
            throwNoCheck(methodHookParam.getThrowable().getCause(), null);
            return null;
        }
        Logger.m94e(TAG, "epic cause exception in call bridge!!", throwable2);
        return null;
    }

    private static <T extends Throwable> void throwNoCheck(Throwable th, Object obj) throws Throwable {
        throw th;
    }

    private static Object handleHookedMethod(Member member, int i, Object obj, Object obj2, Object[] objArr) throws Throwable {
        AdditionalHookInfo additionalHookInfo = (AdditionalHookInfo) obj;
        Object[] snapshot = additionalHookInfo.callbacks.getSnapshot();
        int length = snapshot.length;
        if (length == 0) {
            try {
                return invokeOriginalMethodNative(member, i, additionalHookInfo.parameterTypes, additionalHookInfo.returnType, obj2, objArr);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        } else {
            MethodHookParam methodHookParam = new MethodHookParam();
            methodHookParam.method = member;
            methodHookParam.thisObject = obj2;
            methodHookParam.args = objArr;
            int i2 = 0;
            while (true) {
                try {
                    ((XC_MethodHook) snapshot[i2]).beforeHookedMethod(methodHookParam);
                    if (methodHookParam.returnEarly) {
                        i2++;
                        break;
                    }
                } catch (Throwable th) {
                    log(th);
                    methodHookParam.setResult(null);
                    methodHookParam.returnEarly = false;
                }
                i2++;
                if (i2 >= length) {
                    break;
                }
            }
            if (!methodHookParam.returnEarly) {
                try {
                    methodHookParam.setResult(invokeOriginalMethodNative(member, i, additionalHookInfo.parameterTypes, additionalHookInfo.returnType, methodHookParam.thisObject, methodHookParam.args));
                } catch (InvocationTargetException e2) {
                    methodHookParam.setThrowable(e2.getCause());
                }
            }
            int i3 = i2 - 1;
            do {
                Object result = methodHookParam.getResult();
                Throwable throwable = methodHookParam.getThrowable();
                try {
                    ((XC_MethodHook) snapshot[i3]).afterHookedMethod(methodHookParam);
                } catch (Throwable th2) {
                    log(th2);
                    if (throwable == null) {
                        methodHookParam.setResult(result);
                    } else {
                        methodHookParam.setThrowable(throwable);
                    }
                }
                i3--;
            } while (i3 >= 0);
            if (!methodHookParam.hasThrowable()) {
                return methodHookParam.getResult();
            }
            throw methodHookParam.getThrowable();
        }
    }

    public static Object invokeSuper(Object obj, Member member, Object... objArr) throws NoSuchFieldException {
        try {
            return invokeSuperNative(obj, objArr, member, member.getDeclaringClass(), ((Method) member).getParameterTypes(), ((Method) member).getReturnType(), !Runtime.isArt() ? XposedHelpers.getIntField(XposedHelpers.findMethodExact(obj.getClass().getSuperclass(), member.getName(), (Class<?>[]) ((Method) member).getParameterTypes()), "slot") : 0);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        } catch (IllegalArgumentException e2) {
            throw e2;
        } catch (InvocationTargetException e3) {
            throw new InvocationTargetError(e3.getCause());
        }
    }

    public static Object invokeOriginalMethod(Member member, Object obj, Object[] objArr) throws NullPointerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class cls;
        Class[] clsArr;
        ArtMethod artMethod;
        if (objArr == null) {
            objArr = EMPTY_ARRAY;
        }
        Object[] objArr2 = objArr;
        boolean z = member instanceof Method;
        if (z) {
            Method method = (Method) member;
            clsArr = method.getParameterTypes();
            cls = method.getReturnType();
        } else if (member instanceof Constructor) {
            cls = null;
            clsArr = ((Constructor) member).getParameterTypes();
        } else {
            throw new IllegalArgumentException("method must be of type Method or Constructor");
        }
        if (Runtime.isArt()) {
            if (z) {
                artMethod = ArtMethod.m105of((Method) member);
            } else {
                artMethod = ArtMethod.m104of((Constructor) member);
            }
            try {
                return Epic.getBackMethod(artMethod).invoke(obj, objArr2);
            } catch (InstantiationException e) {
                throwNoCheck(e, null);
            }
        }
        return invokeOriginalMethodNative(member, 0, clsArr, cls, obj, objArr2);
    }
}
