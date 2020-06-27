package com.lody.virtual.helper.utils;

import com.android.launcher3.IconCache;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import p013io.virtualapp.utils.HanziToPinyin.Token;

public class Reflect {
    private final boolean isClass = true;
    /* access modifiers changed from: private */
    public final Object object;

    private static class NULL {
        private NULL() {
        }
    }

    private Reflect(Class<?> cls) {
        this.object = cls;
    }

    private Reflect(Object obj) {
        this.object = obj;
    }

    /* renamed from: on */
    public static Reflect m81on(String str) throws ReflectException {
        return m79on(forName(str));
    }

    /* renamed from: on */
    public static Reflect m82on(String str, ClassLoader classLoader) throws ReflectException {
        return m79on(forName(str, classLoader));
    }

    /* renamed from: on */
    public static Reflect m79on(Class<?> cls) {
        return new Reflect(cls);
    }

    /* renamed from: on */
    public static Reflect m80on(Object obj) {
        return new Reflect(obj);
    }

    public static <T extends AccessibleObject> T accessible(T t) {
        if (t == null) {
            return null;
        }
        if (t instanceof Member) {
            Member member = (Member) t;
            if (Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                return t;
            }
        }
        if (!t.isAccessible()) {
            t.setAccessible(true);
        }
        return t;
    }

    /* access modifiers changed from: private */
    public static String property(String str) {
        int length = str.length();
        if (length == 0) {
            return "";
        }
        if (length == 1) {
            return str.toLowerCase();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(0, 1).toLowerCase());
        sb.append(str.substring(1));
        return sb.toString();
    }

