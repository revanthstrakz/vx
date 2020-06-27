package com.lody.virtual.server.p008am;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import com.lody.virtual.remote.PendingIntentData;
import java.util.HashMap;
import java.util.Map;

/* renamed from: com.lody.virtual.server.am.PendingIntents */
public final class PendingIntents {
    /* access modifiers changed from: private */
    public final Map<IBinder, PendingIntentData> mLruHistory = new HashMap();

    /* access modifiers changed from: 0000 */
    public final PendingIntentData getPendingIntent(IBinder iBinder) {
        PendingIntentData pendingIntentData;
        synchronized (this.mLruHistory) {
            pendingIntentData = (PendingIntentData) this.mLruHistory.get(iBinder);
        }
        return pendingIntentData;
    }

    /* access modifiers changed from: 0000 */
    public final void addPendingIntent(final IBinder iBinder, String str) {
        synchronized (this.mLruHistory) {
            try {
                iBinder.linkToDeath(new DeathRecipient() {
                    public void binderDied() {
                        iBinder.unlinkToDeath(this, 0);
                        PendingIntents.this.mLruHistory.remove(iBinder);
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            PendingIntentData pendingIntentData = (PendingIntentData) this.mLruHistory.get(iBinder);
            if (pendingIntentData == null) {
                this.mLruHistory.put(iBinder, new PendingIntentData(str, iBinder));
            } else {
                pendingIntentData.creator = str;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public final void removePendingIntent(IBinder iBinder) {
        synchronized (this.mLruHistory) {
            this.mLruHistory.remove(iBinder);
        }
    }
}
