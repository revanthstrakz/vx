package com.lody.virtual.client.env;

import com.lody.virtual.client.stub.ShortcutHandleActivity;
import com.lody.virtual.helper.utils.EncodeUtils;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String ACTION_PACKAGE_ADDED = "virtual.android.intent.action.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_CHANGED = "virtual.android.intent.action.PACKAGE_CHANGED";
    public static final String ACTION_PACKAGE_REMOVED = "virtual.android.intent.action.PACKAGE_REMOVED";
    public static final String ACTION_USER_ADDED = "virtual.android.intent.action.USER_ADDED";
    public static final String ACTION_USER_INFO_CHANGED = "virtual.android.intent.action.USER_CHANGED";
    public static final String ACTION_USER_REMOVED = "virtual.android.intent.action.USER_REMOVED";
    public static final String ACTION_USER_STARTED = "Virtual.android.intent.action.USER_STARTED";
    public static final String EXTRA_USER_HANDLE = "android.intent.extra.user_handle";
    public static String FAKE_SIGNATURE_FLAG = ".fake_signature";
    public static final String FEATURE_FAKE_SIGNATURE = "fake-signature";
    public static String META_KEY_IDENTITY = "X-Identity";
    public static String META_VALUE_STUB = "Stub-User";
    public static String NO_NOTIFICATION_FLAG = ".no_notification";
    public static final String PASS_KEY_INTENT = "KEY_INTENT";
    public static final String PASS_KEY_USER = "KEY_USER";
    public static final String PASS_PKG_NAME_ARGUMENT = "MODEL_ARGUMENT";
    public static final List<String> PRIVILEGE_APP = Arrays.asList(new String[]{WECHAT_PACKAGE, EncodeUtils.decode("Y29tLnRlbmNlbnQubW9iaWxlcXE=")});
    public static String SERVER_PROCESS_NAME = ":x";
    public static String SHORTCUT_PROXY_ACTIVITY_NAME = ShortcutHandleActivity.class.getName();
    public static final String WECHAT_PACKAGE = EncodeUtils.decode("Y29tLnRlbmNlbnQubW0=");
}
