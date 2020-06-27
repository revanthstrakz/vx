package com.lody.virtual.helper.compat;

public class ObjectsCompat {
    public static boolean equals(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        }
        return obj.equals(obj2);
    }
}
