package com.lody.virtual.client.fixer;

import android.content.pm.ComponentInfo;
import android.text.TextUtils;
import com.lody.virtual.server.p009pm.PackageSetting;

public class ComponentFixer {
    public static String fixComponentClassName(String str, String str2) {
        if (str2 == null) {
            return null;
        }
        if (str2.charAt(0) != '.') {
            return str2;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(str2);
        return sb.toString();
    }

    public static void fixComponentInfo(PackageSetting packageSetting, ComponentInfo componentInfo, int i) {
        if (componentInfo != null) {
            if (TextUtils.isEmpty(componentInfo.processName)) {
                componentInfo.processName = componentInfo.packageName;
            }
            componentInfo.name = fixComponentClassName(componentInfo.packageName, componentInfo.name);
            if (componentInfo.processName == null) {
                componentInfo.processName = componentInfo.applicationInfo.processName;
            }
        }
    }
}