    /* renamed from: on */
    private static Reflect m83on(Constructor<?> constructor, Object... objArr) throws ReflectException {
        try {
            return m80on(((Constructor) accessible(constructor)).newInstance(objArr));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /* renamed from: on */
    private static Reflect m84on(Method method, Object obj, Object... objArr) throws ReflectException {
        try {
            accessible(method);
            if (method.getReturnType() != Void.TYPE) {
                return m80on(method.invoke(obj, objArr));
            }
            method.invoke(obj, objArr);
            return m80on(obj);
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private static Object unwrap(Object obj) {
        return obj instanceof Reflect ? ((Reflect) obj).get() : obj;
    }

    private static Class<?>[] types(Object... objArr) {
        if (objArr == null) {
            return new Class[0];
        }
        Class[] clsArr = new Class[objArr.length];
        for (int i = 0; i < objArr.length; i++) {
            Object obj = objArr[i];
            clsArr[i] = obj == null ? NULL.class : obj.getClass();
        }
        return clsArr;
    }

    private static Class<?> forName(String str) throws ReflectException {
        try {
            return Class.forName(str);
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private static Class<?> forName(String str, ClassLoader classLoader) throws ReflectException {
        try {
            return Class.forName(str, true, classLoader);
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    public static Class<?> wrapper(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        if (cls.isPrimitive()) {
            if (Boolean.TYPE == cls) {
                return Boolean.class;
            }
            if (Integer.TYPE == cls) {
                return Integer.class;
            }
            if (Long.TYPE == cls) {
                return Long.class;
            }
            if (Short.TYPE == cls) {
                return Short.class;
            }
            if (Byte.TYPE == cls) {
                return Byte.class;
            }
            if (Double.TYPE == cls) {
                return Double.class;
            }
            if (Float.TYPE == cls) {
                return Float.class;
            }
            if (Character.TYPE == cls) {
                return Character.class;
            }
            if (Void.TYPE == cls) {
                return Void.class;
            }
        }
        return cls;
    }

    public <T> T get() {
        return this.object;
    }

    public Reflect set(String str, Object obj) throws ReflectException {
        try {
            Field field0 = field0(str);
            field0.setAccessible(true);
            field0.set(this.object, unwrap(obj));
            return this;
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    public <T> T get(String str) throws ReflectException {
        return field(str).get();
    }

    public Reflect field(String str) throws ReflectException {
        try {
            return m80on(field0(str).get(this.object));
        } catch (Exception e) {
            throw new ReflectException(this.object.getClass().getName(), e);
        }
    }

    private Field field0(String str) throws ReflectException {
        Class type = type();
        try {
            return type.getField(str);
        } catch (NoSuchFieldException e) {
            do {
                try {
                    return (Field) accessible(type.getDeclaredField(str));
                } catch (NoSuchFieldException unused) {
                    type = type.getSuperclass();
                    if (type == null) {
                        throw new ReflectException(e);
                    }
                }
            } while (type == null);
            throw new ReflectException(e);
        }
    }

    public Map<String, Reflect> fields() {
        Field[] declaredFields;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Class type = type();
        do {
            for (Field field : type.getDeclaredFields()) {
                if ((!this.isClass) ^ Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    if (!linkedHashMap.containsKey(name)) {
                        linkedHashMap.put(name, field(name));
                    }
                }
            }
            type = type.getSuperclass();
        } while (type != null);
        return linkedHashMap;
    }

    public Reflect call(String str) throws ReflectException {
        return call(str, new Object[0]);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(3:4|5|6) */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0019, code lost:
        return m84on(similarMethod(r4, r0), r3.object, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001a, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0020, code lost:
        throw new com.lody.virtual.helper.utils.ReflectException(r4);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000f */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.lody.virtual.helper.utils.Reflect call(java.lang.String r4, java.lang.Object... r5) throws com.lody.virtual.helper.utils.ReflectException {
        /*
            r3 = this;
            java.lang.Class[] r0 = types(r5)
            java.lang.reflect.Method r1 = r3.exactMethod(r4, r0)     // Catch:{ NoSuchMethodException -> 0x000f }
            java.lang.Object r2 = r3.object     // Catch:{ NoSuchMethodException -> 0x000f }
            com.lody.virtual.helper.utils.Reflect r1 = m84on(r1, r2, r5)     // Catch:{ NoSuchMethodException -> 0x000f }
            return r1
        L_0x000f:
            java.lang.reflect.Method r4 = r3.similarMethod(r4, r0)     // Catch:{ NoSuchMethodException -> 0x001a }
            java.lang.Object r0 = r3.object     // Catch:{ NoSuchMethodException -> 0x001a }
            com.lody.virtual.helper.utils.Reflect r4 = m84on(r4, r0, r5)     // Catch:{ NoSuchMethodException -> 0x001a }
            return r4
        L_0x001a:
            r4 = move-exception
            com.lody.virtual.helper.utils.ReflectException r5 = new com.lody.virtual.helper.utils.ReflectException
            r5.<init>(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.helper.utils.Reflect.call(java.lang.String, java.lang.Object[]):com.lody.virtual.helper.utils.Reflect");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(3:4|5|6) */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
        throw new java.lang.NoSuchMethodException();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000d, code lost:
        return r0.getDeclaredMethod(r3, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000e, code lost:
        r0 = r0.getSuperclass();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
        if (r0 != null) goto L_0x0009;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x0009 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.reflect.Method exactMethod(java.lang.String r3, java.lang.Class<?>[] r4) throws java.lang.NoSuchMethodException {
        /*
            r2 = this;
            java.lang.Class r0 = r2.type()
            java.lang.reflect.Method r1 = r0.getMethod(r3, r4)     // Catch:{ NoSuchMethodException -> 0x0009 }
            return r1
        L_0x0009:
            java.lang.reflect.Method r1 = r0.getDeclaredMethod(r3, r4)     // Catch:{ NoSuchMethodException -> 0x000e }
            return r1
        L_0x000e:
            java.lang.Class r0 = r0.getSuperclass()
            if (r0 == 0) goto L_0x0015
            goto L_0x0009
        L_0x0015:
            java.lang.NoSuchMethodException r3 = new java.lang.NoSuchMethodException
            r3.<init>()
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.helper.utils.Reflect.exactMethod(java.lang.String, java.lang.Class[]):java.lang.reflect.Method");
    }

    private Method similarMethod(String str, Class<?>[] clsArr) throws NoSuchMethodException {
        Method[] methods;
        Method[] declaredMethods;
        Class type = type();
        for (Method method : type.getMethods()) {
            if (isSimilarSignature(method, str, clsArr)) {
                return method;
            }
        }
        do {
            for (Method method2 : type.getDeclaredMethods()) {
                if (isSimilarSignature(method2, str, clsArr)) {
                    return method2;
                }
            }
            type = type.getSuperclass();
        } while (type != null);
        StringBuilder sb = new StringBuilder();
        sb.append("No similar method ");
        sb.append(str);
        sb.append(" with params ");
        sb.append(Arrays.toString(clsArr));
        sb.append(" could be found on type ");
        sb.append(type());
        sb.append(IconCache.EMPTY_CLASS_NAME);
        throw new NoSuchMethodException(sb.toString());
    }

    private boolean isSimilarSignature(Method method, String str, Class<?>[] clsArr) {
        return method.getName().equals(str) && match(method.getParameterTypes(), clsArr);
    }

    public Reflect create() throws ReflectException {
        return create(new Object[0]);
    }

    public Reflect create(Object... objArr) throws ReflectException {
        Constructor[] declaredConstructors;
        Class[] types = types(objArr);
        try {
            return m83on(type().getDeclaredConstructor(types), objArr);
        } catch (NoSuchMethodException e) {
            for (Constructor constructor : type().getDeclaredConstructors()) {
                if (match(constructor.getParameterTypes(), types)) {
                    return m83on(constructor, objArr);
                }
            }
            throw new ReflectException(e);
        }
    }

    /* renamed from: as */
    public <P> P mo14018as(Class<P> cls) {
        final boolean z = this.object instanceof Map;
        C10741 r1 = new InvocationHandler() {
            public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                int i;
                String name = method.getName();
                try {
                    return Reflect.m80on(Reflect.this.object).call(name, objArr).get();
                } catch (ReflectException e) {
                    if (z) {
                        Map map = (Map) Reflect.this.object;
                        if (objArr == null) {
                            i = 0;
                        } else {
                            i = objArr.length;
                        }
                        if (i == 0 && name.startsWith("get")) {
                            return map.get(Reflect.property(name.substring(3)));
                        }
                        if (i == 0 && name.startsWith("is")) {
                            return map.get(Reflect.property(name.substring(2)));
                        }
                        if (i == 1 && name.startsWith("set")) {
                            map.put(Reflect.property(name.substring(3)), objArr[0]);
                            return null;
                        }
                    }
                    throw e;
                }
            }
        };
        return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, r1);
    }

    private boolean match(Class<?>[] clsArr, Class<?>[] clsArr2) {
        if (clsArr.length != clsArr2.length) {
            return false;
        }
        for (int i = 0; i < clsArr2.length; i++) {
            if (clsArr2[i] != NULL.class && !wrapper(clsArr[i]).isAssignableFrom(wrapper(clsArr2[i]))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return this.object.hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof Reflect) && this.object.equals(((Reflect) obj).get());
    }

    public String toString() {
        return this.object.toString();
    }

    public Class<?> type() {
        if (this.isClass) {
            return (Class) this.object;
        }
        return this.object.getClass();
    }

    public static String getMethodDetails(Method method) {
        StringBuilder sb = new StringBuilder(40);
        sb.append(Modifier.toString(method.getModifiers()));
        sb.append(Token.SEPARATOR);
        sb.append(method.getReturnType().getName());
        sb.append(Token.SEPARATOR);
        sb.append(method.getName());
        sb.append("(");
        Class[] parameterTypes = method.getParameterTypes();
        for (Class name : parameterTypes) {
            sb.append(name.getName());
            sb.append(", ");
        }
        if (parameterTypes.length > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }

    public Reflect callBest(String str, Object... objArr) throws ReflectException {
        Class[] types = types(objArr);
        Method[] declaredMethods = type().getDeclaredMethods();
        int length = declaredMethods.length;
        Method method = null;
        int i = 0;
        char c = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Method method2 = declaredMethods[i];
            if (isSimilarSignature(method2, str, types)) {
                c = 2;
                method = method2;
                break;
            }
            if (matchObjectMethod(method2, str, types)) {
                method = method2;
                c = 1;
            } else if (method2.getName().equals(str) && method2.getParameterTypes().length == 0 && c == 0) {
                method = method2;
            }
            i++;
        }
        if (method != null) {
            if (c == 0) {
                objArr = new Object[0];
            }
            return m84on(method, this.object, c == 1 ? new Object[]{objArr} : objArr);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("no method found for ");
        sb.append(str);
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("No best method ");
        sb3.append(str);
        sb3.append(" with params ");
        sb3.append(Arrays.toString(types));
        sb3.append(" could be found on type ");
        sb3.append(type());
        sb3.append(IconCache.EMPTY_CLASS_NAME);
        throw new ReflectException(sb2, new NoSuchMethodException(sb3.toString()));
    }

    private boolean matchObjectMethod(Method method, String str, Class<?>[] clsArr) {
        return method.getName().equals(str) && matchObject(method.getParameterTypes());
    }

    private boolean matchObject(Class<?>[] clsArr) {
        return clsArr.length > 0 && clsArr[0].isAssignableFrom(Object[].class);
    }
}
