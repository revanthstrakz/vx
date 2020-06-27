package com.lody.virtual.helper.compat;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import mirror.android.app.ActivityManagerNative;
import mirror.android.app.IActivityManagerICS;
import mirror.android.app.IActivityManagerL;
import mirror.android.app.IActivityManagerN;

public class ActivityManagerCompat {
    public static final int INTENT_SENDER_ACTIVITY = 2;
    public static final int INTENT_SENDER_ACTIVITY_RESULT = 3;
    public static final int INTENT_SENDER_BROADCAST = 1;
    public static final int INTENT_SENDER_SERVICE = 4;
    public static final int SERVICE_DONE_EXECUTING_ANON = 0;
    public static final int SERVICE_DONE_EXECUTING_START = 1;
    public static final int SERVICE_DONE_EXECUTING_STOP = 2;
    public static final int START_INTENT_NOT_RESOLVED = -1;
    public static final int START_NOT_CURRENT_USER_ACTIVITY = -8;
    public static final int START_TASK_TO_FRONT = 2;
    public static final int USER_OP_SUCCESS = 0;

    public static boolean finishActivity(IBinder iBinder, int i, Intent intent) {
        if (VERSION.SDK_INT >= 24) {
            return ((Boolean) IActivityManagerN.finishActivity.call(ActivityManagerNative.getDefault.call(new Object[0]), iBinder, Integer.valueOf(i), intent, Integer.valueOf(0))).booleanValue();
        } else if (VERSION.SDK_INT >= 21) {
            return ((Boolean) IActivityManagerL.finishActivity.call(ActivityManagerNative.getDefault.call(new Object[0]), iBinder, Integer.valueOf(i), intent, Boolean.valueOf(false))).booleanValue();
        } else {
            IActivityManagerICS.finishActivity.call(ActivityManagerNative.getDefault.call(new Object[0]), iBinder, Integer.valueOf(i), intent);
            return false;
        }
    }
}
