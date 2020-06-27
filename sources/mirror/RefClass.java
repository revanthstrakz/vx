package mirror;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public final class RefClass {
    private static HashMap<Class<?>, Constructor<?>> REF_TYPES = new HashMap<>();

    static {
        try {
            REF_TYPES.put(RefObject.class, RefObject.class.getConstructor(new Class[]{Class.class, Field.class}));
            REF_TYPES.put(RefMethod.class, RefMethod.class.getConstructor(new Class[]{Class.class, Field.class}));
            REF_TYPES.put(RefInt.class, RefInt.class.getConstructor(new Class[]{Class.class, Field.class}));
            REF_TYPES.put(RefLong.class, RefLong.class.getConstructor(new Class[]{Class.class, Field.class}));
            REF_TYPES.put(RefFloat.class, RefFloat.class.getConstructor(new Class[]{Class.class, Field.class}));
            REF_TYPES.put(RefDouble.class, RefDouble.class.getConstructor(new Class[]{Class.class, Field.class}));
            REF_TYPES.put(RefBoolean.class, RefBoolean.class.getConstructor(new Class[]{Class.class, Field.class}));
            REF_TYPES.put(RefStaticObject.class, RefStaticObject.class.getConstructor(new Class[]{Class.class, Field.class}));
            REF_TYPES.put(RefStaticInt.class, RefStaticInt.class.getConstructor(new Class[]{Class.class, Field.class}));
            REF_TYPES.put(RefStaticMethod.class, RefStaticMethod.class.getConstructor(new Class[]{Class.class, Field.class}));
            REF_TYPES.put(RefConstructor.class, RefConstructor.class.getConstructor(new Class[]{Class.class, Field.class}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> load(Class<?> cls, String str) {
        try {
            return load((Class) cls, Class.forName(str));
        } catch (Exception unused) {
            return null;
        }
    }

    public static Class load(Class cls, Class<?> cls2) {
        Field[] declaredFields;
        for (Field field : cls.getDeclaredFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    Constructor constructor = (Constructor) REF_TYPES.get(field.getType());
                    if (constructor != null) {
                        field.set(null, constructor.newInstance(new Object[]{cls2, field}));
                    }
                }
            } catch (Exception unused) {
            }
        }
        return cls2;
    }
}
