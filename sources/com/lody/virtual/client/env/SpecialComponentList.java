package com.lody.virtual.client.env;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import mirror.android.webkit.IWebViewUpdateService;
import mirror.android.webkit.WebViewFactory;

public final class SpecialComponentList {
    private static final List<String> ACTION_BLACK_LIST = new ArrayList(1);
    private static final HashSet<String> INSTRUMENTATION_CONFLICTING = new HashSet<>(2);
    private static final Map<String, String> PROTECTED_ACTION_MAP = new HashMap(5);
    private static String PROTECT_ACTION_PREFIX = "_VA_protected_";
    private static final HashSet<String> SPEC_SYSTEM_APP_LIST = new HashSet<>(3);
    private static final Set<String> SYSTEM_BROADCAST_ACTION = new HashSet(7);
    private static final HashSet<String> WHITE_PERMISSION = new HashSet<>(3);

    static {
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.DOWNLOAD_COMPLETE");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.SCREEN_ON");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.SCREEN_OFF");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.NEW_OUTGOING_CALL");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.TIME_TICK");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.TIME_SET");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.TIMEZONE_CHANGED");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.BATTERY_CHANGED");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.BATTERY_LOW");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.BATTERY_OKAY");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.ACTION_POWER_CONNECTED");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.ACTION_POWER_DISCONNECTED");
        SYSTEM_BROADCAST_ACTION.add("android.provider.Telephony.SMS_RECEIVED");
        SYSTEM_BROADCAST_ACTION.add("android.provider.Telephony.SMS_DELIVER");
        SYSTEM_BROADCAST_ACTION.add("android.net.wifi.STATE_CHANGE");
        SYSTEM_BROADCAST_ACTION.add("android.net.wifi.SCAN_RESULTS");
        SYSTEM_BROADCAST_ACTION.add("android.net.wifi.WIFI_STATE_CHANGED");
        SYSTEM_BROADCAST_ACTION.add("android.net.conn.CONNECTIVITY_CHANGE");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.ANY_DATA_STATE");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.SIM_STATE_CHANGED");
        SYSTEM_BROADCAST_ACTION.add("android.location.PROVIDERS_CHANGED");
        SYSTEM_BROADCAST_ACTION.add("android.location.MODE_CHANGED");
        ACTION_BLACK_LIST.add("android.appwidget.action.APPWIDGET_UPDATE");
        WHITE_PERMISSION.add("com.google.android.gms.settings.SECURITY_SETTINGS");
        WHITE_PERMISSION.add("com.google.android.apps.plus.PRIVACY_SETTINGS");
        WHITE_PERMISSION.add("android.permission.ACCOUNT_MANAGER");
        PROTECTED_ACTION_MAP.put("android.intent.action.PACKAGE_ADDED", Constants.ACTION_PACKAGE_ADDED);
        PROTECTED_ACTION_MAP.put("android.intent.action.PACKAGE_REMOVED", Constants.ACTION_PACKAGE_REMOVED);
        PROTECTED_ACTION_MAP.put("android.intent.action.PACKAGE_CHANGED", Constants.ACTION_PACKAGE_CHANGED);
        PROTECTED_ACTION_MAP.put("android.intent.action.USER_ADDED", Constants.ACTION_USER_ADDED);
        PROTECTED_ACTION_MAP.put("android.intent.action.USER_REMOVED", Constants.ACTION_USER_REMOVED);
        INSTRUMENTATION_CONFLICTING.add("com.qihoo.magic");
        INSTRUMENTATION_CONFLICTING.add("com.qihoo.magic_mutiple");
        INSTRUMENTATION_CONFLICTING.add("com.facebook.katana");
        SPEC_SYSTEM_APP_LIST.add("android");
        SPEC_SYSTEM_APP_LIST.add("com.google.android.webview");
        if (VERSION.SDK_INT >= 24) {
            try {
                String str = (String) IWebViewUpdateService.getCurrentWebViewPackageName.call(WebViewFactory.getUpdateService.call(new Object[0]), new Object[0]);
                if (str != null) {
                    SPEC_SYSTEM_APP_LIST.add(str);
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public static boolean isSpecSystemPackage(String str) {
        return SPEC_SYSTEM_APP_LIST.contains(str);
    }

    public static boolean isConflictingInstrumentation(String str) {
        return INSTRUMENTATION_CONFLICTING.contains(str);
    }

    public static boolean isActionInBlackList(String str) {
        return ACTION_BLACK_LIST.contains(str);
    }

    public static void addBlackAction(String str) {
        ACTION_BLACK_LIST.add(str);
    }

    public static void protectIntentFilter(IntentFilter intentFilter) {
        if (intentFilter != null) {
            ListIterator listIterator = ((List) mirror.android.content.IntentFilter.mActions.get(intentFilter)).listIterator();
            while (listIterator.hasNext()) {
                String str = (String) listIterator.next();
                if (isActionInBlackList(str)) {
                    listIterator.remove();
                } else if (!SYSTEM_BROADCAST_ACTION.contains(str)) {
                    String protectAction = protectAction(str);
                    if (protectAction != null) {
                        listIterator.set(protectAction);
                    }
                }
            }
        }
    }

    public static void protectIntent(Intent intent) {
        String protectAction = protectAction(intent.getAction());
        if (protectAction != null) {
            intent.setAction(protectAction);
        }
    }

    public static void unprotectIntent(Intent intent) {
        String unprotectAction = unprotectAction(intent.getAction());
        if (unprotectAction != null) {
            intent.setAction(unprotectAction);
        }
    }

    public static String protectAction(String str) {
        if (str == null) {
            return null;
        }
        if (str.startsWith("_VA_")) {
            return str;
        }
        String str2 = (String) PROTECTED_ACTION_MAP.get(str);
        if (str2 == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(PROTECT_ACTION_PREFIX);
            sb.append(str);
            str2 = sb.toString();
        }
        return str2;
    }

    public static String unprotectAction(String str) {
        if (str == null) {
            return null;
        }
        if (str.startsWith(PROTECT_ACTION_PREFIX)) {
            return str.substring(PROTECT_ACTION_PREFIX.length());
        }
        for (Entry entry : PROTECTED_ACTION_MAP.entrySet()) {
            if (((String) entry.getValue()).equals(str)) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public static boolean isWhitePermission(String str) {
        return WHITE_PERMISSION.contains(str);
    }
}
