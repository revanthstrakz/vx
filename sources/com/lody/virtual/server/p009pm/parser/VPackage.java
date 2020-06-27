package com.lody.virtual.server.p009pm.parser;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.p000pm.PackageParser.Activity;
import android.content.p000pm.PackageParser.Instrumentation;
import android.content.p000pm.PackageParser.Permission;
import android.content.p000pm.PackageParser.PermissionGroup;
import android.content.p000pm.PackageParser.Provider;
import android.content.p000pm.PackageParser.Service;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* renamed from: com.lody.virtual.server.pm.parser.VPackage */
public class VPackage implements Parcelable {
    public static final Creator<VPackage> CREATOR = new Creator<VPackage>() {
        public VPackage createFromParcel(Parcel parcel) {
            return new VPackage(parcel);
        }

        public VPackage[] newArray(int i) {
            return new VPackage[i];
        }
    };
    public ArrayList<ActivityComponent> activities;
    public ApplicationInfo applicationInfo;
    public ArrayList<ConfigurationInfo> configPreferences = null;
    public ArrayList<InstrumentationComponent> instrumentation;
    public Bundle mAppMetaData;
    public Object mExtras;
    public int mPreferredOrder;
    public String mSharedUserId;
    public int mSharedUserLabel;
    public Signature[] mSignatures;
    public int mVersionCode;
    public String mVersionName;
    public String packageName;
    public ArrayList<PermissionGroupComponent> permissionGroups;
    public ArrayList<PermissionComponent> permissions;
    public ArrayList<String> protectedBroadcasts;
    public ArrayList<ProviderComponent> providers;
    public ArrayList<ActivityComponent> receivers;
    public ArrayList<FeatureInfo> reqFeatures = null;
    public ArrayList<String> requestedPermissions;
    public ArrayList<ServiceComponent> services;
    public ArrayList<String> usesLibraries;

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$ActivityComponent */
    public static class ActivityComponent extends Component<ActivityIntentInfo> {
        public ActivityInfo info;

        public ActivityComponent(Activity activity) {
            super(activity);
            if (activity.intents != null) {
                this.intents = new ArrayList(activity.intents.size());
                Iterator it = activity.intents.iterator();
                while (it.hasNext()) {
                    this.intents.add(new ActivityIntentInfo((android.content.p000pm.PackageParser.IntentInfo) it.next()));
                }
            }
            this.info = activity.info;
        }

