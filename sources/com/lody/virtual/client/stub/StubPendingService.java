package com.lody.virtual.client.stub;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.lody.virtual.client.ipc.VActivityManager;

public class StubPendingService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            Intent intent2 = (Intent) intent.getParcelableExtra("_VA_|_intent_");
            int intExtra = intent.getIntExtra("_VA_|_user_id_", 0);
            if (intent2 != null) {
                VActivityManager.get().startService(null, intent2, null, intExtra);
            }
        }
        stopSelf();
        return 2;
    }
}
