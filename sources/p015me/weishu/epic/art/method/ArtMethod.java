package p015me.weishu.epic.art.method;

import android.os.Build.VERSION;
import android.util.Log;
import com.taobao.android.dexposed.utility.Logger;
import com.taobao.android.dexposed.utility.NeverCalled;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Arrays;
import p011de.robv.android.xposed.XposedHelpers;
import p015me.weishu.epic.art.EpicNative;

/* renamed from: me.weishu.epic.art.method.ArtMethod */
public class ArtMethod {
    private static final String TAG = "ArtMethod";
    private static int artMethodSize = -1;
    private long address;
    private Constructor constructor;
    private Method method;
    private ArtMethod origin;

    public long getFieldOffset() {
        return 0;
    }

    private ArtMethod(Constructor constructor2) {
        if (constructor2 != null) {
            this.constructor = constructor2;
            init();
            return;
        }
        throw new IllegalArgumentException("constructor can not be null");
    }

    private ArtMethod(Method method2) {
        if (method2 != null) {
            this.method = method2;
            init();
            return;
        }
        throw new IllegalArgumentException("method can not be null");
    }

    private void init() {
        if (this.constructor != null) {
            this.address = EpicNative.getMethodAddress(this.constructor);
        } else {
            this.address = EpicNative.getMethodAddress(this.method);
        }
    }

    /* renamed from: of */
    public static ArtMethod m105of(Method method2) {
        return new ArtMethod(method2);
    }

    /* renamed from: of */
    public static ArtMethod m104of(Constructor constructor2) {
        return new ArtMethod(constructor2);
    }

