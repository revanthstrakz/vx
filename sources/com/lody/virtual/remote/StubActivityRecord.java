package com.lody.virtual.remote;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;

public class StubActivityRecord {
    public ComponentName caller;
    public ActivityInfo info;
    public Intent intent;
    public int userId;

    public StubActivityRecord(Intent intent2, ActivityInfo activityInfo, ComponentName componentName, int i) {
        this.intent = intent2;
        this.info = activityInfo;
        this.caller = componentName;
        this.userId = i;
    }

    public StubActivityRecord(Intent intent2) {
        this.intent = (Intent) intent2.getParcelableExtra("_VA_|_intent_");
        this.info = (ActivityInfo) intent2.getParcelableExtra("_VA_|_info_");
        this.caller = (ComponentName) intent2.getParcelableExtra("_VA_|_caller_");
        this.userId = intent2.getIntExtra("_VA_|_user_id_", 0);
    }

    public void saveToIntent(Intent intent2) {
        intent2.putExtra("_VA_|_intent_", this.intent);
        intent2.putExtra("_VA_|_info_", this.info);
        intent2.putExtra("_VA_|_caller_", this.caller);
        intent2.putExtra("_VA_|_user_id_", this.userId);
    }
}
