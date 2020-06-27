package com.lody.virtual.helper.compat;

import android.content.p000pm.PackageParser;
import android.content.p000pm.PackageParser.Activity;
import android.content.p000pm.PackageParser.Package;
import android.content.p000pm.PackageParser.Provider;
import android.content.p000pm.PackageParser.Service;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.os.Process;
import android.util.DisplayMetrics;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.p007os.VUserHandle;
import java.io.File;
import mirror.android.content.p016pm.PackageParserJellyBean;
import mirror.android.content.p016pm.PackageParserJellyBean17;
import mirror.android.content.p016pm.PackageParserLollipop;
import mirror.android.content.p016pm.PackageParserLollipop22;
import mirror.android.content.p016pm.PackageParserMarshmallow;
import mirror.android.content.p016pm.PackageParserNougat;
import mirror.android.content.p016pm.PackageParserP28;
import mirror.android.content.p016pm.PackageParserP28.CallbackImpl;
import mirror.android.content.p016pm.PackageUserState;

public class PackageParserCompat {
    private static final int API_LEVEL = VERSION.SDK_INT;
    public static final int[] GIDS = VirtualCore.get().getGids();
    private static final int myUserId = VUserHandle.getUserId(Process.myUid());
    private static final Object sUserState = (API_LEVEL >= 17 ? PackageUserState.ctor.newInstance() : null);

    public static PackageParser createParser(File file) {
        if (API_LEVEL >= 23) {
            return (PackageParser) PackageParserMarshmallow.ctor.newInstance();
        }
        if (API_LEVEL >= 22) {
            return (PackageParser) PackageParserLollipop22.ctor.newInstance();
        }
        if (API_LEVEL >= 21) {
            return (PackageParser) PackageParserLollipop.ctor.newInstance();
        }
        if (API_LEVEL >= 17) {
            return (PackageParser) PackageParserJellyBean17.ctor.newInstance(file.getAbsolutePath());
        } else if (API_LEVEL >= 16) {
            return (PackageParser) PackageParserJellyBean.ctor.newInstance(file.getAbsolutePath());
        } else {
            return (PackageParser) mirror.android.content.p016pm.PackageParser.ctor.newInstance(file.getAbsolutePath());
        }
    }

    public static Package parsePackage(PackageParser packageParser, File file, int i) throws Throwable {
        if (BuildCompat.isQ()) {
            PackageParserP28.setCallback.call(packageParser, CallbackImpl.ctor.newInstance(VirtualCore.getPM()));
        }
        if (API_LEVEL >= 23) {
            return (Package) PackageParserMarshmallow.parsePackage.callWithException(packageParser, file, Integer.valueOf(i));
        } else if (API_LEVEL >= 22) {
            return (Package) PackageParserLollipop22.parsePackage.callWithException(packageParser, file, Integer.valueOf(i));
        } else if (API_LEVEL >= 21) {
            return (Package) PackageParserLollipop.parsePackage.callWithException(packageParser, file, Integer.valueOf(i));
        } else if (API_LEVEL >= 17) {
            return (Package) PackageParserJellyBean17.parsePackage.callWithException(packageParser, file, null, new DisplayMetrics(), Integer.valueOf(i));
        } else if (API_LEVEL >= 16) {
            return (Package) PackageParserJellyBean.parsePackage.callWithException(packageParser, file, null, new DisplayMetrics(), Integer.valueOf(i));
        } else {
            return (Package) mirror.android.content.p016pm.PackageParser.parsePackage.callWithException(packageParser, file, null, new DisplayMetrics(), Integer.valueOf(i));
        }
    }

