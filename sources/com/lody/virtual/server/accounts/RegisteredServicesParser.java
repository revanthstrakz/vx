package com.lody.virtual.server.accounts;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import com.lody.virtual.server.p009pm.PackageCacheManager;
import com.lody.virtual.server.p009pm.PackageSetting;

public class RegisteredServicesParser {
    public XmlResourceParser getParser(Context context, ServiceInfo serviceInfo, String str) {
        Bundle bundle = serviceInfo.metaData;
        if (bundle != null) {
            int i = bundle.getInt(str);
            if (i != 0) {
                try {
                    return getResources(context, serviceInfo.applicationInfo).getXml(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public Resources getResources(Context context, ApplicationInfo applicationInfo) throws Exception {
        PackageSetting setting = PackageCacheManager.getSetting(applicationInfo.packageName);
        if (setting == null) {
            return null;
        }
        AssetManager assetManager = (AssetManager) mirror.android.content.res.AssetManager.ctor.newInstance();
        mirror.android.content.res.AssetManager.addAssetPath.call(assetManager, setting.apkPath);
        Resources resources = context.getResources();
        return new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
    }
}
