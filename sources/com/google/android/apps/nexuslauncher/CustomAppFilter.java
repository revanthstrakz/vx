package com.google.android.apps.nexuslauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.UserHandle;
import com.android.launcher3.BuildConfig;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.util.ComponentKey;
import java.util.HashSet;
import java.util.Set;

public class CustomAppFilter extends NexusAppFilter {
    public static final String HIDE_APPS_PREF = "all_apps_hide";
    private final Context mContext;

    public CustomAppFilter(Context context) {
        super(context);
        this.mContext = context;
    }

    public boolean shouldShowApp(ComponentName componentName, UserHandle userHandle) {
        if (componentName.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
            return false;
        }
        if (CustomIconUtils.usingValidPack(this.mContext)) {
            return !isHiddenApp(this.mContext, new ComponentKey(componentName, userHandle));
        }
        return super.shouldShowApp(componentName, userHandle);
    }

    static void resetAppFilter(Context context) {
        Editor edit = Utilities.getPrefs(context).edit();
        edit.putStringSet(HIDE_APPS_PREF, new HashSet());
        edit.apply();
    }

    static void setComponentNameState(Context context, ComponentKey componentKey, boolean z) {
        String componentKey2 = componentKey.toString();
        Set hiddenApps = getHiddenApps(context);
        while (hiddenApps.contains(componentKey2)) {
            hiddenApps.remove(componentKey2);
        }
        if (z != CustomIconUtils.isPackProvider(context, componentKey.componentName.getPackageName())) {
            hiddenApps.add(componentKey2);
        }
        setHiddenApps(context, hiddenApps);
        LauncherModel model = Launcher.getLauncher(context).getModel();
        for (UserHandle onPackagesReload : UserManagerCompat.getInstance(context).getUserProfiles()) {
            model.onPackagesReload(onPackagesReload);
        }
    }

    static boolean isHiddenApp(Context context, ComponentKey componentKey) {
        return getHiddenApps(context).contains(componentKey.toString()) != CustomIconUtils.isPackProvider(context, componentKey.componentName.getPackageName());
    }

    private static Set<String> getHiddenApps(Context context) {
        return new HashSet(Utilities.getPrefs(context).getStringSet(HIDE_APPS_PREF, new HashSet()));
    }

    private static void setHiddenApps(Context context, Set<String> set) {
        Editor edit = Utilities.getPrefs(context).edit();
        edit.putStringSet(HIDE_APPS_PREF, set);
        edit.apply();
    }
}
