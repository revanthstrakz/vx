package mirror;

import com.microsoft.appcenter.ingestion.models.properties.BooleanTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.DoubleTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.LongTypedProperty;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RefStaticMethod<T> {
    private Method method;

    public RefStaticMethod(Class<?> cls, Field field) throws NoSuchMethodException {
        Class cls2;
        int i = 0;
        if (!field.isAnnotationPresent(MethodParams.class)) {
            if (!field.isAnnotationPresent(MethodReflectParams.class)) {
                Method[] declaredMethods = cls.getDeclaredMethods();
                int length = declaredMethods.length;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    Method method2 = declaredMethods[i];
                    if (method2.getName().equals(field.getName())) {
                        this.method = method2;
                        this.method.setAccessible(true);
                        break;
                    }
                    i++;
                }
            } else {
                String[] value = ((MethodReflectParams) field.getAnnotation(MethodReflectParams.class)).value();
                Class[] clsArr = new Class[value.length];
                Class[] clsArr2 = new Class[value.length];
                boolean z = false;
                while (i < value.length) {
                    Class protoType = getProtoType(value[i]);
                    if (protoType == null) {
                        try {
                            protoType = Class.forName(value[i]);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    clsArr[i] = protoType;
                    if ("java.util.HashSet".equals(value[i])) {
                        try {
                            cls2 = Class.forName("android.util.ArraySet");
                        } catch (ClassNotFoundException e2) {
                            e2.printStackTrace();
                            cls2 = protoType;
                        }
                        if (cls2 != null) {
                            clsArr2[i] = cls2;
                        } else {
                            clsArr2[i] = protoType;
                        }
                        z = true;
                    } else {
                        clsArr2[i] = protoType;
                    }
                    i++;
                }
                try {
                    this.method = cls.getDeclaredMethod(field.getName(), clsArr);
                } catch (Exception e3) {
                    e3.printStackTrace();
                    if (z) {
                        this.method = cls.getDeclaredMethod(field.getName(), clsArr2);
                    }
                }
                this.method.setAccessible(true);
            }
        } else {
            Class[] value2 = ((MethodParams) field.getAnnotation(MethodParams.class)).value();
            while (i < value2.length) {
                Class cls3 = value2[i];
                if (cls3.getClassLoader() == getClass().getClassLoader()) {
                    try {
                        Class.forName(cls3.getName());
                        value2[i] = (Class) cls3.getField("TYPE").get(null);
                    } catch (Throwable th) {
                        throw new RuntimeException(th);
                    }
                }
                i++;
            }
            this.method = cls.getDeclaredMethod(field.getName(), value2);
            this.method.setAccessible(true);
        }
        if (this.method == null) {
            throw new NoSuchMethodException(field.getName());
        }
    }

    static Class<?> getProtoType(String str) {
        if (str.equals("int")) {
            return Integer.TYPE;
        }
        if (str.equals(LongTypedProperty.TYPE)) {
            return Long.TYPE;
        }
        if (str.equals(BooleanTypedProperty.TYPE)) {
            return Boolean.TYPE;
        }
        if (str.equals("byte")) {
            return Byte.TYPE;
        }
        if (str.equals("short")) {
            return Short.TYPE;
        }
        if (str.equals("char")) {
            return Character.TYPE;
        }
        if (str.equals("float")) {
            return Float.TYPE;
        }
        if (str.equals(DoubleTypedProperty.TYPE)) {
            return Double.TYPE;
        }
        if (str.equals("void")) {
            return Void.TYPE;
        }
        return null;
    }

    public T call(Object... objArr) {
        try {
            return this.method.invoke(null, objArr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public T callWithException(Object... objArr) throws Throwable {
        try {
            return this.method.invoke(null, objArr);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw e.getCause();
            }
            throw e;
        }
    }
}
