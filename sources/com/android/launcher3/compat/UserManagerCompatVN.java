package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.UserHandle;

@TargetApi(24)
public class UserManagerCompatVN extends UserManagerCompatVM {
    UserManagerCompatVN(Context context) {
        super(context);
    }

    public boolean isQuietModeEnabled(UserHandle userHandle) {
        try {
            return this.mUserManager.isQuietModeEnabled(userHandle);
        } catch (SecurityException unused) {
            return false;
        }
    }

    public boolean isUserUnlocked(UserHandle userHandle) {
        try {
            return this.mUserManager.isUserUnlocked(userHandle);
        } catch (SecurityException unused) {
            return false;
        }
    }
}
