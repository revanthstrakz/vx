package com.lody.virtual.client.hook.utils;

import android.os.Process;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.ArrayUtils;
import java.util.Arrays;
import java.util.HashSet;

public class MethodParameterUtils {
    public static <T> T getFirstParam(Object[] objArr, Class<T> cls) {
        if (objArr == null) {
            return null;
        }
        int indexOfFirst = ArrayUtils.indexOfFirst(objArr, cls);
        if (indexOfFirst != -1) {
            return objArr[indexOfFirst];
        }
        return null;
    }

    public static String replaceFirstAppPkg(Object[] objArr) {
        if (objArr == null) {
            return null;
        }
        int indexOfFirst = ArrayUtils.indexOfFirst(objArr, String.class);
        if (indexOfFirst == -1) {
            return null;
        }
        String str = objArr[indexOfFirst];
        objArr[indexOfFirst] = VirtualCore.get().getHostPkg();
        return str;
    }

    public static String replaceLastAppPkg(Object[] objArr) {
        int indexOfLast = ArrayUtils.indexOfLast(objArr, String.class);
        if (indexOfLast == -1) {
            return null;
        }
        String str = objArr[indexOfLast];
        objArr[indexOfLast] = VirtualCore.get().getHostPkg();
        return str;
    }

    public static void replaceLastUid(Object[] objArr) {
        int indexOfLast = ArrayUtils.indexOfLast(objArr, Integer.class);
        if (indexOfLast != -1 && objArr[indexOfLast].intValue() == Process.myUid()) {
            objArr[indexOfLast] = Integer.valueOf(VirtualCore.get().myUid());
        }
    }

    public static String replaceSequenceAppPkg(Object[] objArr, int i) {
        int indexOf = ArrayUtils.indexOf(objArr, String.class, i);
        if (indexOf == -1) {
            return null;
        }
        String str = objArr[indexOf];
        objArr[indexOf] = VirtualCore.get().getHostPkg();
        return str;
    }

    public static Class<?>[] getAllInterface(Class cls) {
        HashSet hashSet = new HashSet();
        getAllInterfaces(cls, hashSet);
        Class<?>[] clsArr = new Class[hashSet.size()];
        hashSet.toArray(clsArr);
        return clsArr;
    }

    public static void getAllInterfaces(Class cls, HashSet<Class<?>> hashSet) {
        Class[] interfaces = cls.getInterfaces();
        if (interfaces.length != 0) {
            hashSet.addAll(Arrays.asList(interfaces));
        }
        if (cls.getSuperclass() != Object.class) {
            getAllInterfaces(cls.getSuperclass(), hashSet);
        }
    }
}
