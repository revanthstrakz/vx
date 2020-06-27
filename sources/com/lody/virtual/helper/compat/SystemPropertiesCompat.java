package com.lody.virtual.helper.compat;

import com.lody.virtual.helper.utils.Reflect;
import java.lang.reflect.InvocationTargetException;

public class SystemPropertiesCompat {
    private static Class<?> sClass;

    private static Class getSystemPropertiesClass() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.os.SystemProperties");
        }
        return sClass;
    }

    private static String getInner(String str, String str2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        return (String) Reflect.m79on(getSystemPropertiesClass()).call("get", str, str2).get();
    }

    public static String get(String str, String str2) {
        try {
            return getInner(str, str2);
        } catch (Exception e) {
            e.printStackTrace();
            return str2;
        }
    }
}
