package com.lody.virtual.p007os;

import android.os.Binder;
import com.lody.virtual.client.ipc.VActivityManager;

/* renamed from: com.lody.virtual.os.VBinder */
public class VBinder {
    public static int getCallingUid() {
        return VActivityManager.get().getUidByPid(Binder.getCallingPid());
    }

    public static int getBaseCallingUid() {
        return VUserHandle.getAppId(getCallingUid());
    }

    public static int getCallingPid() {
        return Binder.getCallingPid();
    }

    public static VUserHandle getCallingUserHandle() {
        return new VUserHandle(VUserHandle.getUserId(getCallingUid()));
    }
}
