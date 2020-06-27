package com.lody.virtual.server.p008am;

import com.lody.virtual.server.p008am.ServiceRecord.IntentBindRecord;
import java.util.HashSet;

/* renamed from: com.lody.virtual.server.am.AppBindRecord */
final class AppBindRecord {
    final ProcessRecord client;
    final HashSet<ConnectionRecord> connections = new HashSet<>();
    final IntentBindRecord intent;
    final ServiceRecord service;

    AppBindRecord(ServiceRecord serviceRecord, IntentBindRecord intentBindRecord, ProcessRecord processRecord) {
        this.service = serviceRecord;
        this.intent = intentBindRecord;
        this.client = processRecord;
    }
}
