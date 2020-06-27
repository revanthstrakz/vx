package com.android.launcher3.util;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;

public interface SettingsObserver {

    public static abstract class Secure extends ContentObserver implements SettingsObserver {
        private String mKeySetting;
        private ContentResolver mResolver;

        public Secure(ContentResolver contentResolver) {
            super(new Handler());
            this.mResolver = contentResolver;
        }

        public void register(String str, String... strArr) {
            this.mKeySetting = str;
            this.mResolver.registerContentObserver(android.provider.Settings.Secure.getUriFor(this.mKeySetting), false, this);
            for (String uriFor : strArr) {
                this.mResolver.registerContentObserver(android.provider.Settings.Secure.getUriFor(uriFor), false, this);
            }
            onChange(true);
        }

        public void unregister() {
            this.mResolver.unregisterContentObserver(this);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            boolean z2 = true;
            if (android.provider.Settings.Secure.getInt(this.mResolver, this.mKeySetting, 1) != 1) {
                z2 = false;
            }
            onSettingChanged(z2);
        }
    }

    public static abstract class System extends ContentObserver implements SettingsObserver {
        private String mKeySetting;
        private ContentResolver mResolver;

        public System(ContentResolver contentResolver) {
            super(new Handler());
            this.mResolver = contentResolver;
        }

        public void register(String str, String... strArr) {
            this.mKeySetting = str;
            this.mResolver.registerContentObserver(android.provider.Settings.System.getUriFor(this.mKeySetting), false, this);
            for (String uriFor : strArr) {
                this.mResolver.registerContentObserver(android.provider.Settings.System.getUriFor(uriFor), false, this);
            }
            onChange(true);
        }

        public void unregister() {
            this.mResolver.unregisterContentObserver(this);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            boolean z2 = true;
            if (android.provider.Settings.System.getInt(this.mResolver, this.mKeySetting, 1) != 1) {
                z2 = false;
            }
            onSettingChanged(z2);
        }
    }

    void onSettingChanged(boolean z);

    void register(String str, String... strArr);

    void unregister();
}
