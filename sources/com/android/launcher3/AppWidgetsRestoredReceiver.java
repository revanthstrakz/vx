package com.android.launcher3;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.model.LoaderTask;
import com.android.launcher3.provider.RestoreDbTask;
import com.android.launcher3.util.ContentWriter;
import com.android.launcher3.util.ContentWriter.CommitParams;

public class AppWidgetsRestoredReceiver extends BroadcastReceiver {
    private static final String TAG = "AWRestoredReceiver";

    public void onReceive(Context context, Intent intent) {
        if ("android.appwidget.action.APPWIDGET_HOST_RESTORED".equals(intent.getAction())) {
            int intExtra = intent.getIntExtra("hostId", 0);
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Widget ID map received for host:");
            sb.append(intExtra);
            Log.d(str, sb.toString());
            if (intExtra == 1024) {
                final int[] intArrayExtra = intent.getIntArrayExtra("appWidgetOldIds");
                final int[] intArrayExtra2 = intent.getIntArrayExtra("appWidgetIds");
                if (intArrayExtra.length == intArrayExtra2.length) {
                    final PendingResult goAsync = goAsync();
                    Handler handler = new Handler(LauncherModel.getWorkerLooper());
                    final Context context2 = context;
                    C05181 r1 = new Runnable() {
                        public void run() {
                            AppWidgetsRestoredReceiver.restoreAppWidgetIds(context2, intArrayExtra, intArrayExtra2);
                            goAsync.finish();
                        }
                    };
                    handler.postAtFrontOfQueue(r1);
                } else {
                    Log.e(TAG, "Invalid host restored received");
                }
            }
        }
    }

    @WorkerThread
    static void restoreAppWidgetIds(Context context, int[] iArr, int[] iArr2) {
        LauncherAppWidgetHost launcherAppWidgetHost = new LauncherAppWidgetHost(context);
        if (!RestoreDbTask.isPending(context)) {
            Log.e(TAG, "Skipping widget ID remap as DB already in use");
            for (int i : iArr2) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Deleting widgetId: ");
                sb.append(i);
                Log.d(str, sb.toString());
                launcherAppWidgetHost.deleteAppWidgetId(i);
            }
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        AppWidgetManager instance = AppWidgetManager.getInstance(context);
        for (int i2 = 0; i2 < iArr.length; i2++) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Widget state restore id ");
            sb2.append(iArr[i2]);
            sb2.append(" => ");
            sb2.append(iArr2[i2]);
            Log.i(str2, sb2.toString());
            String[] strArr = {Integer.toString(iArr[i2])};
            if (new ContentWriter(context, new CommitParams("appWidgetId=? and (restored & 1) = 1", strArr)).put(Favorites.APPWIDGET_ID, Integer.valueOf(iArr2[i2])).put(Favorites.RESTORED, Integer.valueOf(LoaderTask.isValidProvider(instance.getAppWidgetInfo(iArr2[i2])) ? 4 : 2)).commit() == 0) {
                Cursor query = contentResolver.query(Favorites.CONTENT_URI, new String[]{Favorites.APPWIDGET_ID}, "appWidgetId=?", strArr, null);
                try {
                    if (!query.moveToFirst()) {
                        launcherAppWidgetHost.deleteAppWidgetId(iArr2[i2]);
                    }
                } finally {
                    query.close();
                }
            }
        }
        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
        if (instanceNoCreate != null) {
            instanceNoCreate.getModel().forceReload();
        }
    }
}
