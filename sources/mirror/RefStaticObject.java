package mirror;

import java.lang.reflect.Field;

public class RefStaticObject<T> {
    private Field field;

    public RefStaticObject(Class<?> cls, Field field2) throws NoSuchFieldException {
        this.field = cls.getDeclaredField(field2.getName());
        this.field.setAccessible(true);
    }

    public Class<?> type() {
        return this.field.getType();
    }

    public T get() {
        try {
            return this.field.get(null);
        } catch (Exception unused) {
            return null;
        }
    }

    public void set(T t) {
        try {
            this.field.set(null, t);
        } catch (Exception unused) {
        }
    }
}
