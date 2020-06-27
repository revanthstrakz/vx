package com.lody.virtual.client.hook.proxies.wifi_scanner;

import android.net.wifi.IWifiScanner.Stub;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Messenger;
import android.os.RemoteException;
import java.util.ArrayList;
import mirror.android.net.wifi.WifiScanner;

public class GhostWifiScannerImpl extends Stub {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public Messenger getMessenger() throws RemoteException {
        return new Messenger(this.mHandler);
    }

    public Bundle getAvailableChannels(int i) throws RemoteException {
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList((String) WifiScanner.GET_AVAILABLE_CHANNELS_EXTRA.get(), new ArrayList(0));
        return bundle;
    }
}
