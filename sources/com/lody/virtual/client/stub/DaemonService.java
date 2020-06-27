package com.lody.virtual.client.stub;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.Constants;
import java.io.File;

public class DaemonService extends Service {
    private static final int NOTIFY_ID = 1001;
    static boolean showNotification = true;

    public static final class InnerService extends Service {
        public IBinder onBind(Intent intent) {
            return null;
        }

        public int onStartCommand(Intent intent, int i, int i2) {
            startForeground(1001, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, i, i2);
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    public static void startup(Context context) {
        File fileStreamPath = context.getFileStreamPath(Constants.NO_NOTIFICATION_FLAG);
        if (VERSION.SDK_INT >= 25 && fileStreamPath.exists()) {
            showNotification = false;
        }
        context.startService(new Intent(context, DaemonService.class));
        if (VirtualCore.get().isServerProcess()) {
            DaemonJobService.scheduleJob(context);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        startup(this);
    }

    public void onCreate() {
        super.onCreate();
        if (showNotification) {
            startService(new Intent(this, InnerService.class));
            startForeground(1001, new Notification());
        }
    }
}
