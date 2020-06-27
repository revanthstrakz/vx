package com.lody.virtual.helper.compat;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import mirror.android.content.p016pm.ParceledListSlice;
import mirror.android.content.p016pm.ParceledListSliceJBMR2;

public class ParceledListSliceCompat {
    public static boolean isReturnParceledListSlice(Method method) {
        return method != null && method.getReturnType() == ParceledListSlice.TYPE;
    }

    public static Object create(List list) {
        if (ParceledListSliceJBMR2.ctor != null) {
            return ParceledListSliceJBMR2.ctor.newInstance(list);
        }
        Object newInstance = ParceledListSlice.ctor.newInstance();
        for (Object next : list) {
            ParceledListSlice.append.call(newInstance, next);
        }
        ParceledListSlice.setLastSlice.call(newInstance, Boolean.valueOf(true));
        return newInstance;
    }

    public static List getList(Object obj) {
        if (obj == null || obj.getClass() != ParceledListSlice.TYPE) {
            return Collections.EMPTY_LIST;
        }
        if (ParceledListSliceJBMR2.getList != null) {
            return (List) ParceledListSliceJBMR2.getList.call(obj, new Object[0]);
        }
        return (List) ParceledListSlice.getList.call(obj, new Object[0]);
    }
}
