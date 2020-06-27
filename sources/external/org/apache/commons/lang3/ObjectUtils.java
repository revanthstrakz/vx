package external.org.apache.commons.lang3;

import external.org.apache.commons.lang3.exception.CloneFailedException;
import external.org.apache.commons.lang3.mutable.MutableInt;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

public class ObjectUtils {
    public static final Null NULL = new Null();

    public static class Null implements Serializable {
        private static final long serialVersionUID = 7092611880189329093L;

        Null() {
        }

        private Object readResolve() {
            return ObjectUtils.NULL;
        }
    }

    public static <T> T defaultIfNull(T t, T t2) {
        return t != null ? t : t2;
    }

    public static <T> T firstNonNull(T... tArr) {
        if (tArr != null) {
            for (T t : tArr) {
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    public static boolean equals(Object obj, Object obj2) {
        if (obj == obj2) {
            return true;
        }
        if (obj == null || obj2 == null) {
            return false;
        }
        return obj.equals(obj2);
    }

    public static boolean notEqual(Object obj, Object obj2) {
        return !equals(obj, obj2);
    }

    public static int hashCode(Object obj) {
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    public static int hashCodeMulti(Object... objArr) {
        int i = 1;
        if (objArr != null) {
            for (Object hashCode : objArr) {
                i = (i * 31) + hashCode(hashCode);
            }
        }
        return i;
    }

    public static String identityToString(Object obj) {
        if (obj == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        identityToString(stringBuffer, obj);
        return stringBuffer.toString();
    }

    public static void identityToString(StringBuffer stringBuffer, Object obj) {
        if (obj != null) {
            stringBuffer.append(obj.getClass().getName());
            stringBuffer.append('@');
            stringBuffer.append(Integer.toHexString(System.identityHashCode(obj)));
            return;
        }
        throw new NullPointerException("Cannot get the toString of a null identity");
    }

    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static String toString(Object obj, String str) {
        return obj == null ? str : obj.toString();
    }

    public static <T extends Comparable<? super T>> T min(T... tArr) {
        T t = null;
        if (tArr != null) {
            for (T t2 : tArr) {
                if (compare(t2, t, true) < 0) {
                    t = t2;
                }
            }
        }
        return t;
    }

    public static <T extends Comparable<? super T>> T max(T... tArr) {
        if (tArr == null) {
            return null;
        }
        T t = null;
        for (T t2 : tArr) {
            if (compare(t2, t, false) > 0) {
                t = t2;
            }
        }
        return t;
    }

    public static <T extends Comparable<? super T>> int compare(T t, T t2) {
        return compare(t, t2, false);
    }

    public static <T extends Comparable<? super T>> int compare(T t, T t2, boolean z) {
        if (t == t2) {
            return 0;
        }
        int i = -1;
        if (t == null) {
            if (z) {
                i = 1;
            }
            return i;
        } else if (t2 != null) {
            return t.compareTo(t2);
        } else {
            if (!z) {
                i = 1;
            }
            return i;
        }
    }

    public static <T extends Comparable<? super T>> T median(T... tArr) {
        Validate.notEmpty(tArr);
        Validate.noNullElements(tArr);
        TreeSet treeSet = new TreeSet();
        Collections.addAll(treeSet, tArr);
        return (Comparable) treeSet.toArray()[(treeSet.size() - 1) / 2];
    }

    public static <T> T median(Comparator<T> comparator, T... tArr) {
        Validate.notEmpty(tArr, "null/empty items", new Object[0]);
        Validate.noNullElements(tArr);
        Validate.notNull(comparator, "null comparator", new Object[0]);
        TreeSet treeSet = new TreeSet(comparator);
        Collections.addAll(treeSet, tArr);
        return treeSet.toArray()[(treeSet.size() - 1) / 2];
    }

    public static <T> T mode(T... tArr) {
        if (!ArrayUtils.isNotEmpty(tArr)) {
            return null;
        }
        HashMap hashMap = new HashMap(tArr.length);
        int i = 0;
        for (T t : tArr) {
            MutableInt mutableInt = (MutableInt) hashMap.get(t);
            if (mutableInt == null) {
                hashMap.put(t, new MutableInt(1));
            } else {
                mutableInt.increment();
            }
        }
        Iterator it = hashMap.entrySet().iterator();
        while (true) {
            T t2 = null;
            while (true) {
                if (!it.hasNext()) {
                    return t2;
                }
                Entry entry = (Entry) it.next();
                int intValue = ((MutableInt) entry.getValue()).intValue();
                if (intValue != i) {
                    if (intValue > i) {
                        t2 = entry.getKey();
                        i = intValue;
                    }
                }
            }
        }
    }

    public static <T> T clone(T t) {
        T t2;
        T t3;
        if (!(t instanceof Cloneable)) {
            return null;
        }
        if (t.getClass().isArray()) {
            Class componentType = t.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                int length = Array.getLength(t);
                t3 = Array.newInstance(componentType, length);
                while (true) {
                    int i = length - 1;
                    if (length <= 0) {
                        break;
                    }
                    Array.set(t3, i, Array.get(t, i));
                    length = i;
                }
            } else {
                t2 = ((Object[]) t).clone();
                return t2;
            }
        } else {
            try {
                t3 = t.getClass().getMethod("clone", new Class[0]).invoke(t, new Object[0]);
            } catch (NoSuchMethodException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cloneable type ");
                sb.append(t.getClass().getName());
                sb.append(" has no clone method");
                throw new CloneFailedException(sb.toString(), e);
            } catch (IllegalAccessException e2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Cannot clone Cloneable type ");
                sb2.append(t.getClass().getName());
                throw new CloneFailedException(sb2.toString(), e2);
            } catch (InvocationTargetException e3) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Exception cloning Cloneable type ");
                sb3.append(t.getClass().getName());
                throw new CloneFailedException(sb3.toString(), e3.getCause());
            }
        }
        t2 = t3;
        return t2;
    }

    public static <T> T cloneIfPossible(T t) {
        Object clone = clone(t);
        return clone == null ? t : clone;
    }
}
