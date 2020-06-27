package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;

public class AppFilter {
    public boolean shouldShowApp(ComponentName componentName, UserHandle userHandle) {
        return true;
    }

    public static AppFilter newInstance(Context context) {
        return (AppFilter) Utilities.getOverrideObject(AppFilter.class, context, C0622R.string.app_filter_class);
    }
}
