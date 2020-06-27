package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.UserHandle;

@TargetApi(23)
public class UserManagerCompatVM extends UserManagerCompatVL {
    UserManagerCompatVM(Context context) {
        super(context);
    }

    public long getUserCreationTime(UserHandle userHandle) {
        return this.mUserManager.getUserCreationTime(userHandle);
    }
}