    public ArtMethod backup() {
        ArtMethod artMethod;
        Field[] declaredFields;
        Field[] declaredFields2;
        try {
            Class superclass = Method.class.getSuperclass();
            Object executable = getExecutable();
            if (VERSION.SDK_INT < 23) {
                Class cls = Class.forName("java.lang.reflect.ArtMethod");
                Field declaredField = superclass.getDeclaredField("artMethod");
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                Object obj = declaredField.get(executable);
                Constructor declaredConstructor = cls.getDeclaredConstructor(new Class[0]);
                declaredConstructor.setAccessible(true);
                Object newInstance = declaredConstructor.newInstance(new Object[0]);
                for (Field field : cls.getDeclaredFields()) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    field.set(newInstance, field.get(obj));
                }
                Method method2 = (Method) Method.class.getConstructor(new Class[]{cls}).newInstance(new Object[]{newInstance});
                method2.setAccessible(true);
                artMethod = m105of(method2);
                artMethod.setEntryPointFromQuickCompiledCode(getEntryPointFromQuickCompiledCode());
                artMethod.setEntryPointFromJni(getEntryPointFromJni());
            } else {
                Constructor declaredConstructor2 = Method.class.getDeclaredConstructor(new Class[0]);
                Field declaredField2 = AccessibleObject.class.getDeclaredField(VERSION.SDK_INT == 23 ? "flag" : "override");
                declaredField2.setAccessible(true);
                declaredField2.set(declaredConstructor2, Boolean.valueOf(true));
                Method method3 = (Method) declaredConstructor2.newInstance(new Object[0]);
                method3.setAccessible(true);
                for (Field field2 : superclass.getDeclaredFields()) {
                    field2.setAccessible(true);
                    field2.set(method3, field2.get(executable));
                }
                Field declaredField3 = superclass.getDeclaredField("artMethod");
                declaredField3.setAccessible(true);
                int artMethodSize2 = getArtMethodSize();
                long map = EpicNative.map(artMethodSize2);
                EpicNative.put(EpicNative.get(this.address, artMethodSize2), map);
                declaredField3.set(method3, Long.valueOf(map));
                artMethod = m105of(method3);
            }
            artMethod.makePrivate();
            artMethod.setAccessible(true);
            artMethod.origin = this;
            return artMethod;
        } catch (Throwable th) {
            Log.e(TAG, "backup method error:", th);
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot create backup method from :: ");
            sb.append(getExecutable());
            throw new IllegalStateException(sb.toString(), th);
        }
    }

    public boolean isAccessible() {
        if (this.constructor != null) {
            return this.constructor.isAccessible();
        }
        return this.method.isAccessible();
    }

    public void setAccessible(boolean z) {
        if (this.constructor != null) {
            this.constructor.setAccessible(z);
        } else {
            this.method.setAccessible(z);
        }
    }

    public String getName() {
        if (this.constructor != null) {
            return this.constructor.getName();
        }
        return this.method.getName();
    }

    public Class<?> getDeclaringClass() {
        if (this.constructor != null) {
            return this.constructor.getDeclaringClass();
        }
        return this.method.getDeclaringClass();
    }

    public boolean compile() {
        if (this.constructor != null) {
            return EpicNative.compileMethod(this.constructor);
        }
        return EpicNative.compileMethod(this.method);
    }

    public Object invoke(Object obj, Object... objArr) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (VERSION.SDK_INT >= 24 && this.origin != null) {
            byte[] bArr = EpicNative.get(this.origin.address, 4);
            if (!Arrays.equals(bArr, EpicNative.get(this.address, 4))) {
                EpicNative.put(bArr, this.address);
                return invokeInternal(obj, objArr);
            }
            Logger.m95i(TAG, "the address is same with last invoke, not moved by gc");
        }
        return invokeInternal(obj, objArr);
    }

    private Object invokeInternal(Object obj, Object... objArr) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (this.constructor != null) {
            return this.constructor.newInstance(objArr);
        }
        return this.method.invoke(obj, objArr);
    }

    public int getModifiers() {
        if (this.constructor != null) {
            return this.constructor.getModifiers();
        }
        return this.method.getModifiers();
    }

    public Class<?>[] getParameterTypes() {
        if (this.constructor != null) {
            return this.constructor.getParameterTypes();
        }
        return this.method.getParameterTypes();
    }

    public Class<?> getReturnType() {
        if (this.constructor != null) {
            return Object.class;
        }
        return this.method.getReturnType();
    }

    public Class<?>[] getExceptionTypes() {
        if (this.constructor != null) {
            return this.constructor.getExceptionTypes();
        }
        return this.method.getExceptionTypes();
    }

    public String toGenericString() {
        if (this.constructor != null) {
            return this.constructor.toGenericString();
        }
        return this.method.toGenericString();
    }

    public Object getExecutable() {
        if (this.constructor != null) {
            return this.constructor;
        }
        return this.method;
    }

    public long getAddress() {
        return this.address;
    }

    public String getIdentifier() {
        return String.valueOf(getAddress());
    }

    public void makePrivate() {
        setAccessFlags((getAccessFlags() & -2) | 2);
    }

    public void ensureResolved() {
        if (!Modifier.isStatic(getModifiers())) {
            Logger.m92d(TAG, "not static, ignore.");
            return;
        }
        try {
            invoke(null, new Object[0]);
            Logger.m92d(TAG, "ensure resolved");
        } catch (Exception unused) {
        }
    }

    public long getEntryPointFromQuickCompiledCode() {
        return Offset.read(this.address, Offset.ART_QUICK_CODE_OFFSET);
    }

    public void setEntryPointFromQuickCompiledCode(long j) {
        Offset.write(this.address, Offset.ART_QUICK_CODE_OFFSET, j);
    }

    public int getAccessFlags() {
        return (int) Offset.read(this.address, Offset.ART_ACCESS_FLAG_OFFSET);
    }

    public void setAccessFlags(int i) {
        Offset.write(this.address, Offset.ART_ACCESS_FLAG_OFFSET, (long) i);
    }

    public void setEntryPointFromJni(long j) {
        Offset.write(this.address, Offset.ART_JNI_ENTRY_OFFSET, j);
    }

    public long getEntryPointFromJni() {
        return Offset.read(this.address, Offset.ART_JNI_ENTRY_OFFSET);
    }

    public static int getArtMethodSize() {
        if (artMethodSize > 0) {
            return artMethodSize;
        }
        long abs = Math.abs(EpicNative.getMethodAddress(XposedHelpers.findMethodExact(ArtMethod.class, "rule2", (Class<?>[]) new Class[0])) - EpicNative.getMethodAddress(XposedHelpers.findMethodExact(ArtMethod.class, "rule1", (Class<?>[]) new Class[0])));
        artMethodSize = (int) abs;
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("art Method size: ");
        sb.append(abs);
        Logger.m92d(str, sb.toString());
        return artMethodSize;
    }

    private void rule1() {
        Log.i(TAG, "do not inline me!!");
    }

    private void rule2() {
        Log.i(TAG, "do not inline me!!");
    }

    public static long getQuickToInterpreterBridge() {
        if (VERSION.SDK_INT < 24) {
            return -1;
        }
        return m105of(XposedHelpers.findMethodExact(NeverCalled.class, "fake", (Class<?>[]) new Class[]{Integer.TYPE})).getEntryPointFromQuickCompiledCode();
    }

    public static long searchOffset(long j, long j2, int i) {
        long j3 = j2 / 4;
        for (long j4 = 0; j4 < j3; j4++) {
            long j5 = j4 * 4;
            if (ByteBuffer.allocate(4).put(EpicNative.memget(j + j5, 4)).getInt() == i) {
                return j5;
            }
        }
        return -1;
    }

    public static long searchOffset(long j, long j2, long j3) {
        long j4 = j2 / 4;
        for (long j5 = 0; j5 < j4; j5++) {
            long j6 = j5 * 4;
            if (ByteBuffer.allocate(8).put(EpicNative.memget(j + j6, 4)).getLong() == j3) {
                return j6;
            }
        }
        return -1;
    }
}
