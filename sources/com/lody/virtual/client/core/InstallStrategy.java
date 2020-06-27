package com.lody.virtual.client.core;

public interface InstallStrategy {
    public static final int COMPARE_VERSION = 8;
    public static final int DEPEND_SYSTEM_IF_EXIST = 32;
    public static final int IGNORE_NEW_VERSION = 16;
    public static final int SKIP_DEX_OPT = 64;
    public static final int TERMINATE_IF_EXIST = 2;
    public static final int UPDATE_IF_EXIST = 4;
}
