package com.lody.virtual.client.hook.base;

import android.os.Process;
import com.lody.virtual.helper.utils.ArrayUtils;
import java.lang.reflect.Method;

public class ReplaceLastUidMethodProxy extends StaticMethodProxy {
    public ReplaceLastUidMethodProxy(String str) {
        super(str);
    }

    public boolean beforeCall(Object obj, Method method, Object... objArr) {
        int indexOfLast = ArrayUtils.indexOfLast(objArr, Integer.class);
        if (indexOfLast != -1 && objArr[indexOfLast].intValue() == Process.myUid()) {
            objArr[indexOfLast] = Integer.valueOf(getRealUid());
        }
        return super.beforeCall(obj, method, objArr);
    }
}
