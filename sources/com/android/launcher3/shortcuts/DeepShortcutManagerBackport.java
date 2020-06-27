package com.android.launcher3.shortcuts;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Process;
import com.android.launcher3.Utilities;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DeepShortcutManagerBackport {
    static Drawable getShortcutIconDrawable(ShortcutInfoCompat shortcutInfoCompat, int i) {
        return ((ShortcutInfoCompatBackport) shortcutInfoCompat).getIcon(i);
    }

    public static List<ShortcutInfoCompat> getForPackage(Context context, LauncherApps launcherApps, ComponentName componentName, String str) {
        ArrayList arrayList = new ArrayList();
        if (Utilities.ATLEAST_MARSHMALLOW) {
            for (LauncherActivityInfo launcherActivityInfo : launcherApps.getActivityList(str, Process.myUserHandle())) {
                if (componentName == null || componentName.equals(launcherActivityInfo.getComponentName())) {
                    parsePackageXml(context, launcherActivityInfo.getComponentName().getPackageName(), launcherActivityInfo.getComponentName(), arrayList);
                }
            }
        }
        return arrayList;
    }

    private static void parsePackageXml(Context context, String str, ComponentName componentName, List<ShortcutInfoCompat> list) {
        PackageManager packageManager = context.getPackageManager();
        String str2 = "";
        String className = componentName.getClassName();
        HashMap hashMap = new HashMap();
        try {
            Resources resourcesForApplication = packageManager.getResourcesForApplication(str);
            XmlResourceParser openXmlResourceParser = resourcesForApplication.getAssets().openXmlResourceParser("AndroidManifest.xml");
            String str3 = str2;
            String str4 = null;
            while (true) {
                int nextToken = openXmlResourceParser.nextToken();
                int i = 0;
                if (nextToken == 1) {
                    break;
                } else if (nextToken == 2) {
                    String name = openXmlResourceParser.getName();
                    if (!ServiceManagerNative.ACTIVITY.equals(name)) {
                        if (!"activity-alias".equals(name)) {
                            if (name.equals("meta-data") && str3.equals(className)) {
                                hashMap.clear();
                                while (i < openXmlResourceParser.getAttributeCount()) {
                                    hashMap.put(openXmlResourceParser.getAttributeName(i), openXmlResourceParser.getAttributeValue(i));
                                    i++;
                                }
                                if (hashMap.containsKey(CommonProperties.NAME) && ((String) hashMap.get(CommonProperties.NAME)).equals("android.app.shortcuts") && hashMap.containsKey("resource")) {
                                    str4 = (String) hashMap.get("resource");
                                }
                            }
                        }
                    }
                    hashMap.clear();
                    while (i < openXmlResourceParser.getAttributeCount()) {
                        hashMap.put(openXmlResourceParser.getAttributeName(i), openXmlResourceParser.getAttributeValue(i));
                        i++;
                    }
                    if (hashMap.containsKey(CommonProperties.NAME)) {
                        str3 = (String) hashMap.get(CommonProperties.NAME);
                    }
                }
            }
            openXmlResourceParser.close();
            if (str4 != null) {
                int identifier = resourcesForApplication.getIdentifier(str4, null, str);
                if (identifier == 0) {
                    identifier = Integer.parseInt(str4.substring(1));
                }
                XmlResourceParser xml = resourcesForApplication.getXml(identifier);
                while (true) {
                    int nextToken2 = xml.nextToken();
                    if (nextToken2 == 1) {
                        xml.close();
                        return;
                    } else if (nextToken2 == 2 && xml.getName().equals("shortcut")) {
                        ShortcutInfoCompat parseShortcut = parseShortcut(context, componentName, resourcesForApplication, str, xml);
                        if (parseShortcut != null && parseShortcut.getId() != null) {
                            Iterator it = packageManager.queryIntentActivities(ShortcutInfoCompatBackport.stripPackage(parseShortcut.makeIntent()), 0).iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                ResolveInfo resolveInfo = (ResolveInfo) it.next();
                                if (!resolveInfo.isDefault) {
                                    if (resolveInfo.activityInfo.exported) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            list.add(parseShortcut);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ShortcutInfoCompat parseShortcut(Context context, ComponentName componentName, Resources resources, String str, XmlResourceParser xmlResourceParser) {
        try {
            ShortcutInfoCompatBackport shortcutInfoCompatBackport = new ShortcutInfoCompatBackport(context, resources, str, componentName, xmlResourceParser);
            return shortcutInfoCompatBackport;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
