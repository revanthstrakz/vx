package com.android.launcher3;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInstaller.SessionInfo;
import android.os.AsyncTask;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.launcher3.compat.LauncherAppsCompat;
import java.util.List;

@TargetApi(26)
public class SessionCommitReceiver extends BroadcastReceiver {
    public static final String ADD_ICON_PREFERENCE_INITIALIZED_KEY = "pref_add_icon_to_home_initialized";
    public static final String ADD_ICON_PREFERENCE_KEY = "pref_add_icon_to_home";
    private static final String MARKER_PROVIDER_PREFIX = ".addtohomescreen";
    private static final String TAG = "SessionCommitReceiver";

    private static class PrefInitTask extends AsyncTask<Void, Void, Void> {
        private final Context mContext;

        PrefInitTask(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voidArr) {
            Utilities.getPrefs(this.mContext).edit().putBoolean(SessionCommitReceiver.ADD_ICON_PREFERENCE_KEY, readValueFromMarketApp()).putBoolean(SessionCommitReceiver.ADD_ICON_PREFERENCE_INITIALIZED_KEY, true).apply();
            return null;
        }

        /* JADX WARNING: Removed duplicated region for block: B:27:0x007e  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0084  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean readValueFromMarketApp() {
            /*
                r11 = this;
                android.content.Context r0 = r11.mContext
                android.content.pm.PackageManager r0 = r0.getPackageManager()
                android.content.Intent r1 = new android.content.Intent
                java.lang.String r2 = "android.intent.action.MAIN"
                r1.<init>(r2)
                java.lang.String r2 = "android.intent.category.APP_MARKET"
                android.content.Intent r1 = r1.addCategory(r2)
                r2 = 1114112(0x110000, float:1.561203E-39)
                android.content.pm.ResolveInfo r0 = r0.resolveActivity(r1, r2)
                r1 = 1
                if (r0 != 0) goto L_0x001d
                return r1
            L_0x001d:
                r2 = 0
                android.content.Context r3 = r11.mContext     // Catch:{ Exception -> 0x0074 }
                android.content.ContentResolver r4 = r3.getContentResolver()     // Catch:{ Exception -> 0x0074 }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0074 }
                r3.<init>()     // Catch:{ Exception -> 0x0074 }
                java.lang.String r5 = "content://"
                r3.append(r5)     // Catch:{ Exception -> 0x0074 }
                android.content.pm.ActivityInfo r0 = r0.activityInfo     // Catch:{ Exception -> 0x0074 }
                java.lang.String r0 = r0.packageName     // Catch:{ Exception -> 0x0074 }
                r3.append(r0)     // Catch:{ Exception -> 0x0074 }
                java.lang.String r0 = ".addtohomescreen"
                r3.append(r0)     // Catch:{ Exception -> 0x0074 }
                java.lang.String r0 = r3.toString()     // Catch:{ Exception -> 0x0074 }
                android.net.Uri r5 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x0074 }
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                android.database.Cursor r0 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0074 }
                boolean r2 = r0.moveToNext()     // Catch:{ Exception -> 0x006d, all -> 0x006a }
                if (r2 == 0) goto L_0x0064
                java.lang.String r2 = "value"
                int r2 = r0.getColumnIndexOrThrow(r2)     // Catch:{ Exception -> 0x006d, all -> 0x006a }
                int r2 = r0.getInt(r2)     // Catch:{ Exception -> 0x006d, all -> 0x006a }
                if (r2 == 0) goto L_0x005d
                goto L_0x005e
            L_0x005d:
                r1 = 0
            L_0x005e:
                if (r0 == 0) goto L_0x0063
                r0.close()
            L_0x0063:
                return r1
            L_0x0064:
                if (r0 == 0) goto L_0x0081
                r0.close()
                goto L_0x0081
            L_0x006a:
                r1 = move-exception
                r2 = r0
                goto L_0x0082
            L_0x006d:
                r2 = move-exception
                r10 = r2
                r2 = r0
                r0 = r10
                goto L_0x0075
            L_0x0072:
                r1 = move-exception
                goto L_0x0082
            L_0x0074:
                r0 = move-exception
            L_0x0075:
                java.lang.String r3 = "SessionCommitReceiver"
                java.lang.String r4 = "Error reading add to homescreen preference"
                android.util.Log.d(r3, r4, r0)     // Catch:{ all -> 0x0072 }
                if (r2 == 0) goto L_0x0081
                r2.close()
            L_0x0081:
                return r1
            L_0x0082:
                if (r2 == 0) goto L_0x0087
                r2.close()
            L_0x0087:
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.SessionCommitReceiver.PrefInitTask.readValueFromMarketApp():boolean");
        }
    }

    public void onReceive(Context context, Intent intent) {
        if (isEnabled(context) && Utilities.ATLEAST_OREO) {
            SessionInfo sessionInfo = (SessionInfo) intent.getParcelableExtra("android.content.pm.extra.SESSION");
            UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
            if (!Process.myUserHandle().equals(userHandle) || (!TextUtils.isEmpty(sessionInfo.getAppPackageName()) && sessionInfo.getInstallReason() == 4)) {
                queueAppIconAddition(context, sessionInfo.getAppPackageName(), userHandle);
            }
        }
    }

    public static void queueAppIconAddition(Context context, String str, UserHandle userHandle) {
        List activityList = LauncherAppsCompat.getInstance(context).getActivityList(str, userHandle);
        if (activityList != null && !activityList.isEmpty()) {
            InstallShortcutReceiver.queueActivityInfo((LauncherActivityInfo) activityList.get(0), context);
        }
    }

    public static boolean isEnabled(Context context) {
        return Utilities.getPrefs(context).getBoolean(ADD_ICON_PREFERENCE_KEY, true);
    }

    public static void applyDefaultUserPrefs(Context context) {
        if (Utilities.ATLEAST_OREO) {
            SharedPreferences prefs = Utilities.getPrefs(context);
            if (prefs.getAll().isEmpty()) {
                prefs.edit().putBoolean(ADD_ICON_PREFERENCE_KEY, true).apply();
            } else if (!prefs.contains(ADD_ICON_PREFERENCE_INITIALIZED_KEY)) {
                new PrefInitTask(context).executeOnExecutor(Utilities.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }
    }
}
