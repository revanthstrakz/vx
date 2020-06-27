package com.google.android.libraries.gsa.launcherclient;

import amirz.aidlbridge.Bridge;
import amirz.aidlbridge.BridgeCallback.Stub;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class LauncherClientBridge extends Stub implements ServiceConnection {
    private final BaseClientService mClientService;

    public LauncherClientBridge(BaseClientService baseClientService) {
        this.mClientService = baseClientService;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            Bridge.Stub.asInterface(iBinder).setCallback(this.mClientService instanceof LauncherClientService ? 1 : 0, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onBridgeConnected(IBinder iBinder) {
        this.mClientService.onServiceConnected(null, iBinder);
    }

    public void onBridgeDisconnected() {
        this.mClientService.onServiceDisconnected(null);
    }

    public void onServiceDisconnected(ComponentName componentName) {
        onBridgeDisconnected();
    }
}