    public static ServiceInfo generateServiceInfo(Service service, int i) {
        if (API_LEVEL >= 23) {
            return (ServiceInfo) PackageParserMarshmallow.generateServiceInfo.call(service, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 22) {
            return (ServiceInfo) PackageParserLollipop22.generateServiceInfo.call(service, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 21) {
            return (ServiceInfo) PackageParserLollipop.generateServiceInfo.call(service, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 17) {
            return (ServiceInfo) PackageParserJellyBean17.generateServiceInfo.call(service, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 16) {
            return (ServiceInfo) PackageParserJellyBean.generateServiceInfo.call(service, Integer.valueOf(i), Boolean.valueOf(false), Integer.valueOf(1), Integer.valueOf(myUserId));
        } else {
            return (ServiceInfo) mirror.android.content.p016pm.PackageParser.generateServiceInfo.call(service, Integer.valueOf(i));
        }
    }

    public static ApplicationInfo generateApplicationInfo(Package packageR, int i) {
        if (API_LEVEL >= 23) {
            return (ApplicationInfo) PackageParserMarshmallow.generateApplicationInfo.call(packageR, Integer.valueOf(i), sUserState);
        } else if (API_LEVEL >= 22) {
            return (ApplicationInfo) PackageParserLollipop22.generateApplicationInfo.call(packageR, Integer.valueOf(i), sUserState);
        } else if (API_LEVEL >= 21) {
            return (ApplicationInfo) PackageParserLollipop.generateApplicationInfo.call(packageR, Integer.valueOf(i), sUserState);
        } else if (API_LEVEL >= 17) {
            return (ApplicationInfo) PackageParserJellyBean17.generateApplicationInfo.call(packageR, Integer.valueOf(i), sUserState);
        } else if (API_LEVEL >= 16) {
            return (ApplicationInfo) PackageParserJellyBean.generateApplicationInfo.call(packageR, Integer.valueOf(i), Boolean.valueOf(false), Integer.valueOf(1));
        } else {
            return (ApplicationInfo) mirror.android.content.p016pm.PackageParser.generateApplicationInfo.call(packageR, Integer.valueOf(i));
        }
    }

    public static ActivityInfo generateActivityInfo(Activity activity, int i) {
        if (API_LEVEL >= 23) {
            return (ActivityInfo) PackageParserMarshmallow.generateActivityInfo.call(activity, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 22) {
            return (ActivityInfo) PackageParserLollipop22.generateActivityInfo.call(activity, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 21) {
            return (ActivityInfo) PackageParserLollipop.generateActivityInfo.call(activity, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 17) {
            return (ActivityInfo) PackageParserJellyBean17.generateActivityInfo.call(activity, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 16) {
            return (ActivityInfo) PackageParserJellyBean.generateActivityInfo.call(activity, Integer.valueOf(i), Boolean.valueOf(false), Integer.valueOf(1), Integer.valueOf(myUserId));
        } else {
            return (ActivityInfo) mirror.android.content.p016pm.PackageParser.generateActivityInfo.call(activity, Integer.valueOf(i));
        }
    }

    public static ProviderInfo generateProviderInfo(Provider provider, int i) {
        if (API_LEVEL >= 23) {
            return (ProviderInfo) PackageParserMarshmallow.generateProviderInfo.call(provider, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 22) {
            return (ProviderInfo) PackageParserLollipop22.generateProviderInfo.call(provider, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 21) {
            return (ProviderInfo) PackageParserLollipop.generateProviderInfo.call(provider, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 17) {
            return (ProviderInfo) PackageParserJellyBean17.generateProviderInfo.call(provider, Integer.valueOf(i), sUserState, Integer.valueOf(myUserId));
        } else if (API_LEVEL >= 16) {
            return (ProviderInfo) PackageParserJellyBean.generateProviderInfo.call(provider, Integer.valueOf(i), Boolean.valueOf(false), Integer.valueOf(1), Integer.valueOf(myUserId));
        } else {
            return (ProviderInfo) mirror.android.content.p016pm.PackageParser.generateProviderInfo.call(provider, Integer.valueOf(i));
        }
    }

    public static PackageInfo generatePackageInfo(Package packageR, int i, long j, long j2) {
        if (API_LEVEL >= 23) {
            return (PackageInfo) PackageParserMarshmallow.generatePackageInfo.call(packageR, GIDS, Integer.valueOf(i), Long.valueOf(j), Long.valueOf(j2), null, sUserState);
        } else if (API_LEVEL >= 21) {
            if (PackageParserLollipop22.generatePackageInfo != null) {
                return (PackageInfo) PackageParserLollipop22.generatePackageInfo.call(packageR, GIDS, Integer.valueOf(i), Long.valueOf(j), Long.valueOf(j2), null, sUserState);
            }
            return (PackageInfo) PackageParserLollipop.generatePackageInfo.call(packageR, GIDS, Integer.valueOf(i), Long.valueOf(j), Long.valueOf(j2), null, sUserState);
        } else if (API_LEVEL >= 17) {
            return (PackageInfo) PackageParserJellyBean17.generatePackageInfo.call(packageR, GIDS, Integer.valueOf(i), Long.valueOf(j), Long.valueOf(j2), null, sUserState);
        } else if (API_LEVEL >= 16) {
            return (PackageInfo) PackageParserJellyBean.generatePackageInfo.call(packageR, GIDS, Integer.valueOf(i), Long.valueOf(j), Long.valueOf(j2), null);
        } else {
            return (PackageInfo) mirror.android.content.p016pm.PackageParser.generatePackageInfo.call(packageR, GIDS, Integer.valueOf(i), Long.valueOf(j), Long.valueOf(j2));
        }
    }

    public static void collectCertificates(PackageParser packageParser, Package packageR, int i) throws Throwable {
        if (API_LEVEL >= 28) {
            PackageParserP28.collectCertificates.callWithException(packageR, Boolean.valueOf(true));
        } else if (API_LEVEL >= 24) {
            PackageParserNougat.collectCertificates.callWithException(packageR, Integer.valueOf(i));
        } else if (API_LEVEL >= 23) {
            PackageParserMarshmallow.collectCertificates.callWithException(packageParser, packageR, Integer.valueOf(i));
        } else if (API_LEVEL >= 22) {
            PackageParserLollipop22.collectCertificates.callWithException(packageParser, packageR, Integer.valueOf(i));
        } else if (API_LEVEL >= 21) {
            PackageParserLollipop.collectCertificates.callWithException(packageParser, packageR, Integer.valueOf(i));
        } else if (API_LEVEL >= 17) {
            PackageParserJellyBean17.collectCertificates.callWithException(packageParser, packageR, Integer.valueOf(i));
        } else if (API_LEVEL >= 16) {
            PackageParserJellyBean.collectCertificates.callWithException(packageParser, packageR, Integer.valueOf(i));
        } else {
            mirror.android.content.p016pm.PackageParser.collectCertificates.call(packageParser, packageR, Integer.valueOf(i));
        }
    }
}
