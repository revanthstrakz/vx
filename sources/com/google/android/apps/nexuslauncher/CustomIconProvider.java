package com.google.android.apps.nexuslauncher;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.util.ComponentKey;
import com.google.android.apps.nexuslauncher.clock.CustomClock;
import com.google.android.apps.nexuslauncher.clock.CustomClock.Metadata;
import com.google.android.apps.nexuslauncher.clock.DynamicClock;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CustomIconProvider extends DynamicIconProvider {
    public static final String DISABLE_PACK_PREF = "all_apps_disable_pack";
    /* access modifiers changed from: private */
    public final Context mContext;
    private final BroadcastReceiver mDateChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!Utilities.ATLEAST_NOUGAT) {
                int i = Calendar.getInstance().get(5);
                if (i != CustomIconProvider.this.mDateOfMonth) {
                    CustomIconProvider.this.mDateOfMonth = i;
                } else {
                    return;
                }
            }
            LauncherAppsCompat instance = LauncherAppsCompat.getInstance(CustomIconProvider.this.mContext);
            LauncherModel model = LauncherAppState.getInstance(context).getModel();
            DeepShortcutManager instance2 = DeepShortcutManager.getInstance(context);
            for (UserHandle userHandle : UserManagerCompat.getInstance(context).getUserProfiles()) {
                HashSet<String> hashSet = new HashSet<>();
                for (ComponentName packageName : CustomIconProvider.this.mFactory.packCalendars.keySet()) {
                    String packageName2 = packageName.getPackageName();
                    if (!instance.getActivityList(packageName2, userHandle).isEmpty()) {
                        hashSet.add(packageName2);
                    }
                }
                for (String reloadIcon : hashSet) {
                    CustomIconUtils.reloadIcon(instance2, model, userHandle, reloadIcon);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int mDateOfMonth;
    /* access modifiers changed from: private */
    public CustomDrawableFactory mFactory;

    public CustomIconProvider(Context context) {
        super(context);
        this.mContext = context;
        this.mFactory = (CustomDrawableFactory) DrawableFactory.get(context);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        if (!Utilities.ATLEAST_NOUGAT) {
            intentFilter.addAction("android.intent.action.TIME_TICK");
        }
        this.mContext.registerReceiver(this.mDateChangeReceiver, intentFilter, null, new Handler(LauncherModel.getWorkerLooper()));
    }

    public Drawable getIcon(LauncherActivityInfo launcherActivityInfo, int i, boolean z) {
        this.mFactory.ensureInitialLoadComplete();
        String str = launcherActivityInfo.getApplicationInfo().packageName;
        ComponentName componentName = launcherActivityInfo.getComponentName();
        Drawable drawable = null;
        if (CustomIconUtils.usingValidPack(this.mContext) && isEnabledForApp(this.mContext, new ComponentKey(componentName, launcherActivityInfo.getUser()))) {
            PackageManager packageManager = this.mContext.getPackageManager();
            if (this.mFactory.packCalendars.containsKey(componentName)) {
                try {
                    Resources resourcesForApplication = packageManager.getResourcesForApplication(this.mFactory.iconPack);
                    StringBuilder sb = new StringBuilder();
                    sb.append((String) this.mFactory.packCalendars.get(componentName));
                    sb.append(Calendar.getInstance().get(5));
                    int identifier = resourcesForApplication.getIdentifier(sb.toString(), "drawable", this.mFactory.iconPack);
                    if (identifier != 0) {
                        drawable = packageManager.getDrawable(this.mFactory.iconPack, identifier, null);
                    }
                } catch (NameNotFoundException unused) {
                }
            } else if (this.mFactory.packComponents.containsKey(componentName)) {
                int intValue = ((Integer) this.mFactory.packComponents.get(componentName)).intValue();
                drawable = packageManager.getDrawable(this.mFactory.iconPack, ((Integer) this.mFactory.packComponents.get(componentName)).intValue(), null);
                if (Utilities.ATLEAST_OREO && this.mFactory.packClocks.containsKey(Integer.valueOf(intValue))) {
                    drawable = CustomClock.getClock(this.mContext, drawable, (Metadata) this.mFactory.packClocks.get(Integer.valueOf(intValue)), i);
                }
            }
        }
        if (drawable == null && !DynamicIconProvider.GOOGLE_CALENDAR.equals(str) && !DynamicClock.DESK_CLOCK.equals(componentName)) {
            drawable = getRoundIcon(componentName, i);
        }
        return drawable == null ? super.getIcon(launcherActivityInfo, i, z) : drawable.mutate();
    }

    private Drawable getRoundIcon(ComponentName componentName, int i) {
        HashMap hashMap = new HashMap();
        try {
            Resources resourcesForApplication = this.mContext.getPackageManager().getResourcesForApplication(componentName.getPackageName());
            XmlResourceParser openXmlResourceParser = resourcesForApplication.getAssets().openXmlResourceParser("AndroidManifest.xml");
            String str = null;
            while (true) {
                if (openXmlResourceParser.next() == 1) {
                    break;
                } else if (openXmlResourceParser.getEventType() == 2) {
                    String name = openXmlResourceParser.getName();
                    for (int i2 = 0; i2 < openXmlResourceParser.getAttributeCount(); i2++) {
                        hashMap.put(openXmlResourceParser.getAttributeName(i2), openXmlResourceParser.getAttributeValue(i2));
                    }
                    if (hashMap.containsKey(BaseLauncherColumns.ICON)) {
                        if (!name.equals("application")) {
                            if ((name.equals(ServiceManagerNative.ACTIVITY) || name.equals("activity-alias")) && hashMap.containsKey(CommonProperties.NAME) && ((String) hashMap.get(CommonProperties.NAME)).equals(componentName.getClassName())) {
                                str = (String) hashMap.get("roundIcon");
                                break;
                            }
                        } else {
                            str = (String) hashMap.get("roundIcon");
                        }
                    }
                    hashMap.clear();
                }
            }
            openXmlResourceParser.close();
            if (str != null) {
                int identifier = resourcesForApplication.getIdentifier(str, null, componentName.getPackageName());
                if (identifier == 0) {
                    identifier = Integer.parseInt(str.substring(1));
                }
                return resourcesForApplication.getDrawableForDensity(identifier, i);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    static void clearDisabledApps(Context context) {
        setDisabledApps(context, new HashSet());
    }

    static boolean isEnabledForApp(Context context, ComponentKey componentKey) {
        return !getDisabledApps(context).contains(componentKey.toString());
    }

    static void setAppState(Context context, ComponentKey componentKey, boolean z) {
        String componentKey2 = componentKey.toString();
        Set disabledApps = getDisabledApps(context);
        while (disabledApps.contains(componentKey2)) {
            disabledApps.remove(componentKey2);
        }
        if (!z) {
            disabledApps.add(componentKey2);
        }
        setDisabledApps(context, disabledApps);
    }

    private static Set<String> getDisabledApps(Context context) {
        return new HashSet(Utilities.getPrefs(context).getStringSet(DISABLE_PACK_PREF, new HashSet()));
    }

    private static void setDisabledApps(Context context, Set<String> set) {
        Editor edit = Utilities.getPrefs(context).edit();
        edit.putStringSet(DISABLE_PACK_PREF, set);
        edit.apply();
    }
}
