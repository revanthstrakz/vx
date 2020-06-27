package com.android.launcher3.config;

abstract class BaseFlags {
    public static final boolean ADAPTIVE_ICON_SHADOW = true;
    public static final boolean DISCOVERY_ENABLED = false;
    public static final boolean GO_DISABLE_WIDGETS = false;
    public static final boolean IS_DOGFOOD_BUILD = false;
    public static final boolean LAUNCHER3_BACKPORT_SHORTCUTS = false;
    public static final boolean LAUNCHER3_DIRECT_SCROLL = true;
    public static boolean LAUNCHER3_DISABLE_ICON_NORMALIZATION = false;
    public static boolean LAUNCHER3_DISABLE_PINCH_TO_OVERVIEW = false;
    public static final boolean LAUNCHER3_GRADIENT_ALL_APPS = true;
    public static boolean LAUNCHER3_LEGACY_FOLDER_ICON = false;
    public static boolean LAUNCHER3_NEW_FOLDER_ANIMATION = true;
    public static final boolean LAUNCHER3_PHYSICS = true;
    public static final boolean LAUNCHER3_PROMISE_APPS_IN_ALL_APPS = false;
    public static final boolean LAUNCHER3_SPRING_ICONS = true;
    public static final boolean LAUNCHER3_UPDATE_SOFT_INPUT_MODE = false;
    public static final boolean LEGACY_ICON_TREATMENT = true;
    public static final boolean LIGHT_STATUS_BAR = false;
    public static final boolean NO_ALL_APPS_ICON = true;
    public static final boolean PULLDOWN_SEARCH = false;
    public static final boolean QSB_IN_HOTSEAT = true;
    public static final boolean QSB_ON_FIRST_SCREEN = true;

    BaseFlags() {
    }
}
