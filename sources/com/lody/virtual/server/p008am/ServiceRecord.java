package com.lody.virtual.server.p008am;

import android.app.IServiceConnection;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.lody.virtual.server.am.ServiceRecord */
public class ServiceRecord extends Binder {
    public long activeSince;
    public final List<IntentBindRecord> bindings = new ArrayList();
    public int foregroundId;
    public Notification foregroundNoti;
    public long lastActivityTime;
    public ProcessRecord process;
    public ServiceInfo serviceInfo;
    public int startId;

    /* renamed from: com.lody.virtual.server.am.ServiceRecord$DeathRecipient */
    private static class DeathRecipient implements android.os.IBinder.DeathRecipient {
        private final IntentBindRecord bindRecord;
        private final IServiceConnection connection;

        private DeathRecipient(IntentBindRecord intentBindRecord, IServiceConnection iServiceConnection) {
            this.bindRecord = intentBindRecord;
            this.connection = iServiceConnection;
        }

        public void binderDied() {
            this.bindRecord.removeConnection(this.connection);
            this.connection.asBinder().unlinkToDeath(this, 0);
        }
    }

    /* renamed from: com.lody.virtual.server.am.ServiceRecord$IntentBindRecord */
    public static class IntentBindRecord {
        public IBinder binder;
        public final List<IServiceConnection> connections = Collections.synchronizedList(new ArrayList());
        public boolean doRebind = false;
        Intent intent;

        public boolean containConnection(IServiceConnection iServiceConnection) {
            for (IServiceConnection asBinder : this.connections) {
                if (asBinder.asBinder() == iServiceConnection.asBinder()) {
                    return true;
                }
            }
            return false;
        }

        public void addConnection(IServiceConnection iServiceConnection) {
            if (!containConnection(iServiceConnection)) {
                this.connections.add(iServiceConnection);
                try {
                    iServiceConnection.asBinder().linkToDeath(new DeathRecipient(this, iServiceConnection), 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void removeConnection(IServiceConnection iServiceConnection) {
            synchronized (this.connections) {
                Iterator it = this.connections.iterator();
                while (it.hasNext()) {
                    if (((IServiceConnection) it.next()).asBinder() == iServiceConnection.asBinder()) {
                        it.remove();
                    }
                }
            }
        }
    }

    public boolean containConnection(IServiceConnection iServiceConnection) {
        for (IntentBindRecord containConnection : this.bindings) {
            if (containConnection.containConnection(iServiceConnection)) {
                return true;
            }
        }
        return false;
    }

    public int getClientCount() {
        return this.bindings.size();
    }

    /* access modifiers changed from: 0000 */
    public int getConnectionCount() {
        int i;
        synchronized (this.bindings) {
            i = 0;
            for (IntentBindRecord intentBindRecord : this.bindings) {
                i += intentBindRecord.connections.size();
            }
        }
        return i;
    }

    /* access modifiers changed from: 0000 */
    public IntentBindRecord peekBinding(Intent intent) {
        synchronized (this.bindings) {
            for (IntentBindRecord intentBindRecord : this.bindings) {
                if (intentBindRecord.intent.filterEquals(intent)) {
                    return intentBindRecord;
                }
            }
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void addToBoundIntent(Intent intent, IServiceConnection iServiceConnection) {
        IntentBindRecord peekBinding = peekBinding(intent);
        if (peekBinding == null) {
            peekBinding = new IntentBindRecord();
            peekBinding.intent = intent;
            synchronized (this.bindings) {
                this.bindings.add(peekBinding);
            }
        }
        peekBinding.addConnection(iServiceConnection);
    }
}
