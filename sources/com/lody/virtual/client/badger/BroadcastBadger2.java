package com.lody.virtual.client.badger;

import android.content.ComponentName;
import android.content.Intent;
import com.lody.virtual.remote.BadgerInfo;

public abstract class BroadcastBadger2 implements IBadger {

    static class NewHtcHomeBadger1 extends BroadcastBadger2 {
        public String getAction() {
            return "com.htc.launcher.action.SET_NOTIFICATION";
        }

        public String getComponentKey() {
            return "com.htc.launcher.extra.COMPONENT";
        }

        public String getCountKey() {
            return "com.htc.launcher.extra.COUNT";
        }

        NewHtcHomeBadger1() {
        }
    }

    public abstract String getAction();

    public abstract String getComponentKey();

    public abstract String getCountKey();

    public BadgerInfo handleBadger(Intent intent) {
        BadgerInfo badgerInfo = new BadgerInfo();
        ComponentName unflattenFromString = ComponentName.unflattenFromString(intent.getStringExtra(getComponentKey()));
        if (unflattenFromString == null) {
            return null;
        }
        badgerInfo.packageName = unflattenFromString.getPackageName();
        badgerInfo.className = unflattenFromString.getClassName();
        badgerInfo.badgerCount = intent.getIntExtra(getCountKey(), 0);
        return badgerInfo;
    }
}
