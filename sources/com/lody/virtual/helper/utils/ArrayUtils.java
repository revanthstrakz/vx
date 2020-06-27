package com.lody.virtual.helper.utils;

import com.lody.virtual.helper.compat.ObjectsCompat;

public class ArrayUtils {
    public static Object[] push(Object[] objArr, Object obj) {
        Object[] objArr2 = new Object[(objArr.length + 1)];
        System.arraycopy(objArr, 0, objArr2, 0, objArr.length);
        objArr2[objArr.length] = obj;
        return objArr2;
    }

    public static <T> boolean contains(T[] tArr, T t) {
        return indexOf(tArr, t) != -1;
    }

    public static boolean contains(int[] iArr, int i) {
        if (iArr == null) {
            return false;
        }
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    public static <T> int indexOf(T[] tArr, T t) {
        if (tArr == null) {
            return -1;
        }
        for (int i = 0; i < tArr.length; i++) {
            if (ObjectsCompat.equals(tArr[i], t)) {
                return i;
            }
        }
        return -1;
    }

    public static int protoIndexOf(Class<?>[] clsArr, Class<?> cls) {
        if (clsArr == null) {
            return -1;
        }
        for (int i = 0; i < clsArr.length; i++) {
            if (clsArr[i] == cls) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOfFirst(Object[] objArr, Class<?> cls) {
        if (!isEmpty(objArr)) {
            int i = -1;
            for (Object obj : objArr) {
                i++;
                if (obj != null && cls == obj.getClass()) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int protoIndexOf(Class<?>[] clsArr, Class<?> cls, int i) {
        if (clsArr == null) {
            return -1;
        }
        while (i < clsArr.length) {
            if (cls == clsArr[i]) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static int indexOfObject(Object[] objArr, Class<?> cls, int i) {
        if (objArr == null) {
            return -1;
        }
        while (i < objArr.length) {
            if (cls.isInstance(objArr[i])) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static int indexOf(Object[] objArr, Class<?> cls, int i) {
        if (!isEmpty(objArr)) {
            int i2 = i;
            int i3 = -1;
            for (Object obj : objArr) {
                i3++;
                if (obj != null && obj.getClass() == cls) {
                    i2--;
                    if (i2 <= 0) {
                        return i3;
                    }
                }
            }
        }
        return -1;
    }

    public static int indexOfLast(Object[] objArr, Class<?> cls) {
        if (!isEmpty(objArr)) {
            for (int length = objArr.length; length > 0; length--) {
                int i = length - 1;
                Object obj = objArr[i];
                if (obj != null && obj.getClass() == cls) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static <T> boolean isEmpty(T[] tArr) {
        return tArr == null || tArr.length == 0;
    }

    public static <T> T getFirst(Object[] objArr, Class<?> cls) {
        int indexOfFirst = indexOfFirst(objArr, cls);
        if (indexOfFirst != -1) {
            return objArr[indexOfFirst];
        }
        return null;
    }

    public static void checkOffsetAndCount(int i, int i2, int i3) throws ArrayIndexOutOfBoundsException {
        if ((i2 | i3) < 0 || i2 > i || i - i2 < i3) {
            throw new ArrayIndexOutOfBoundsException(i2);
        }
    }
}
