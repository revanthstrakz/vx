package com.allenliu.versionchecklib.core;

import android.content.Context;
import android.content.Intent;
import com.allenliu.versionchecklib.core.http.AllenHttp;

public class AllenChecker {
    private static Context globalContexst = null;
    private static boolean isDebug = true;
    private static VersionParams params;

    public static void startVersionCheck(Context context, VersionParams versionParams) {
        globalContexst = context;
        params = versionParams;
        Intent intent = new Intent(context, versionParams.getService());
        intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
        context.stopService(intent);
        context.startService(intent);
    }

    public static void init(boolean z) {
        isDebug = z;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void cancelMission() {
        AllenHttp.getHttpClient().dispatcher().cancelAll();
        if (!(globalContexst == null || params == null)) {
            globalContexst.stopService(new Intent(globalContexst, params.getService()));
        }
        if (VersionDialogActivity.instance != null) {
            VersionDialogActivity.instance.finish();
        }
        globalContexst = null;
        params = null;
    }
}
