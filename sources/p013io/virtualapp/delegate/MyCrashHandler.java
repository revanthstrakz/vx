package p013io.virtualapp.delegate;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.InstalledAppInfo;
import com.microsoft.appcenter.crashes.Crashes;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/* renamed from: io.virtualapp.delegate.MyCrashHandler */
public class MyCrashHandler extends BaseCrashHandler {
    private static final String CRASH_SP = "vxp_crash";
    private static final String KEY_LAST_CRASH_TIME = "last_crash_time";
    private static final String KEY_LAST_CRASH_TYPE = "last_crash_type";

    public void handleUncaughtException(Thread thread, Throwable th) {
        SharedPreferences sharedPreferences = VirtualCore.get().getContext().getSharedPreferences(CRASH_SP, 4);
        HashMap hashMap = new HashMap();
        try {
            ApplicationInfo currentApplicationInfo = VClientImpl.get().getCurrentApplicationInfo();
            if (currentApplicationInfo != null) {
                String str = currentApplicationInfo.packageName;
                hashMap.put("process", currentApplicationInfo.processName);
                hashMap.put(ServiceManagerNative.PACKAGE, str);
                int myUserId = VUserHandle.myUserId();
                hashMap.put("uid", String.valueOf(myUserId));
                InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(str, 0);
                if (installedAppInfo != null) {
                    PackageInfo packageInfo = installedAppInfo.getPackageInfo(myUserId);
                    if (packageInfo != null) {
                        String str2 = packageInfo.versionName;
                        int i = packageInfo.versionCode;
                        hashMap.put("versionName", str2);
                        hashMap.put("versionCode", String.valueOf(i));
                    }
                }
            }
        } catch (Throwable unused) {
        }
        String name = th.getClass().getName();
        long currentTimeMillis = System.currentTimeMillis();
        long j = sharedPreferences.getLong(KEY_LAST_CRASH_TIME, 0);
        if (!name.equals(sharedPreferences.getString(KEY_LAST_CRASH_TYPE, null)) || currentTimeMillis - j >= TimeUnit.MINUTES.toMillis(1)) {
            Crashes.trackError(th, hashMap, null);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("uncaught :");
        sb.append(thread);
        Log.i("XApp", sb.toString(), th);
        sharedPreferences.edit().putLong(KEY_LAST_CRASH_TIME, currentTimeMillis).putString(KEY_LAST_CRASH_TYPE, name).commit();
        super.handleUncaughtException(thread, th);
    }
}
