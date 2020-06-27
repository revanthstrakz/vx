package com.lody.virtual.client.hook.proxies.backup;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ResultStaticMethodProxy;
import mirror.android.app.backup.IBackupManager.Stub;

public class BackupManagerStub extends BinderInvocationProxy {
    public BackupManagerStub() {
        super(Stub.asInterface, "backup");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("dataChanged", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("clearBackupData", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("agentConnected", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("agentDisconnected", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("restoreAtInstall", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("setBackupEnabled", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("setBackupProvisioned", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("backupNow", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("fullBackup", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("fullTransportBackup", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("fullRestore", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("acknowledgeFullBackupOrRestore", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getCurrentTransport", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("listAllTransports", new String[0]));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("selectBackupTransport", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("isBackupEnabled", Boolean.valueOf(false)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("setBackupPassword", Boolean.valueOf(true)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("hasBackupPassword", Boolean.valueOf(false)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("beginRestoreSession", null));
    }
}
