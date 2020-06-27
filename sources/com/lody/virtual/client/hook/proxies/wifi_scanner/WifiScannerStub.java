package com.lody.virtual.client.hook.proxies.wifi_scanner;

import android.os.IInterface;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;

public class WifiScannerStub extends BinderInvocationProxy {
    public WifiScannerStub() {
        super((IInterface) new GhostWifiScannerImpl(), "wifiscanner");
    }
}
