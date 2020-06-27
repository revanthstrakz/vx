package com.lody.virtual.client.stub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lody.virtual.helper.utils.ComponentUtils;

public class StubPendingReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = (Intent) intent.getParcelableExtra("_VA_|_intent_");
        int intExtra = intent.getIntExtra("_VA_|_user_id_", -1);
        if (intent2 != null) {
            Intent redirectBroadcastIntent = ComponentUtils.redirectBroadcastIntent(intent2, intExtra);
            if (redirectBroadcastIntent != null) {
                context.sendBroadcast(redirectBroadcastIntent);
            }
        }
    }
}
