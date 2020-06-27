package com.lody.virtual.client.stub;

import android.app.Activity;
import android.os.Bundle;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.remote.StubActivityRecord;

public class StubPendingActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        finish();
        StubActivityRecord stubActivityRecord = new StubActivityRecord(getIntent());
        if (stubActivityRecord.intent != null) {
            stubActivityRecord.intent.addFlags(33554432);
            VActivityManager.get().startActivity(stubActivityRecord.intent, stubActivityRecord.userId);
        }
    }
}
