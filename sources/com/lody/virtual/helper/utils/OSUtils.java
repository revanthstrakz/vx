package com.lody.virtual.helper.utils;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class OSUtils {
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final OSUtils sOSUtils = new OSUtils();
    private boolean emui;
    private boolean flyme;
    private boolean miui;
    private String miuiVersion;

    private OSUtils() {
        Properties properties;
        try {
            properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (IOException unused) {
            properties = null;
        }
        if (properties != null) {
            boolean z = true;
            this.emui = !TextUtils.isEmpty(properties.getProperty(KEY_EMUI_VERSION_CODE));
            this.miuiVersion = properties.getProperty(KEY_MIUI_VERSION_CODE);
            if (TextUtils.isEmpty(this.miuiVersion) && TextUtils.isEmpty(properties.getProperty(KEY_MIUI_VERSION_NAME)) && TextUtils.isEmpty(properties.getProperty(KEY_MIUI_INTERNAL_STORAGE))) {
                z = false;
            }
            this.miui = z;
        }
        this.flyme = hasFlyme();
    }

    public static OSUtils getInstance() {
        return sOSUtils;
    }

    public String getMiuiVersion() {
        return this.miuiVersion;
    }

    public boolean isEmui() {
        return this.emui;
    }

    public boolean isMiui() {
        return this.miui;
    }

    public boolean isFlyme() {
        return this.flyme;
    }

    private boolean hasFlyme() {
        boolean z = false;
        try {
            if (Build.class.getMethod("hasSmartBar", new Class[0]) != null) {
                z = true;
            }
            return z;
        } catch (Exception unused) {
            return false;
        }
    }
}
