package com.google.android.apps.nexuslauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.search.DefaultAppSearchAlgorithm;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.LooperExecutor;
import com.google.android.apps.nexuslauncher.clock.CustomClock.Metadata;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

public class CustomIconUtils {
    private static final String[] ICON_INTENTS = {"com.fede.launcher.THEME_ICONPACK", "com.anddoes.launcher.THEME", "com.novalauncher.THEME", "com.teslacoilsw.launcher.THEME", "com.gau.go.launcherex.theme", "org.adw.launcher.THEMES", "org.adw.launcher.icons.ACTION_PICK_ICON"};

    static HashMap<String, CharSequence> getPackProviders(Context context) {
        PackageManager packageManager = context.getPackageManager();
        HashMap<String, CharSequence> hashMap = new HashMap<>();
        for (String intent : ICON_INTENTS) {
            for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(new Intent(intent), 128)) {
                hashMap.put(resolveInfo.activityInfo.packageName, resolveInfo.loadLabel(packageManager));
            }
        }
        return hashMap;
    }

    static boolean isPackProvider(Context context, String str) {
        if (str != null && !str.isEmpty()) {
            PackageManager packageManager = context.getPackageManager();
            for (String intent : ICON_INTENTS) {
                if (packageManager.queryIntentActivities(new Intent(intent).setPackage(str), 128).iterator().hasNext()) {
                    return true;
                }
            }
        }
        return false;
    }

    static String getCurrentPack(Context context) {
        return Utilities.getPrefs(context).getString(SettingsActivity.ICON_PACK_PREF, "");
    }

    static void setCurrentPack(Context context, String str) {
        Editor edit = Utilities.getPrefs(context).edit();
        edit.putString(SettingsActivity.ICON_PACK_PREF, str);
        edit.apply();
    }

    static boolean usingValidPack(Context context) {
        return isPackProvider(context, getCurrentPack(context));
    }

    static void applyIconPackAsync(final Context context) {
        new LooperExecutor(LauncherModel.getWorkerLooper()).execute(new Runnable() {
            public void run() {
                UserManagerCompat instance = UserManagerCompat.getInstance(context);
                LauncherModel model = LauncherAppState.getInstance(context).getModel();
                boolean isEmpty = CustomIconUtils.getCurrentPack(context).isEmpty();
                Utilities.getPrefs(context).edit().putBoolean(DefaultAppSearchAlgorithm.SEARCH_HIDDEN_APPS, !isEmpty).apply();
                if (isEmpty) {
                    CustomAppFilter.resetAppFilter(context);
                }
                for (UserHandle onPackagesReload : instance.getUserProfiles()) {
                    model.onPackagesReload(onPackagesReload);
                }
                CustomIconProvider.clearDisabledApps(context);
                ((CustomDrawableFactory) DrawableFactory.get(context)).reloadIconPack();
                DeepShortcutManager instance2 = DeepShortcutManager.getInstance(context);
                LauncherAppsCompat instance3 = LauncherAppsCompat.getInstance(context);
                for (UserHandle userHandle : instance.getUserProfiles()) {
                    HashSet hashSet = new HashSet();
                    for (LauncherActivityInfo componentName : instance3.getActivityList(null, userHandle)) {
                        hashSet.add(componentName.getComponentName().getPackageName());
                    }
                    Iterator it = hashSet.iterator();
                    while (it.hasNext()) {
                        CustomIconUtils.reloadIcon(instance2, model, userHandle, (String) it.next());
                    }
                }
            }
        });
    }

    static void reloadIconByKey(Context context, ComponentKey componentKey) {
        reloadIcon(DeepShortcutManager.getInstance(context), LauncherAppState.getInstance(context).getModel(), componentKey.user, componentKey.componentName.getPackageName());
    }

    static void reloadIcon(DeepShortcutManager deepShortcutManager, LauncherModel launcherModel, UserHandle userHandle, String str) {
        launcherModel.onPackageChanged(str, userHandle);
        List queryForPinnedShortcuts = deepShortcutManager.queryForPinnedShortcuts(str, userHandle);
        if (!queryForPinnedShortcuts.isEmpty()) {
            launcherModel.updatePinnedShortcuts(str, queryForPinnedShortcuts, userHandle);
        }
    }

    static void parsePack(CustomDrawableFactory customDrawableFactory, PackageManager packageManager, String str) {
        String str2;
        CustomDrawableFactory customDrawableFactory2 = customDrawableFactory;
        String str3 = str;
        try {
            Resources resourcesForApplication = packageManager.getResourcesForApplication(str);
            int identifier = resourcesForApplication.getIdentifier("appfilter", "xml", str3);
            if (identifier != 0) {
                String str4 = "ComponentInfo{";
                int length = str4.length();
                String str5 = "}";
                int length2 = str5.length();
                String str6 = null;
                XmlResourceParser xml = packageManager.getXml(str3, identifier, null);
                while (xml.next() != 1) {
                    if (xml.getEventType() == 2) {
                        String name = xml.getName();
                        boolean equals = name.equals("calendar");
                        if (!equals) {
                            if (!name.equals("item")) {
                                if (name.equals("dynamic-clock")) {
                                    String attributeValue = xml.getAttributeValue(str6, "drawable");
                                    if (attributeValue != null) {
                                        int identifier2 = resourcesForApplication.getIdentifier(attributeValue, "drawable", str3);
                                        if (identifier2 != 0) {
                                            Map<Integer, Metadata> map = customDrawableFactory2.packClocks;
                                            Integer valueOf = Integer.valueOf(identifier2);
                                            int attributeIntValue = xml.getAttributeIntValue(str6, "hourLayerIndex", -1);
                                            int attributeIntValue2 = xml.getAttributeIntValue(str6, "minuteLayerIndex", -1);
                                            int attributeIntValue3 = xml.getAttributeIntValue(str6, "secondLayerIndex", -1);
                                            int attributeIntValue4 = xml.getAttributeIntValue(str6, "defaultHour", 0);
                                            int attributeIntValue5 = xml.getAttributeIntValue(str6, "defaultMinute", 0);
                                            int attributeIntValue6 = xml.getAttributeIntValue(str6, "defaultSecond", 0);
                                            Metadata metadata = r11;
                                            Metadata metadata2 = new Metadata(attributeIntValue, attributeIntValue2, attributeIntValue3, attributeIntValue4, attributeIntValue5, attributeIntValue6);
                                            map.put(valueOf, metadata);
                                            str2 = null;
                                            str6 = str2;
                                        }
                                    }
                                }
                                str2 = str6;
                                str6 = str2;
                            }
                        }
                        str2 = null;
                        String attributeValue2 = xml.getAttributeValue(null, "component");
                        String attributeValue3 = xml.getAttributeValue(null, equals ? "prefix" : "drawable");
                        if (attributeValue2 != null && attributeValue3 != null && attributeValue2.startsWith(str4) && attributeValue2.endsWith(str5)) {
                            ComponentName unflattenFromString = ComponentName.unflattenFromString(attributeValue2.substring(length, attributeValue2.length() - length2));
                            if (unflattenFromString != null) {
                                if (equals) {
                                    customDrawableFactory2.packCalendars.put(unflattenFromString, attributeValue3);
                                } else {
                                    int identifier3 = resourcesForApplication.getIdentifier(attributeValue3, "drawable", str3);
                                    if (identifier3 != 0) {
                                        customDrawableFactory2.packComponents.put(unflattenFromString, Integer.valueOf(identifier3));
                                    }
                                }
                            }
                        }
                        str6 = str2;
                    }
                }
            }
        } catch (NameNotFoundException | IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
