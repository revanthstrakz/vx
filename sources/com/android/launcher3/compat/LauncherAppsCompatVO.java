package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps.PinItemRequest;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Parcelable;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.ShortcutConfigActivityInfo.ShortcutConfigActivityInfoVO;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.List;

@TargetApi(26)
public class LauncherAppsCompatVO extends LauncherAppsCompatVL {
    LauncherAppsCompatVO(Context context) {
        super(context);
    }

    public ApplicationInfo getApplicationInfo(String str, int i, UserHandle userHandle) {
        try {
            ApplicationInfo applicationInfo = this.mLauncherApps.getApplicationInfo(str, i, userHandle);
            if ((applicationInfo.flags & 8388608) == 0 || !applicationInfo.enabled) {
                applicationInfo = null;
            }
            return applicationInfo;
        } catch (NameNotFoundException unused) {
            return null;
        }
    }

    public List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(@Nullable PackageUserKey packageUserKey) {
        List<UserHandle> list;
        String str;
        ArrayList arrayList = new ArrayList();
        UserHandle myUserHandle = Process.myUserHandle();
        if (packageUserKey == null) {
            list = UserManagerCompat.getInstance(this.mContext).getUserProfiles();
            str = null;
        } else {
            ArrayList arrayList2 = new ArrayList(1);
            arrayList2.add(packageUserKey.mUser);
            List list2 = arrayList2;
            str = packageUserKey.mPackageName;
            list = list2;
        }
        for (UserHandle userHandle : list) {
            boolean equals = myUserHandle.equals(userHandle);
            for (LauncherActivityInfo launcherActivityInfo : this.mLauncherApps.getShortcutConfigActivityList(str, userHandle)) {
                if (equals || launcherActivityInfo.getApplicationInfo().targetSdkVersion >= 26) {
                    arrayList.add(new ShortcutConfigActivityInfoVO(launcherActivityInfo));
                }
            }
        }
        return arrayList;
    }

    @Nullable
    public static ShortcutInfo createShortcutInfoFromPinItemRequest(Context context, final PinItemRequest pinItemRequest, final long j) {
        if (pinItemRequest == null || pinItemRequest.getRequestType() != 1 || !pinItemRequest.isValid()) {
            return null;
        }
        if (j > 0) {
            new LooperExecutor(LauncherModel.getWorkerLooper()).execute(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(j);
                    } catch (InterruptedException unused) {
                    }
                    if (pinItemRequest.isValid()) {
                        pinItemRequest.accept();
                    }
                }
            });
        } else if (!pinItemRequest.accept()) {
            return null;
        }
        ShortcutInfoCompat shortcutInfoCompat = new ShortcutInfoCompat(pinItemRequest.getShortcutInfo());
        ShortcutInfo shortcutInfo = new ShortcutInfo(shortcutInfoCompat, context);
        shortcutInfo.iconBitmap = LauncherIcons.createShortcutIcon(shortcutInfoCompat, context, false);
        LauncherAppState.getInstance(context).getModel().updateAndBindShortcutInfo(shortcutInfo, shortcutInfoCompat);
        return shortcutInfo;
    }

    public static PinItemRequest getPinItemRequest(Intent intent) {
        Parcelable parcelableExtra = intent.getParcelableExtra("android.content.pm.extra.PIN_ITEM_REQUEST");
        if (parcelableExtra instanceof PinItemRequest) {
            return (PinItemRequest) parcelableExtra;
        }
        return null;
    }
}
