package com.android.launcher3.allapps;

import android.content.Context;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.AppInfo;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.util.LabelComparator;
import java.util.Comparator;

public class AppInfoComparator implements Comparator<AppInfo> {
    private final LabelComparator mLabelComparator = new LabelComparator();
    private final UserHandle mMyUser = Process.myUserHandle();
    private final UserManagerCompat mUserManager;

    public AppInfoComparator(Context context) {
        this.mUserManager = UserManagerCompat.getInstance(context);
    }

    public int compare(AppInfo appInfo, AppInfo appInfo2) {
        int compare = this.mLabelComparator.compare(appInfo.title.toString(), appInfo2.title.toString());
        if (compare != 0) {
            return compare;
        }
        int compareTo = appInfo.componentName.compareTo(appInfo2.componentName);
        if (compareTo != 0) {
            return compareTo;
        }
        if (this.mMyUser.equals(appInfo.user)) {
            return -1;
        }
        return Long.valueOf(this.mUserManager.getSerialNumberForUser(appInfo.user)).compareTo(Long.valueOf(this.mUserManager.getSerialNumberForUser(appInfo2.user)));
    }
}
