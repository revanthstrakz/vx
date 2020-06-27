package com.lody.virtual.server.p009pm.parser;

import android.content.p000pm.PackageParser;
import android.content.p000pm.PackageParser.Activity;
import android.content.p000pm.PackageParser.Instrumentation;
import android.content.p000pm.PackageParser.Package;
import android.content.p000pm.PackageParser.Provider;
import android.content.p000pm.PackageParser.Service;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.text.TextUtils;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.client.fixer.ComponentFixer;
import com.lody.virtual.helper.collection.ArrayMap;
import com.lody.virtual.helper.compat.PackageParserCompat;
import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.server.p009pm.PackageSetting;
import com.lody.virtual.server.p009pm.PackageUserState;
import com.lody.virtual.server.p009pm.parser.VPackage.ActivityComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.ActivityIntentInfo;
import com.lody.virtual.server.p009pm.parser.VPackage.InstrumentationComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.PermissionComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.PermissionGroupComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.ProviderComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.ProviderIntentInfo;
import com.lody.virtual.server.p009pm.parser.VPackage.ServiceComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.ServiceIntentInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mirror.android.content.p016pm.ApplicationInfoL;
import mirror.android.content.p016pm.ApplicationInfoN;
import mirror.android.content.p016pm.PackageParser.SigningDetails;

/* renamed from: com.lody.virtual.server.pm.parser.PackageParserEx */
public class PackageParserEx {
    private static final String FAKE_SIG = "308203553082023da0030201020204378edaaa300d06092a864886f70d01010b0500305a310d300b0603550406130466616b65310d300b0603550408130466616b65310d300b0603550407130466616b65310d300b060355040a130466616b65310d300b060355040b130466616b65310d300b0603550403130466616b653020170d3138303533303034343434385a180f32313237313230353034343434385a305a310d300b0603550406130466616b65310d300b0603550408130466616b65310d300b0603550407130466616b65310d300b060355040a130466616b65310d300b060355040b130466616b65310d300b0603550403130466616b6530820122300d06092a864886f70d01010105000382010f003082010a0282010100b766ff6afd8a53edd4cee4985bc90e0c515157b5e9f731818961f7250d0d1ac7c7fb80eb5aeb8c28478732e8ff38cff574bfa0eba8039f73af1532f939c4ef9684719efbaba2dd3c583a20907c1c55248a63098c6da23dcfc877763d5fe6061dddd399cf2f49e3250e23f9e687a4d182bcd0662179ba4c9983448e34b4c83e5abbf4f87e87add9157c75fd40de3416744507a3517915f35b6fcad78766e8e1879df8ab823a6ffa335e4790f6e29c87393732025b63ce3a38e42cb0d48cdceb902f191d7d45823db9a0678895e8bfc59b2af7526ca4c2dc3dbe7e70c7c840e666b9629d36e5ddf1d9a80c37f1ab1bc1fb30432914008fbde95d5d3db7853565510203010001a321301f301d0603551d0e04160414d8513e1ae21c64e9ebeee3507e24ea375eef958e300d06092a864886f70d01010b0500038201010088bf20b36428558359536dddcfff16fe233656a92364cb544d8acc43b0859f880a8da339dd430616085edf035e4e6e6dd2281ceb14adde2f05e9ac58d547a09083eece0c6d405289cb7918f85754ee545eefe35e30c103cad617905e94eb4fb68e6920a60d30577855f9feb6e3a664856f74aa9f824aa7d4a3adf85e162c67b9a4261e3185f038ead96112ae3e574d280425e90567352fb82bc9173302122025eaecfabd94d0f9be69a85c415f7cf7759c9651734300952027b316c37aaa1b2418865a3fc7b6bd1072c92ccaacdaa1cf9586d9b8310ceee066ce68859107dfc45ccce729ad9e75b53b584fa37dcd64da8673b1279c6c5861ed3792deac156c8a";
    private static final String TAG = "PackageParserEx";
    private static final ArrayMap<String, String[]> sSharedLibCache = new ArrayMap<>();

