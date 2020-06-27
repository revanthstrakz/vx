package com.lody.virtual.client.hook.secondary;

import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.ReflectException;

public class HackAppUtils {
    public static void enableQQLogOutput(String str, ClassLoader classLoader) {
        if ("com.tencent.mobileqq".equals(str)) {
            try {
                Reflect.m82on("com.tencent.qphone.base.util.QLog", classLoader).set("UIN_REPORTLOG_LEVEL", Integer.valueOf(100));
            } catch (ReflectException unused) {
            }
        }
    }
}
