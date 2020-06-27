package com.lody.virtual.server.p008am;

import android.app.IServiceConnection;

/* renamed from: com.lody.virtual.server.am.ConnectionRecord */
final class ConnectionRecord {
    final AppBindRecord binding;
    final IServiceConnection conn;
    final int flags;
    boolean serviceDead;

    ConnectionRecord(AppBindRecord appBindRecord, IServiceConnection iServiceConnection, int i) {
        this.binding = appBindRecord;
        this.conn = iServiceConnection;
        this.flags = i;
    }
}
