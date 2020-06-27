package com.allenliu.versionchecklib.core;

import android.content.Intent;
import android.os.IBinder;

public class MyService extends AVersionService {
    public void onResponses(AVersionService aVersionService, String str) {
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