        protected ActivityComponent(Parcel parcel) {
            this.info = (ActivityInfo) parcel.readParcelable(ActivityInfo.class.getClassLoader());
            this.className = parcel.readString();
            this.metaData = parcel.readBundle(Bundle.class.getClassLoader());
            int readInt = parcel.readInt();
            this.intents = new ArrayList(readInt);
            while (true) {
                int i = readInt - 1;
                if (readInt > 0) {
                    this.intents.add(new ActivityIntentInfo(parcel));
                    readInt = i;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$ActivityIntentInfo */
    public static class ActivityIntentInfo extends IntentInfo {
        public ActivityComponent activity;

        public ActivityIntentInfo(android.content.p000pm.PackageParser.IntentInfo intentInfo) {
            super(intentInfo);
        }

        protected ActivityIntentInfo(Parcel parcel) {
            super(parcel);
        }
    }

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$Component */
    public static class Component<II extends IntentInfo> {
        public String className;
        private ComponentName componentName;
        public ArrayList<II> intents;
        public Bundle metaData;
        public VPackage owner;

        protected Component() {
        }

        public Component(android.content.p000pm.PackageParser.Component component) {
            this.className = component.className;
            this.metaData = component.metaData;
        }

        public ComponentName getComponentName() {
            if (this.componentName != null) {
                return this.componentName;
            }
            if (this.className != null) {
                this.componentName = new ComponentName(this.owner.packageName, this.className);
            }
            return this.componentName;
        }
    }

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$InstrumentationComponent */
    public static class InstrumentationComponent extends Component<IntentInfo> {
        public InstrumentationInfo info;

        public InstrumentationComponent(Instrumentation instrumentation) {
            super(instrumentation);
            this.info = instrumentation.info;
        }

        protected InstrumentationComponent(Parcel parcel) {
            this.info = (InstrumentationInfo) parcel.readParcelable(ActivityInfo.class.getClassLoader());
            this.className = parcel.readString();
            this.metaData = parcel.readBundle(Bundle.class.getClassLoader());
            int readInt = parcel.readInt();
            this.intents = new ArrayList(readInt);
            while (true) {
                int i = readInt - 1;
                if (readInt > 0) {
                    this.intents.add(new IntentInfo(parcel));
                    readInt = i;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$IntentInfo */
    public static class IntentInfo implements Parcelable {
        public static final Creator<IntentInfo> CREATOR = new Creator<IntentInfo>() {
            public IntentInfo createFromParcel(Parcel parcel) {
                return new IntentInfo(parcel);
            }

            public IntentInfo[] newArray(int i) {
                return new IntentInfo[i];
            }
        };
        public int banner;
        public IntentFilter filter;
        public boolean hasDefault;
        public int icon;
        public int labelRes;
        public int logo;
        public String nonLocalizedLabel;

        public int describeContents() {
            return 0;
        }

        public IntentInfo(android.content.p000pm.PackageParser.IntentInfo intentInfo) {
            this.filter = intentInfo;
            this.hasDefault = intentInfo.hasDefault;
            this.labelRes = intentInfo.labelRes;
            if (intentInfo.nonLocalizedLabel != null) {
                this.nonLocalizedLabel = intentInfo.nonLocalizedLabel.toString();
            }
            this.icon = intentInfo.icon;
            this.logo = intentInfo.logo;
            if (VERSION.SDK_INT > 19) {
                this.banner = intentInfo.banner;
            }
        }

        protected IntentInfo(Parcel parcel) {
            this.filter = (IntentFilter) parcel.readParcelable(VPackage.class.getClassLoader());
            this.hasDefault = parcel.readByte() != 0;
            this.labelRes = parcel.readInt();
            this.nonLocalizedLabel = parcel.readString();
            this.icon = parcel.readInt();
            this.logo = parcel.readInt();
            this.banner = parcel.readInt();
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.filter, i);
            parcel.writeByte(this.hasDefault ? (byte) 1 : 0);
            parcel.writeInt(this.labelRes);
            parcel.writeString(this.nonLocalizedLabel);
            parcel.writeInt(this.icon);
            parcel.writeInt(this.logo);
            parcel.writeInt(this.banner);
        }
    }

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$PermissionComponent */
    public static class PermissionComponent extends Component<IntentInfo> {
        public static Set<String> DANGEROUS_PERMISSION = new HashSet<String>() {
            {
                add("android.permission.READ_CALENDAR");
                add("android.permission.WRITE_CALENDAR");
                add("android.permission.CAMERA");
                add("android.permission.READ_CONTACTS");
                add("android.permission.WRITE_CONTACTS");
                add("android.permission.GET_ACCOUNTS");
                add("android.permission.ACCESS_FINE_LOCATION");
                add("android.permission.ACCESS_COARSE_LOCATION");
                add("android.permission.READ_PHONE_STATE");
                add("android.permission.CALL_PHONE");
                add("android.permission.READ_CALL_LOG");
                add("android.permission.WRITE_CALL_LOG");
                add("com.android.voicemail.permission.ADD_VOICEMAIL");
                add("android.permission.USE_SIP");
                add("android.permission.PROCESS_OUTGOING_CALLS");
                add("android.permission.BODY_SENSORS");
                add("android.permission.SEND_SMS");
                add("android.permission.RECEIVE_SMS");
                add("android.permission.READ_SMS");
                add("android.permission.RECEIVE_WAP_PUSH");
                add("android.permission.RECEIVE_MMS");
                add("android.permission.READ_EXTERNAL_STORAGE");
                add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
        };
        public PermissionInfo info;

        public PermissionComponent(Permission permission) {
            super(permission);
            this.info = permission.info;
        }

        protected PermissionComponent(Parcel parcel) {
            this.info = (PermissionInfo) parcel.readParcelable(ActivityInfo.class.getClassLoader());
            this.className = parcel.readString();
            this.metaData = parcel.readBundle(Bundle.class.getClassLoader());
            int readInt = parcel.readInt();
            this.intents = new ArrayList(readInt);
            while (true) {
                int i = readInt - 1;
                if (readInt > 0) {
                    this.intents.add(new IntentInfo(parcel));
                    readInt = i;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$PermissionGroupComponent */
    public static class PermissionGroupComponent extends Component<IntentInfo> {
        public PermissionGroupInfo info;

        public PermissionGroupComponent(PermissionGroup permissionGroup) {
            super(permissionGroup);
            this.info = permissionGroup.info;
        }

        protected PermissionGroupComponent(Parcel parcel) {
            this.info = (PermissionGroupInfo) parcel.readParcelable(ActivityInfo.class.getClassLoader());
            this.className = parcel.readString();
            this.metaData = parcel.readBundle(Bundle.class.getClassLoader());
            int readInt = parcel.readInt();
            this.intents = new ArrayList(readInt);
            while (true) {
                int i = readInt - 1;
                if (readInt > 0) {
                    this.intents.add(new IntentInfo(parcel));
                    readInt = i;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$ProviderComponent */
    public static class ProviderComponent extends Component<ProviderIntentInfo> {
        public ProviderInfo info;

        public ProviderComponent(Provider provider) {
            super(provider);
            if (provider.intents != null) {
                this.intents = new ArrayList(provider.intents.size());
                Iterator it = provider.intents.iterator();
                while (it.hasNext()) {
                    this.intents.add(new ProviderIntentInfo((android.content.p000pm.PackageParser.IntentInfo) it.next()));
                }
            }
            this.info = provider.info;
        }

        protected ProviderComponent(Parcel parcel) {
            this.info = (ProviderInfo) parcel.readParcelable(ActivityInfo.class.getClassLoader());
            this.className = parcel.readString();
            this.metaData = parcel.readBundle(Bundle.class.getClassLoader());
            int readInt = parcel.readInt();
            this.intents = new ArrayList(readInt);
            while (true) {
                int i = readInt - 1;
                if (readInt > 0) {
                    this.intents.add(new ProviderIntentInfo(parcel));
                    readInt = i;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$ProviderIntentInfo */
    public static class ProviderIntentInfo extends IntentInfo {
        public ProviderComponent provider;

        public ProviderIntentInfo(android.content.p000pm.PackageParser.IntentInfo intentInfo) {
            super(intentInfo);
        }

        protected ProviderIntentInfo(Parcel parcel) {
            super(parcel);
        }
    }

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$ServiceComponent */
    public static class ServiceComponent extends Component<ServiceIntentInfo> {
        public ServiceInfo info;

        public ServiceComponent(Service service) {
            super(service);
            if (service.intents != null) {
                this.intents = new ArrayList(service.intents.size());
                Iterator it = service.intents.iterator();
                while (it.hasNext()) {
                    this.intents.add(new ServiceIntentInfo((android.content.p000pm.PackageParser.IntentInfo) it.next()));
                }
            }
            this.info = service.info;
        }

        protected ServiceComponent(Parcel parcel) {
            this.info = (ServiceInfo) parcel.readParcelable(ActivityInfo.class.getClassLoader());
            this.className = parcel.readString();
            this.metaData = parcel.readBundle(Bundle.class.getClassLoader());
            int readInt = parcel.readInt();
            this.intents = new ArrayList(readInt);
            while (true) {
                int i = readInt - 1;
                if (readInt > 0) {
                    this.intents.add(new ServiceIntentInfo(parcel));
                    readInt = i;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: com.lody.virtual.server.pm.parser.VPackage$ServiceIntentInfo */
    public static class ServiceIntentInfo extends IntentInfo {
        public ServiceComponent service;

        public ServiceIntentInfo(android.content.p000pm.PackageParser.IntentInfo intentInfo) {
            super(intentInfo);
        }

        protected ServiceIntentInfo(Parcel parcel) {
            super(parcel);
        }
    }

    public int describeContents() {
        return 0;
    }

    public VPackage() {
    }

    protected VPackage(Parcel parcel) {
        int readInt = parcel.readInt();
        this.activities = new ArrayList<>(readInt);
        while (true) {
            int i = readInt - 1;
            if (readInt <= 0) {
                break;
            }
            this.activities.add(new ActivityComponent(parcel));
            readInt = i;
        }
        int readInt2 = parcel.readInt();
        this.receivers = new ArrayList<>(readInt2);
        while (true) {
            int i2 = readInt2 - 1;
            if (readInt2 <= 0) {
                break;
            }
            this.receivers.add(new ActivityComponent(parcel));
            readInt2 = i2;
        }
        int readInt3 = parcel.readInt();
        this.providers = new ArrayList<>(readInt3);
        while (true) {
            int i3 = readInt3 - 1;
            if (readInt3 <= 0) {
                break;
            }
            this.providers.add(new ProviderComponent(parcel));
            readInt3 = i3;
        }
        int readInt4 = parcel.readInt();
        this.services = new ArrayList<>(readInt4);
        while (true) {
            int i4 = readInt4 - 1;
            if (readInt4 <= 0) {
                break;
            }
            this.services.add(new ServiceComponent(parcel));
            readInt4 = i4;
        }
        int readInt5 = parcel.readInt();
        this.instrumentation = new ArrayList<>(readInt5);
        while (true) {
            int i5 = readInt5 - 1;
            if (readInt5 <= 0) {
                break;
            }
            this.instrumentation.add(new InstrumentationComponent(parcel));
            readInt5 = i5;
        }
        int readInt6 = parcel.readInt();
        this.permissions = new ArrayList<>(readInt6);
        while (true) {
            int i6 = readInt6 - 1;
            if (readInt6 <= 0) {
                break;
            }
            this.permissions.add(new PermissionComponent(parcel));
            readInt6 = i6;
        }
        int readInt7 = parcel.readInt();
        this.permissionGroups = new ArrayList<>(readInt7);
        while (true) {
            int i7 = readInt7 - 1;
            if (readInt7 > 0) {
                this.permissionGroups.add(new PermissionGroupComponent(parcel));
                readInt7 = i7;
            } else {
                this.requestedPermissions = parcel.createStringArrayList();
                this.protectedBroadcasts = parcel.createStringArrayList();
                this.applicationInfo = (ApplicationInfo) parcel.readParcelable(ApplicationInfo.class.getClassLoader());
                this.mAppMetaData = parcel.readBundle(Bundle.class.getClassLoader());
                this.packageName = parcel.readString();
                this.mPreferredOrder = parcel.readInt();
                this.mVersionName = parcel.readString();
                this.mSharedUserId = parcel.readString();
                this.usesLibraries = parcel.createStringArrayList();
                this.mVersionCode = parcel.readInt();
                this.mSharedUserLabel = parcel.readInt();
                this.configPreferences = parcel.createTypedArrayList(ConfigurationInfo.CREATOR);
                this.reqFeatures = parcel.createTypedArrayList(FeatureInfo.CREATOR);
                return;
            }
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.activities.size());
        Iterator it = this.activities.iterator();
        while (true) {
            int i2 = 0;
            if (!it.hasNext()) {
                break;
            }
            ActivityComponent activityComponent = (ActivityComponent) it.next();
            parcel.writeParcelable(activityComponent.info, 0);
            parcel.writeString(activityComponent.className);
            parcel.writeBundle(activityComponent.metaData);
            if (activityComponent.intents != null) {
                i2 = activityComponent.intents.size();
            }
            parcel.writeInt(i2);
            if (activityComponent.intents != null) {
                Iterator it2 = activityComponent.intents.iterator();
                while (it2.hasNext()) {
                    ((ActivityIntentInfo) it2.next()).writeToParcel(parcel, i);
                }
            }
        }
        parcel.writeInt(this.receivers.size());
        Iterator it3 = this.receivers.iterator();
        while (it3.hasNext()) {
            ActivityComponent activityComponent2 = (ActivityComponent) it3.next();
            parcel.writeParcelable(activityComponent2.info, 0);
            parcel.writeString(activityComponent2.className);
            parcel.writeBundle(activityComponent2.metaData);
            parcel.writeInt(activityComponent2.intents != null ? activityComponent2.intents.size() : 0);
            if (activityComponent2.intents != null) {
                Iterator it4 = activityComponent2.intents.iterator();
                while (it4.hasNext()) {
                    ((ActivityIntentInfo) it4.next()).writeToParcel(parcel, i);
                }
            }
        }
        parcel.writeInt(this.providers.size());
        Iterator it5 = this.providers.iterator();
        while (it5.hasNext()) {
            ProviderComponent providerComponent = (ProviderComponent) it5.next();
            parcel.writeParcelable(providerComponent.info, 0);
            parcel.writeString(providerComponent.className);
            parcel.writeBundle(providerComponent.metaData);
            parcel.writeInt(providerComponent.intents != null ? providerComponent.intents.size() : 0);
            if (providerComponent.intents != null) {
                Iterator it6 = providerComponent.intents.iterator();
                while (it6.hasNext()) {
                    ((ProviderIntentInfo) it6.next()).writeToParcel(parcel, i);
                }
            }
        }
        parcel.writeInt(this.services.size());
        Iterator it7 = this.services.iterator();
        while (it7.hasNext()) {
            ServiceComponent serviceComponent = (ServiceComponent) it7.next();
            parcel.writeParcelable(serviceComponent.info, 0);
            parcel.writeString(serviceComponent.className);
            parcel.writeBundle(serviceComponent.metaData);
            parcel.writeInt(serviceComponent.intents != null ? serviceComponent.intents.size() : 0);
            if (serviceComponent.intents != null) {
                Iterator it8 = serviceComponent.intents.iterator();
                while (it8.hasNext()) {
                    ((ServiceIntentInfo) it8.next()).writeToParcel(parcel, i);
                }
            }
        }
        parcel.writeInt(this.instrumentation.size());
        Iterator it9 = this.instrumentation.iterator();
        while (it9.hasNext()) {
            InstrumentationComponent instrumentationComponent = (InstrumentationComponent) it9.next();
            parcel.writeParcelable(instrumentationComponent.info, 0);
            parcel.writeString(instrumentationComponent.className);
            parcel.writeBundle(instrumentationComponent.metaData);
            parcel.writeInt(instrumentationComponent.intents != null ? instrumentationComponent.intents.size() : 0);
            if (instrumentationComponent.intents != null) {
                Iterator it10 = instrumentationComponent.intents.iterator();
                while (it10.hasNext()) {
                    ((IntentInfo) it10.next()).writeToParcel(parcel, i);
                }
            }
        }
        parcel.writeInt(this.permissions.size());
        Iterator it11 = this.permissions.iterator();
        while (it11.hasNext()) {
            PermissionComponent permissionComponent = (PermissionComponent) it11.next();
            parcel.writeParcelable(permissionComponent.info, 0);
            parcel.writeString(permissionComponent.className);
            parcel.writeBundle(permissionComponent.metaData);
            parcel.writeInt(permissionComponent.intents != null ? permissionComponent.intents.size() : 0);
            if (permissionComponent.intents != null) {
                Iterator it12 = permissionComponent.intents.iterator();
                while (it12.hasNext()) {
                    ((IntentInfo) it12.next()).writeToParcel(parcel, i);
                }
            }
        }
        parcel.writeInt(this.permissionGroups.size());
        Iterator it13 = this.permissionGroups.iterator();
        while (it13.hasNext()) {
            PermissionGroupComponent permissionGroupComponent = (PermissionGroupComponent) it13.next();
            parcel.writeParcelable(permissionGroupComponent.info, 0);
            parcel.writeString(permissionGroupComponent.className);
            parcel.writeBundle(permissionGroupComponent.metaData);
            parcel.writeInt(permissionGroupComponent.intents != null ? permissionGroupComponent.intents.size() : 0);
            if (permissionGroupComponent.intents != null) {
                Iterator it14 = permissionGroupComponent.intents.iterator();
                while (it14.hasNext()) {
                    ((IntentInfo) it14.next()).writeToParcel(parcel, i);
                }
            }
        }
        parcel.writeStringList(this.requestedPermissions);
        parcel.writeStringList(this.protectedBroadcasts);
        parcel.writeParcelable(this.applicationInfo, i);
        parcel.writeBundle(this.mAppMetaData);
        parcel.writeString(this.packageName);
        parcel.writeInt(this.mPreferredOrder);
        parcel.writeString(this.mVersionName);
        parcel.writeString(this.mSharedUserId);
        parcel.writeStringList(this.usesLibraries);
        parcel.writeInt(this.mVersionCode);
        parcel.writeInt(this.mSharedUserLabel);
        parcel.writeTypedList(this.configPreferences);
        parcel.writeTypedList(this.reqFeatures);
    }
}
