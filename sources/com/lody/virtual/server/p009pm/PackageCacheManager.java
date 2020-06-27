package com.lody.virtual.server.p009pm;

import com.lody.virtual.helper.collection.ArrayMap;
import com.lody.virtual.server.p009pm.parser.PackageParserEx;
import com.lody.virtual.server.p009pm.parser.VPackage;

/* renamed from: com.lody.virtual.server.pm.PackageCacheManager */
public class PackageCacheManager {
    static final ArrayMap<String, VPackage> PACKAGE_CACHE = new ArrayMap<>();

    public static int size() {
        int size;
        synchronized (PACKAGE_CACHE) {
            size = PACKAGE_CACHE.size();
        }
        return size;
    }

    public static void put(VPackage vPackage, PackageSetting packageSetting) {
        synchronized (PackageCacheManager.class) {
            PackageParserEx.initApplicationInfoBase(packageSetting, vPackage);
            PACKAGE_CACHE.put(vPackage.packageName, vPackage);
            vPackage.mExtras = packageSetting;
            VPackageManagerService.get().analyzePackageLocked(vPackage);
        }
    }

    public static VPackage get(String str) {
        VPackage vPackage;
        synchronized (PackageCacheManager.class) {
            vPackage = (VPackage) PACKAGE_CACHE.get(str);
        }
        return vPackage;
    }

    public static PackageSetting getSetting(String str) {
        synchronized (PackageCacheManager.class) {
            VPackage vPackage = (VPackage) PACKAGE_CACHE.get(str);
            if (vPackage == null) {
                return null;
            }
            PackageSetting packageSetting = (PackageSetting) vPackage.mExtras;
            return packageSetting;
        }
    }

    public static VPackage remove(String str) {
        VPackage vPackage;
        synchronized (PackageCacheManager.class) {
            VPackageManagerService.get().deletePackageLocked(str);
            vPackage = (VPackage) PACKAGE_CACHE.remove(str);
        }
        return vPackage;
    }
}
