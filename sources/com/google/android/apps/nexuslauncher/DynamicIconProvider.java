package com.google.android.apps.nexuslauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.IconProvider;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.google.android.apps.nexuslauncher.clock.DynamicClock;
import java.util.Calendar;
import java.util.List;
import p013io.virtualapp.utils.HanziToPinyin.Token;

public class DynamicIconProvider extends IconProvider {
    public static final String GOOGLE_CALENDAR = "com.google.android.calendar";
    private final Context mContext;
    private final BroadcastReceiver mDateChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!Utilities.ATLEAST_NOUGAT) {
                int i = Calendar.getInstance().get(5);
                if (i != DynamicIconProvider.this.mDateOfMonth) {
                    DynamicIconProvider.this.mDateOfMonth = i;
                } else {
                    return;
                }
            }
            for (UserHandle userHandle : UserManagerCompat.getInstance(context).getUserProfiles()) {
                LauncherModel model = LauncherAppState.getInstance(context).getModel();
                model.onPackageChanged(DynamicIconProvider.GOOGLE_CALENDAR, userHandle);
                List queryForPinnedShortcuts = DeepShortcutManager.getInstance(context).queryForPinnedShortcuts(DynamicIconProvider.GOOGLE_CALENDAR, userHandle);
                if (!queryForPinnedShortcuts.isEmpty()) {
                    model.updatePinnedShortcuts(DynamicIconProvider.GOOGLE_CALENDAR, queryForPinnedShortcuts, userHandle);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int mDateOfMonth;
    private final PackageManager mPackageManager;

    public DynamicIconProvider(Context context) {
        this.mContext = context;
        IntentFilter intentFilter = new IntentFilter("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        if (!Utilities.ATLEAST_NOUGAT) {
            intentFilter.addAction("android.intent.action.TIME_TICK");
        }
        this.mContext.registerReceiver(this.mDateChangeReceiver, intentFilter, null, new Handler(LauncherModel.getWorkerLooper()));
        this.mPackageManager = this.mContext.getPackageManager();
    }

    private int getDayOfMonth() {
        return Calendar.getInstance().get(5) - 1;
    }

    private int getDayResId(Bundle bundle, Resources resources) {
        if (bundle != null) {
            int i = bundle.getInt("com.google.android.calendar.dynamic_icons_nexus_round", 0);
            if (i != 0) {
                try {
                    TypedArray obtainTypedArray = resources.obtainTypedArray(i);
                    int resourceId = obtainTypedArray.getResourceId(getDayOfMonth(), 0);
                    obtainTypedArray.recycle();
                    return resourceId;
                } catch (NotFoundException unused) {
                }
            }
        }
        return 0;
    }

    private boolean isCalendar(String str) {
        return GOOGLE_CALENDAR.equals(str);
    }

    public Drawable getIcon(LauncherActivityInfo launcherActivityInfo, int i, boolean z) {
        String str = launcherActivityInfo.getApplicationInfo().packageName;
        Drawable drawable = null;
        if (isCalendar(str)) {
            try {
                Bundle bundle = this.mPackageManager.getActivityInfo(launcherActivityInfo.getComponentName(), 8320).metaData;
                Resources resourcesForApplication = this.mPackageManager.getResourcesForApplication(str);
                int dayResId = getDayResId(bundle, resourcesForApplication);
                if (dayResId != 0) {
                    drawable = resourcesForApplication.getDrawableForDensity(dayResId, i);
                }
            } catch (NameNotFoundException unused) {
            }
        } else if (!z && Utilities.ATLEAST_OREO && DynamicClock.DESK_CLOCK.equals(launcherActivityInfo.getComponentName()) && Process.myUserHandle().equals(launcherActivityInfo.getUser())) {
            drawable = DynamicClock.getClock(this.mContext, i);
        }
        return drawable == null ? super.getIcon(launcherActivityInfo, i, z) : drawable;
    }

    public String getIconSystemState(String str) {
        if (!isCalendar(str)) {
            return this.mSystemState;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.mSystemState);
        sb.append(Token.SEPARATOR);
        sb.append(getDayOfMonth());
        return sb.toString();
    }
}