    public static VPackage parsePackage(File file) throws Throwable {
        PackageParser createParser = PackageParserCompat.createParser(file);
        Package parsePackage = PackageParserCompat.parsePackage(createParser, file, 0);
        if (!parsePackage.requestedPermissions.contains("android.permission.FAKE_PACKAGE_SIGNATURE") || parsePackage.mAppMetaData == null || !parsePackage.mAppMetaData.containsKey(Constants.FEATURE_FAKE_SIGNATURE)) {
            try {
                PackageParserCompat.collectCertificates(createParser, parsePackage, 1);
            } catch (Throwable th) {
                VLog.m87e(TAG, "collectCertificates failed", th);
                if (VirtualCore.get().getContext().getFileStreamPath(Constants.FAKE_SIGNATURE_FLAG).exists()) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Using fake signature: ");
                    sb.append(parsePackage.packageName);
                    VLog.m91w(str, sb.toString(), new Object[0]);
                    buildSignature(parsePackage, new Signature[]{new Signature(FAKE_SIG)});
                } else {
                    throw th;
                }
            }
        } else {
            buildSignature(parsePackage, new Signature[]{new Signature(parsePackage.mAppMetaData.getString(Constants.FEATURE_FAKE_SIGNATURE))});
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Using fake-signature feature on : ");
            sb2.append(parsePackage.packageName);
            VLog.m86d(str2, sb2.toString(), new Object[0]);
        }
        return buildPackageCache(parsePackage);
    }

    private static void buildSignature(Package packageR, Signature[] signatureArr) {
        if (VERSION.SDK_INT < 28) {
            packageR.mSignatures = signatureArr;
            return;
        }
        Object obj = mirror.android.content.p016pm.PackageParser.Package.mSigningDetails.get(packageR);
        SigningDetails.pastSigningCertificates.set(obj, signatureArr);
        SigningDetails.signatures.set(obj, signatureArr);
    }

    /* JADX INFO: finally extract failed */
    public static VPackage readPackageCache(String str) {
        Parcel obtain = Parcel.obtain();
        try {
            FileInputStream fileInputStream = new FileInputStream(VEnvironment.getPackageCacheFile(str));
            byte[] byteArray = FileUtils.toByteArray(fileInputStream);
            fileInputStream.close();
            obtain.unmarshall(byteArray, 0, byteArray.length);
            obtain.setDataPosition(0);
            if (obtain.readInt() == 4) {
                VPackage vPackage = new VPackage(obtain);
                addOwner(vPackage);
                obtain.recycle();
                return vPackage;
            }
            throw new IllegalStateException("Invalid version.");
        } catch (Exception e) {
            e.printStackTrace();
            obtain.recycle();
            return null;
        } catch (Throwable th) {
            obtain.recycle();
            throw th;
        }
    }

    public static void readSignature(VPackage vPackage) {
        File signatureFile = VEnvironment.getSignatureFile(vPackage.packageName);
        if (signatureFile.exists()) {
            Parcel obtain = Parcel.obtain();
            try {
                FileInputStream fileInputStream = new FileInputStream(signatureFile);
                byte[] byteArray = FileUtils.toByteArray(fileInputStream);
                fileInputStream.close();
                obtain.unmarshall(byteArray, 0, byteArray.length);
                obtain.setDataPosition(0);
                vPackage.mSignatures = (Signature[]) obtain.createTypedArray(Signature.CREATOR);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Throwable th) {
                obtain.recycle();
                throw th;
            }
            obtain.recycle();
        }
    }

    public static void savePackageCache(VPackage vPackage) {
        String str = vPackage.packageName;
        Parcel obtain = Parcel.obtain();
        try {
            obtain.writeInt(4);
            vPackage.writeToParcel(obtain, 0);
            FileOutputStream fileOutputStream = new FileOutputStream(VEnvironment.getPackageCacheFile(str));
            fileOutputStream.write(obtain.marshall());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            obtain.recycle();
            throw th;
        }
        obtain.recycle();
        Signature[] signatureArr = vPackage.mSignatures;
        if (signatureArr != null) {
            File signatureFile = VEnvironment.getSignatureFile(str);
            if (signatureFile.exists() && !signatureFile.delete()) {
                String str2 = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Unable to delete the signatures of ");
                sb.append(str);
                VLog.m91w(str2, sb.toString(), new Object[0]);
            }
            Parcel obtain2 = Parcel.obtain();
            try {
                obtain2.writeTypedArray(signatureArr, 0);
                FileUtils.writeParcelToFile(obtain2, signatureFile);
            } catch (IOException e2) {
                e2.printStackTrace();
            } catch (Throwable th2) {
                obtain2.recycle();
                throw th2;
            }
            obtain2.recycle();
        }
    }

    private static VPackage buildPackageCache(Package packageR) {
        VPackage vPackage = new VPackage();
        vPackage.activities = new ArrayList<>(packageR.activities.size());
        vPackage.services = new ArrayList<>(packageR.services.size());
        vPackage.receivers = new ArrayList<>(packageR.receivers.size());
        vPackage.providers = new ArrayList<>(packageR.providers.size());
        vPackage.instrumentation = new ArrayList<>(packageR.instrumentation.size());
        vPackage.permissions = new ArrayList<>(packageR.permissions.size());
        vPackage.permissionGroups = new ArrayList<>(packageR.permissionGroups.size());
        Iterator it = packageR.activities.iterator();
        while (it.hasNext()) {
            vPackage.activities.add(new ActivityComponent((Activity) it.next()));
        }
        Iterator it2 = packageR.services.iterator();
        while (it2.hasNext()) {
            vPackage.services.add(new ServiceComponent((Service) it2.next()));
        }
        Iterator it3 = packageR.receivers.iterator();
        while (it3.hasNext()) {
            vPackage.receivers.add(new ActivityComponent((Activity) it3.next()));
        }
        Iterator it4 = packageR.providers.iterator();
        while (it4.hasNext()) {
            vPackage.providers.add(new ProviderComponent((Provider) it4.next()));
        }
        Iterator it5 = packageR.instrumentation.iterator();
        while (it5.hasNext()) {
            vPackage.instrumentation.add(new InstrumentationComponent((Instrumentation) it5.next()));
        }
        vPackage.requestedPermissions = new ArrayList<>(packageR.requestedPermissions.size());
        vPackage.requestedPermissions.addAll(packageR.requestedPermissions);
        if (mirror.android.content.p016pm.PackageParser.Package.protectedBroadcasts != null) {
            List list = (List) mirror.android.content.p016pm.PackageParser.Package.protectedBroadcasts.get(packageR);
            if (list != null) {
                vPackage.protectedBroadcasts = new ArrayList<>(list);
                vPackage.protectedBroadcasts.addAll(list);
            }
        }
        vPackage.applicationInfo = packageR.applicationInfo;
        if (VERSION.SDK_INT < 28) {
            vPackage.mSignatures = packageR.mSignatures;
        } else {
            Object obj = mirror.android.content.p016pm.PackageParser.Package.mSigningDetails.get(packageR);
            if (((Boolean) SigningDetails.hasPastSigningCertificates.call(obj, new Object[0])).booleanValue()) {
                vPackage.mSignatures = new Signature[1];
                vPackage.mSignatures[0] = ((Signature[]) SigningDetails.pastSigningCertificates.get(obj))[0];
            } else if (((Boolean) SigningDetails.hasSignatures.call(obj, new Object[0])).booleanValue()) {
                Signature[] signatureArr = (Signature[]) SigningDetails.signatures.get(obj);
                int length = signatureArr.length;
                vPackage.mSignatures = new Signature[length];
                System.arraycopy(signatureArr, 0, vPackage.mSignatures, 0, length);
            }
        }
        vPackage.mAppMetaData = packageR.mAppMetaData;
        vPackage.packageName = packageR.packageName;
        vPackage.mPreferredOrder = packageR.mPreferredOrder;
        vPackage.mVersionName = packageR.mVersionName;
        vPackage.mSharedUserId = packageR.mSharedUserId;
        vPackage.mSharedUserLabel = packageR.mSharedUserLabel;
        vPackage.usesLibraries = packageR.usesLibraries;
        vPackage.mVersionCode = packageR.mVersionCode;
        vPackage.mAppMetaData = packageR.mAppMetaData;
        vPackage.configPreferences = packageR.configPreferences;
        vPackage.reqFeatures = packageR.reqFeatures;
        addOwner(vPackage);
        return vPackage;
    }

    public static void initApplicationInfoBase(PackageSetting packageSetting, VPackage vPackage) {
        ApplicationInfo applicationInfo = vPackage.applicationInfo;
        applicationInfo.flags |= 4;
        if (TextUtils.isEmpty(applicationInfo.processName)) {
            applicationInfo.processName = applicationInfo.packageName;
        }
        applicationInfo.enabled = true;
        applicationInfo.nativeLibraryDir = packageSetting.libPath;
        applicationInfo.uid = packageSetting.appId;
        applicationInfo.name = ComponentFixer.fixComponentClassName(packageSetting.packageName, applicationInfo.name);
        applicationInfo.publicSourceDir = packageSetting.apkPath;
        applicationInfo.sourceDir = packageSetting.apkPath;
        if (VERSION.SDK_INT >= 21) {
            applicationInfo.splitSourceDirs = new String[]{packageSetting.apkPath};
            applicationInfo.splitPublicSourceDirs = applicationInfo.splitSourceDirs;
            ApplicationInfoL.scanSourceDir.set(applicationInfo, applicationInfo.dataDir);
            ApplicationInfoL.scanPublicSourceDir.set(applicationInfo, applicationInfo.dataDir);
            ApplicationInfoL.primaryCpuAbi.set(applicationInfo, (String) ApplicationInfoL.primaryCpuAbi.get(VirtualCore.get().getContext().getApplicationInfo()));
        }
        if (packageSetting.dependSystem) {
            String[] strArr = (String[]) sSharedLibCache.get(packageSetting.packageName);
            if (strArr == null) {
                try {
                    String[] strArr2 = VirtualCore.get().getUnHookPackageManager().getApplicationInfo(packageSetting.packageName, 1024).sharedLibraryFiles;
                    if (strArr2 == null) {
                        try {
                            strArr = new String[0];
                        } catch (NameNotFoundException e) {
                            e = e;
                            strArr = strArr2;
                        }
                    } else {
                        strArr = strArr2;
                    }
                    sSharedLibCache.put(packageSetting.packageName, strArr);
                } catch (NameNotFoundException e2) {
                    e = e2;
                }
            }
            applicationInfo.sharedLibraryFiles = strArr;
        }
        return;
        e.printStackTrace();
        applicationInfo.sharedLibraryFiles = strArr;
    }

    private static void initApplicationAsUser(ApplicationInfo applicationInfo, int i) {
        applicationInfo.dataDir = VEnvironment.getDataUserPackageDirectory(i, applicationInfo.packageName).getPath();
        if (VERSION.SDK_INT >= 21) {
            ApplicationInfoL.scanSourceDir.set(applicationInfo, applicationInfo.dataDir);
            ApplicationInfoL.scanPublicSourceDir.set(applicationInfo, applicationInfo.dataDir);
        }
        if (VERSION.SDK_INT >= 24) {
            if (VERSION.SDK_INT < 26) {
                ApplicationInfoN.deviceEncryptedDataDir.set(applicationInfo, applicationInfo.dataDir);
                ApplicationInfoN.credentialEncryptedDataDir.set(applicationInfo, applicationInfo.dataDir);
            }
            ApplicationInfoN.deviceProtectedDataDir.set(applicationInfo, applicationInfo.dataDir);
            ApplicationInfoN.credentialProtectedDataDir.set(applicationInfo, applicationInfo.dataDir);
        }
    }

    private static void addOwner(VPackage vPackage) {
        Iterator it = vPackage.activities.iterator();
        while (it.hasNext()) {
            ActivityComponent activityComponent = (ActivityComponent) it.next();
            activityComponent.owner = vPackage;
            Iterator it2 = activityComponent.intents.iterator();
            while (it2.hasNext()) {
                ((ActivityIntentInfo) it2.next()).activity = activityComponent;
            }
        }
        Iterator it3 = vPackage.services.iterator();
        while (it3.hasNext()) {
            ServiceComponent serviceComponent = (ServiceComponent) it3.next();
            serviceComponent.owner = vPackage;
            Iterator it4 = serviceComponent.intents.iterator();
            while (it4.hasNext()) {
                ((ServiceIntentInfo) it4.next()).service = serviceComponent;
            }
        }
        Iterator it5 = vPackage.receivers.iterator();
        while (it5.hasNext()) {
            ActivityComponent activityComponent2 = (ActivityComponent) it5.next();
            activityComponent2.owner = vPackage;
            Iterator it6 = activityComponent2.intents.iterator();
            while (it6.hasNext()) {
                ((ActivityIntentInfo) it6.next()).activity = activityComponent2;
            }
        }
        Iterator it7 = vPackage.providers.iterator();
        while (it7.hasNext()) {
            ProviderComponent providerComponent = (ProviderComponent) it7.next();
            providerComponent.owner = vPackage;
            Iterator it8 = providerComponent.intents.iterator();
            while (it8.hasNext()) {
                ((ProviderIntentInfo) it8.next()).provider = providerComponent;
            }
        }
        Iterator it9 = vPackage.instrumentation.iterator();
        while (it9.hasNext()) {
            ((InstrumentationComponent) it9.next()).owner = vPackage;
        }
        Iterator it10 = vPackage.permissions.iterator();
        while (it10.hasNext()) {
            ((PermissionComponent) it10.next()).owner = vPackage;
        }
        Iterator it11 = vPackage.permissionGroups.iterator();
        while (it11.hasNext()) {
            ((PermissionGroupComponent) it11.next()).owner = vPackage;
        }
    }

    public static PackageInfo generatePackageInfo(VPackage vPackage, int i, long j, long j2, PackageUserState packageUserState, int i2) {
        if (!checkUseInstalledOrHidden(packageUserState, i)) {
            return null;
        }
        if (vPackage.mSignatures == null) {
            readSignature(vPackage);
        }
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = vPackage.packageName;
        packageInfo.versionCode = vPackage.mVersionCode;
        packageInfo.sharedUserLabel = vPackage.mSharedUserLabel;
        packageInfo.versionName = vPackage.mVersionName;
        packageInfo.sharedUserId = vPackage.mSharedUserId;
        packageInfo.sharedUserLabel = vPackage.mSharedUserLabel;
        packageInfo.applicationInfo = generateApplicationInfo(vPackage, i, packageUserState, i2);
        packageInfo.firstInstallTime = j;
        packageInfo.lastUpdateTime = j2;
        if (vPackage.requestedPermissions != null && !vPackage.requestedPermissions.isEmpty()) {
            String[] strArr = new String[vPackage.requestedPermissions.size()];
            vPackage.requestedPermissions.toArray(strArr);
            packageInfo.requestedPermissions = strArr;
        }
        if ((i & 256) != 0) {
            packageInfo.gids = PackageParserCompat.GIDS;
        }
        if ((i & 16384) != 0) {
            int size = vPackage.configPreferences != null ? vPackage.configPreferences.size() : 0;
            if (size > 0) {
                packageInfo.configPreferences = new ConfigurationInfo[size];
                vPackage.configPreferences.toArray(packageInfo.configPreferences);
            }
            int size2 = vPackage.reqFeatures != null ? vPackage.reqFeatures.size() : 0;
            if (size2 > 0) {
                packageInfo.reqFeatures = new FeatureInfo[size2];
                vPackage.reqFeatures.toArray(packageInfo.reqFeatures);
            }
        }
        if ((i & 1) != 0) {
            int size3 = vPackage.activities.size();
            if (size3 > 0) {
                ActivityInfo[] activityInfoArr = new ActivityInfo[size3];
                int i3 = 0;
                int i4 = 0;
                while (i3 < size3) {
                    int i5 = i4 + 1;
                    activityInfoArr[i4] = generateActivityInfo((ActivityComponent) vPackage.activities.get(i3), i, packageUserState, i2);
                    i3++;
                    i4 = i5;
                }
                packageInfo.activities = activityInfoArr;
            }
        }
        if ((i & 2) != 0) {
            int size4 = vPackage.receivers.size();
            if (size4 > 0) {
                ActivityInfo[] activityInfoArr2 = new ActivityInfo[size4];
                int i6 = 0;
                int i7 = 0;
                while (i6 < size4) {
                    int i8 = i7 + 1;
                    activityInfoArr2[i7] = generateActivityInfo((ActivityComponent) vPackage.receivers.get(i6), i, packageUserState, i2);
                    i6++;
                    i7 = i8;
                }
                packageInfo.receivers = activityInfoArr2;
            }
        }
        if ((i & 4) != 0) {
            int size5 = vPackage.services.size();
            if (size5 > 0) {
                ServiceInfo[] serviceInfoArr = new ServiceInfo[size5];
                int i9 = 0;
                int i10 = 0;
                while (i9 < size5) {
                    int i11 = i10 + 1;
                    serviceInfoArr[i10] = generateServiceInfo((ServiceComponent) vPackage.services.get(i9), i, packageUserState, i2);
                    i9++;
                    i10 = i11;
                }
                packageInfo.services = serviceInfoArr;
            }
        }
        if ((i & 8) != 0) {
            int size6 = vPackage.providers.size();
            if (size6 > 0) {
                ProviderInfo[] providerInfoArr = new ProviderInfo[size6];
                int i12 = 0;
                int i13 = 0;
                while (i12 < size6) {
                    int i14 = i13 + 1;
                    providerInfoArr[i13] = generateProviderInfo((ProviderComponent) vPackage.providers.get(i12), i, packageUserState, i2);
                    i12++;
                    i13 = i14;
                }
                packageInfo.providers = providerInfoArr;
            }
        }
        if ((i & 16) != 0) {
            int size7 = vPackage.instrumentation.size();
            if (size7 > 0) {
                packageInfo.instrumentation = new InstrumentationInfo[size7];
                for (int i15 = 0; i15 < size7; i15++) {
                    packageInfo.instrumentation[i15] = generateInstrumentationInfo((InstrumentationComponent) vPackage.instrumentation.get(i15), i);
                }
            }
        }
        if ((i & 64) != 0) {
            int length = vPackage.mSignatures != null ? vPackage.mSignatures.length : 0;
            if (length > 0) {
                packageInfo.signatures = new Signature[length];
                System.arraycopy(vPackage.mSignatures, 0, packageInfo.signatures, 0, length);
            }
        }
        return packageInfo;
    }

    public static ApplicationInfo generateApplicationInfo(VPackage vPackage, int i, PackageUserState packageUserState, int i2) {
        if (vPackage == null || !checkUseInstalledOrHidden(packageUserState, i)) {
            return null;
        }
        ApplicationInfo applicationInfo = new ApplicationInfo(vPackage.applicationInfo);
        if ((i & 128) != 0) {
            applicationInfo.metaData = vPackage.mAppMetaData;
        }
        initApplicationAsUser(applicationInfo, i2);
        return applicationInfo;
    }

    public static ActivityInfo generateActivityInfo(ActivityComponent activityComponent, int i, PackageUserState packageUserState, int i2) {
        if (activityComponent == null || !checkUseInstalledOrHidden(packageUserState, i)) {
            return null;
        }
        ActivityInfo activityInfo = new ActivityInfo(activityComponent.info);
        if (!((i & 128) == 0 || activityComponent.metaData == null)) {
            activityInfo.metaData = activityComponent.metaData;
        }
        activityInfo.applicationInfo = generateApplicationInfo(activityComponent.owner, i, packageUserState, i2);
        return activityInfo;
    }

    public static ServiceInfo generateServiceInfo(ServiceComponent serviceComponent, int i, PackageUserState packageUserState, int i2) {
        if (serviceComponent == null || !checkUseInstalledOrHidden(packageUserState, i)) {
            return null;
        }
        ServiceInfo serviceInfo = new ServiceInfo(serviceComponent.info);
        if (!((i & 128) == 0 || serviceComponent.metaData == null)) {
            serviceInfo.metaData = serviceComponent.metaData;
        }
        serviceInfo.applicationInfo = generateApplicationInfo(serviceComponent.owner, i, packageUserState, i2);
        return serviceInfo;
    }

    public static ProviderInfo generateProviderInfo(ProviderComponent providerComponent, int i, PackageUserState packageUserState, int i2) {
        if (providerComponent == null || !checkUseInstalledOrHidden(packageUserState, i)) {
            return null;
        }
        ProviderInfo providerInfo = new ProviderInfo(providerComponent.info);
        if (!((i & 128) == 0 || providerComponent.metaData == null)) {
            providerInfo.metaData = providerComponent.metaData;
        }
        if ((i & 2048) == 0) {
            providerInfo.uriPermissionPatterns = null;
        }
        providerInfo.applicationInfo = generateApplicationInfo(providerComponent.owner, i, packageUserState, i2);
        return providerInfo;
    }

    public static InstrumentationInfo generateInstrumentationInfo(InstrumentationComponent instrumentationComponent, int i) {
        if (instrumentationComponent == null) {
            return null;
        }
        if ((i & 128) == 0) {
            return instrumentationComponent.info;
        }
        InstrumentationInfo instrumentationInfo = new InstrumentationInfo(instrumentationComponent.info);
        instrumentationInfo.metaData = instrumentationComponent.metaData;
        return instrumentationInfo;
    }

    public static PermissionInfo generatePermissionInfo(PermissionComponent permissionComponent, int i) {
        if (permissionComponent == null) {
            return null;
        }
        if ((i & 128) == 0) {
            return permissionComponent.info;
        }
        PermissionInfo permissionInfo = new PermissionInfo(permissionComponent.info);
        permissionInfo.metaData = permissionComponent.metaData;
        return permissionInfo;
    }

    public static PermissionGroupInfo generatePermissionGroupInfo(PermissionGroupComponent permissionGroupComponent, int i) {
        if (permissionGroupComponent == null) {
            return null;
        }
        if ((i & 128) == 0) {
            return permissionGroupComponent.info;
        }
        PermissionGroupInfo permissionGroupInfo = new PermissionGroupInfo(permissionGroupComponent.info);
        permissionGroupInfo.metaData = permissionGroupComponent.metaData;
        return permissionGroupInfo;
    }

    private static boolean checkUseInstalledOrHidden(PackageUserState packageUserState, int i) {
        return (packageUserState.installed && !packageUserState.hidden) || (i & 8192) != 0;
    }
}
