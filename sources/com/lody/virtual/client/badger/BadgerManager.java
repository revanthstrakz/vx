package com.lody.virtual.client.badger;

import android.content.Intent;
import com.lody.virtual.client.ipc.VActivityManager;
import java.util.HashMap;
import java.util.Map;

public class BadgerManager {
    private static final Map<String, IBadger> BADGERS = new HashMap(10);

    static {
        addBadger(new AdwHomeBadger());
        addBadger(new AospHomeBadger());
        addBadger(new LGHomeBadger());
        addBadger(new NewHtcHomeBadger2());
        addBadger(new OPPOHomeBader());
        addBadger(new NewHtcHomeBadger1());
    }

    private static void addBadger(IBadger iBadger) {
        BADGERS.put(iBadger.getAction(), iBadger);
    }

    public static boolean handleBadger(Intent intent) {
        IBadger iBadger = (IBadger) BADGERS.get(intent.getAction());
        if (iBadger == null) {
            return false;
        }
        VActivityManager.get().notifyBadgerChange(iBadger.handleBadger(intent));
        return true;
    }
}
