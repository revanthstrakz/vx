package com.android.launcher3.compat;

import android.os.UserHandle;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.p007os.VUserInfo;
import com.lody.virtual.p007os.VUserManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserManagerCompatVA extends UserManagerCompat {
    private VUserManager mUserManager = VUserManager.get();

    public void enableAndResetCache() {
    }

    public boolean isDemoUser() {
        return false;
    }

    public boolean isQuietModeEnabled(UserHandle userHandle) {
        return false;
    }

    public boolean isUserUnlocked(UserHandle userHandle) {
        return false;
    }

    public List<UserHandle> getUserProfiles() {
        List<VUserInfo> users = this.mUserManager.getUsers();
        ArrayList arrayList = new ArrayList();
        for (VUserInfo vUserInfo : users) {
            arrayList.add(fromUserId(vUserInfo.f180id));
        }
        return arrayList;
    }

    public long getSerialNumberForUser(UserHandle userHandle) {
        return this.mUserManager.getSerialNumberForUser(new VUserHandle(toUserId(userHandle)));
    }

    public UserHandle getUserForSerialNumber(long j) {
        return fromUserId(this.mUserManager.getUserForSerialNumber(j).getIdentifier());
    }

    public CharSequence getBadgedLabelForUser(CharSequence charSequence, UserHandle userHandle) {
        return String.format(Locale.getDefault(), "%s[%d]", new Object[]{charSequence, Integer.valueOf(toUserId(userHandle))});
    }

    public long getUserCreationTime(UserHandle userHandle) {
        return this.mUserManager.getUserInfo(toUserId(userHandle)).creationTime;
    }
}
