package android.content.p000pm;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.os.Bundle;
import java.util.ArrayList;

/* renamed from: android.content.pm.PackageParser */
public class PackageParser {
    public static final int PARSE_IS_SYSTEM = 1;

    /* renamed from: android.content.pm.PackageParser$Activity */
    public static final class Activity extends Component<ActivityIntentInfo> {
        public ActivityInfo info;
    }

    /* renamed from: android.content.pm.PackageParser$ActivityIntentInfo */
    public class ActivityIntentInfo extends IntentInfo {
        public Activity activity;

        public ActivityIntentInfo() {
        }
    }

    /* renamed from: android.content.pm.PackageParser$Component */
    public static class Component<II extends IntentInfo> {
        public String className;
        public ArrayList<II> intents;
        public Bundle metaData;
        public Package owner;

        public ComponentName getComponentName() {
            return null;
        }
    }

    /* renamed from: android.content.pm.PackageParser$Instrumentation */
    public final class Instrumentation extends Component<IntentInfo> {
        public InstrumentationInfo info;

        public Instrumentation() {
        }
    }

    /* renamed from: android.content.pm.PackageParser$IntentInfo */
    public static class IntentInfo extends IntentFilter {
        public int banner;
        public boolean hasDefault;
        public int icon;
        public int labelRes;
        public int logo;
        public CharSequence nonLocalizedLabel;
    }

    /* renamed from: android.content.pm.PackageParser$Package */
    public class Package {
        public final ArrayList<Activity> activities = new ArrayList<>(0);
        public ApplicationInfo applicationInfo;
        public ArrayList<ConfigurationInfo> configPreferences = null;
        public final ArrayList<Instrumentation> instrumentation = new ArrayList<>(0);
        public Bundle mAppMetaData;
        public Object mExtras;
        public int mPreferredOrder;
        public String mSharedUserId;
        public int mSharedUserLabel;
        public Signature[] mSignatures;
        public int mVersionCode;
        public String mVersionName;
        public String packageName;
        public final ArrayList<PermissionGroup> permissionGroups = new ArrayList<>(0);
        public final ArrayList<Permission> permissions = new ArrayList<>(0);
        public final ArrayList<Provider> providers = new ArrayList<>(0);
        public final ArrayList<Activity> receivers = new ArrayList<>(0);
        public ArrayList<FeatureInfo> reqFeatures = null;
        public final ArrayList<String> requestedPermissions = new ArrayList<>();
        public final ArrayList<Service> services = new ArrayList<>(0);
        public ArrayList<String> usesLibraries;

        public Package() {
        }
    }

    /* renamed from: android.content.pm.PackageParser$Permission */
    public final class Permission extends Component<IntentInfo> {
        public PermissionInfo info;

        public Permission() {
        }
    }

    /* renamed from: android.content.pm.PackageParser$PermissionGroup */
    public final class PermissionGroup extends Component<IntentInfo> {
        public PermissionGroupInfo info;

        public PermissionGroup() {
        }
    }

    /* renamed from: android.content.pm.PackageParser$Provider */
    public final class Provider extends Component<ProviderIntentInfo> {
        public ProviderInfo info;

        public Provider() {
        }
    }

    /* renamed from: android.content.pm.PackageParser$ProviderIntentInfo */
    public class ProviderIntentInfo extends IntentInfo {
        public Provider provider;

        public ProviderIntentInfo() {
        }
    }

    /* renamed from: android.content.pm.PackageParser$Service */
    public final class Service extends Component<ServiceIntentInfo> {
        public ServiceInfo info;

        public Service() {
        }
    }

    /* renamed from: android.content.pm.PackageParser$ServiceIntentInfo */
    public class ServiceIntentInfo extends IntentInfo {
        public Service service;

        public ServiceIntentInfo() {
        }
    }
}
