package com.lody.virtual.client.stub;

import java.util.Locale;

public class VASettings {
    public static final String ACTION_BADGER_CHANGE = "com.lody.virtual.BADGER_CHANGE";
    public static boolean ENABLE_INNER_SHORTCUT = true;
    public static boolean ENABLE_IO_REDIRECT = true;
    public static String[] PRIVILEGE_APPS = {"com.google.android.gms"};
    public static String RESOLVER_ACTIVITY = ResolverActivity.class.getName();
    public static String STUB_ACTIVITY = StubActivity.class.getName();
    public static int STUB_COUNT = 50;
    public static String STUB_CP = StubCP.class.getName();
    public static String STUB_CP_AUTHORITY = STUB_DEF_AUTHORITY;
    public static final String STUB_DEF_AUTHORITY = "virtual_stub_";
    public static String STUB_DIALOG = StubDialog.class.getName();
    public static String STUB_EXCLUDE_FROM_RECENT_ACTIVITY = StubExcludeFromRecentActivity.class.getName();
    public static String STUB_JOB = StubJob.class.getName();

    public static class Wifi {
        public static String BSSID = DEFAULT_BSSID;
        public static String DEFAULT_BSSID = "66:55:44:33:22:11";
        public static String DEFAULT_MAC = "11:22:33:44:55:66";
        public static String DEFAULT_SSID = "VirtualApp";
        public static boolean FAKE_WIFI_STATE = false;
        public static String MAC = DEFAULT_MAC;
        public static String SSID = DEFAULT_SSID;
    }

    public static String getStubExcludeFromRecentActivityName(int i) {
        return String.format(Locale.ENGLISH, "%s$C%d", new Object[]{STUB_EXCLUDE_FROM_RECENT_ACTIVITY, Integer.valueOf(i)});
    }

    public static String getStubActivityName(int i) {
        return String.format(Locale.ENGLISH, "%s$C%d", new Object[]{STUB_ACTIVITY, Integer.valueOf(i)});
    }

    public static String getStubDialogName(int i) {
        return String.format(Locale.ENGLISH, "%s$C%d", new Object[]{STUB_DIALOG, Integer.valueOf(i)});
    }

    public static String getStubCP(int i) {
        return String.format(Locale.ENGLISH, "%s$C%d", new Object[]{STUB_CP, Integer.valueOf(i)});
    }

    public static String getStubAuthority(int i) {
        return String.format(Locale.ENGLISH, "%s%d", new Object[]{STUB_CP_AUTHORITY, Integer.valueOf(i)});
    }
}
