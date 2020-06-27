package com.android.launcher3.util;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri.Builder;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.launcher3.AppInfo;
import com.android.launcher3.C0622R;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.net.URISyntaxException;
import java.util.List;

public class PackageManagerHelper {
    private final Context mContext;
    private final LauncherAppsCompat mLauncherApps;
    private final PackageManager mPm;

    public PackageManagerHelper(Context context) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mLauncherApps = LauncherAppsCompat.getInstance(context);
    }

    public boolean isAppOnSdcard(String str, UserHandle userHandle) {
        ApplicationInfo applicationInfo = this.mLauncherApps.getApplicationInfo(str, 8192, userHandle);
        return (applicationInfo == null || (applicationInfo.flags & 262144) == 0) ? false : true;
    }

    public boolean isAppSuspended(String str, UserHandle userHandle) {
        ApplicationInfo applicationInfo = this.mLauncherApps.getApplicationInfo(str, 0, userHandle);
        if (applicationInfo == null || !isAppSuspended(applicationInfo)) {
            return false;
        }
        return true;
    }

    public boolean isSafeMode() {
        return this.mContext.getPackageManager().isSafeMode();
    }

    public Intent getAppLaunchIntent(String str, UserHandle userHandle) {
        List activityList = this.mLauncherApps.getActivityList(str, userHandle);
        if (activityList.isEmpty()) {
            return null;
        }
        return AppInfo.makeLaunchIntent((LauncherActivityInfo) activityList.get(0));
    }

    public static boolean isAppSuspended(ApplicationInfo applicationInfo) {
        boolean z = false;
        if (!Utilities.ATLEAST_NOUGAT) {
            return false;
        }
        if ((applicationInfo.flags & 1073741824) != 0) {
            z = true;
        }
        return z;
    }

    public boolean hasPermissionForActivity(Intent intent, String str) {
        boolean z = false;
        ResolveInfo resolveActivity = this.mPm.resolveActivity(intent, 0);
        if (resolveActivity == null) {
            return false;
        }
        if (TextUtils.isEmpty(resolveActivity.activityInfo.permission)) {
            return true;
        }
        if (TextUtils.isEmpty(str) || this.mPm.checkPermission(resolveActivity.activityInfo.permission, str) != 0) {
            return false;
        }
        if (!Utilities.ATLEAST_MARSHMALLOW || TextUtils.isEmpty(AppOpsManager.permissionToOp(resolveActivity.activityInfo.permission))) {
            return true;
        }
        try {
            if (this.mPm.getApplicationInfo(str, 0).targetSdkVersion >= 23) {
                z = true;
            }
            return z;
        } catch (NameNotFoundException unused) {
            return false;
        }
    }

    public static Intent getMarketIntent(String str) {
        return new Intent("android.intent.action.VIEW").setData(new Builder().scheme("market").authority("details").appendQueryParameter(CommonProperties.f192ID, str).build());
    }

    public static Intent getMarketSearchIntent(Context context, String str) {
        try {
            Intent parseUri = Intent.parseUri(context.getString(C0622R.string.market_search_intent), 0);
            if (!TextUtils.isEmpty(str)) {
                parseUri.setData(parseUri.getData().buildUpon().appendQueryParameter("q", str).build());
            }
            return parseUri;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
