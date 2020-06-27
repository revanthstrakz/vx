package com.microsoft.appcenter.utils.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Set;

public class SharedPreferencesManager {
    private static final String PREFERENCES_NAME = "AppCenter";
    @SuppressLint({"StaticFieldLeak"})
    private static Context sContext;
    private static SharedPreferences sSharedPreferences;

    public static synchronized void initialize(Context context) {
        synchronized (SharedPreferencesManager.class) {
            if (sContext == null) {
                sContext = context;
                sSharedPreferences = sContext.getSharedPreferences("AppCenter", 0);
            }
        }
    }

    public static boolean getBoolean(@NonNull String str) {
        return getBoolean(str, false);
    }

    public static boolean getBoolean(@NonNull String str, boolean z) {
        return sSharedPreferences.getBoolean(str, z);
    }

    public static void putBoolean(@NonNull String str, boolean z) {
        Editor edit = sSharedPreferences.edit();
        edit.putBoolean(str, z);
        edit.apply();
    }

    public static float getFloat(@NonNull String str) {
        return getFloat(str, 0.0f);
    }

    public static float getFloat(@NonNull String str, float f) {
        return sSharedPreferences.getFloat(str, f);
    }

    public static void putFloat(@NonNull String str, float f) {
        Editor edit = sSharedPreferences.edit();
        edit.putFloat(str, f);
        edit.apply();
    }

    public static int getInt(@NonNull String str) {
        return getInt(str, 0);
    }

    public static int getInt(@NonNull String str, int i) {
        return sSharedPreferences.getInt(str, i);
    }

    public static void putInt(@NonNull String str, int i) {
        Editor edit = sSharedPreferences.edit();
        edit.putInt(str, i);
        edit.apply();
    }

    public static long getLong(@NonNull String str) {
        return getLong(str, 0);
    }

    public static long getLong(@NonNull String str, long j) {
        return sSharedPreferences.getLong(str, j);
    }

    public static void putLong(@NonNull String str, long j) {
        Editor edit = sSharedPreferences.edit();
        edit.putLong(str, j);
        edit.apply();
    }

    @Nullable
    public static String getString(@NonNull String str) {
        return getString(str, null);
    }

    public static String getString(@NonNull String str, String str2) {
        return sSharedPreferences.getString(str, str2);
    }

    public static void putString(@NonNull String str, String str2) {
        Editor edit = sSharedPreferences.edit();
        edit.putString(str, str2);
        edit.apply();
    }

    public static Set<String> getStringSet(@NonNull String str) {
        return getStringSet(str, null);
    }

    public static Set<String> getStringSet(@NonNull String str, Set<String> set) {
        return sSharedPreferences.getStringSet(str, set);
    }

    public static void putStringSet(@NonNull String str, Set<String> set) {
        Editor edit = sSharedPreferences.edit();
        edit.putStringSet(str, set);
        edit.apply();
    }

    public static void remove(@NonNull String str) {
        Editor edit = sSharedPreferences.edit();
        edit.remove(str);
        edit.apply();
    }

    public static void clear() {
        Editor edit = sSharedPreferences.edit();
        edit.clear();
        edit.apply();
    }
}
