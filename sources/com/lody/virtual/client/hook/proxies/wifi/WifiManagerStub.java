package com.lody.virtual.client.hook.proxies.wifi;

import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.WorkSource;
import android.support.p004v7.widget.helper.ItemTouchHelper.Callback;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import com.lody.virtual.client.stub.VASettings.Wifi;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.Reflect;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;
import mirror.android.net.wifi.IWifiManager.Stub;
import mirror.android.net.wifi.WifiSsid;

public class WifiManagerStub extends BinderInvocationProxy {

    private final class GetConnectionInfo extends MethodProxy {
        public String getMethodName() {
            return "getConnectionInfo";
        }

        private GetConnectionInfo() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            WifiInfo wifiInfo = (WifiInfo) method.invoke(obj, objArr);
            if (isFakeLocationEnable()) {
                mirror.android.net.wifi.WifiInfo.mBSSID.set(wifiInfo, "00:00:00:00:00:00");
                mirror.android.net.wifi.WifiInfo.mMacAddress.set(wifiInfo, "00:00:00:00:00:00");
            }
            if (Wifi.FAKE_WIFI_STATE) {
                return WifiManagerStub.createWifiInfo();
            }
            if (wifiInfo != null) {
                mirror.android.net.wifi.WifiInfo.mMacAddress.set(wifiInfo, getDeviceInfo().wifiMac);
            }
            return wifiInfo;
        }
    }

    private final class GetScanResults extends ReplaceCallingPkgMethodProxy {
        public GetScanResults() {
            super("getScanResults");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                new ArrayList(0);
            }
            return super.call(obj, method, objArr);
        }
    }

    public static class IPInfo {
        InetAddress addr;
        NetworkInterface intf;

        /* renamed from: ip */
        String f177ip;
        int ip_hex;
        int netmask_hex;
    }

    private class RemoveWorkSourceMethodProxy extends StaticMethodProxy {
        RemoveWorkSourceMethodProxy(String str) {
            super(str);
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            int indexOfFirst = ArrayUtils.indexOfFirst(objArr, WorkSource.class);
            if (indexOfFirst >= 0) {
                objArr[indexOfFirst] = null;
            }
            return super.call(obj, method, objArr);
        }
    }

    private static int netmask_to_hex(int i) {
        int i2 = 0;
        int i3 = 0;
        int i4 = 1;
        while (i2 < i) {
            i3 |= i4;
            i2++;
            i4 <<= 1;
        }
        return i3;
    }

    public WifiManagerStub() {
        super(Stub.asInterface, "wifi");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new MethodProxy() {
            public String getMethodName() {
                return "isWifiEnabled";
            }

            public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                if (Wifi.FAKE_WIFI_STATE) {
                    return Boolean.valueOf(true);
                }
                return super.call(obj, method, objArr);
            }
        });
        addMethodProxy((MethodProxy) new MethodProxy() {
            public String getMethodName() {
                return "getWifiEnabledState";
            }

            public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                if (Wifi.FAKE_WIFI_STATE) {
                    return Integer.valueOf(3);
                }
                return super.call(obj, method, objArr);
            }
        });
        addMethodProxy((MethodProxy) new MethodProxy() {
            public String getMethodName() {
                return "createDhcpInfo";
            }

            public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                if (Wifi.FAKE_WIFI_STATE) {
                    IPInfo access$000 = WifiManagerStub.getIPInfo();
                    if (access$000 != null) {
                        return WifiManagerStub.this.createDhcpInfo(access$000);
                    }
                }
                return super.call(obj, method, objArr);
            }
        });
        addMethodProxy((MethodProxy) new GetConnectionInfo());
        addMethodProxy((MethodProxy) new GetScanResults());
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getBatchedScanResults"));
        addMethodProxy((MethodProxy) new RemoveWorkSourceMethodProxy("acquireWifiLock"));
        addMethodProxy((MethodProxy) new RemoveWorkSourceMethodProxy("updateWifiLockWorkSource"));
        if (VERSION.SDK_INT > 21) {
            addMethodProxy((MethodProxy) new RemoveWorkSourceMethodProxy("startLocationRestrictedScan"));
        }
        if (VERSION.SDK_INT >= 19) {
            addMethodProxy((MethodProxy) new RemoveWorkSourceMethodProxy("startScan"));
            addMethodProxy((MethodProxy) new RemoveWorkSourceMethodProxy("requestBatchedScan"));
        }
    }

    private static ScanResult cloneScanResult(Parcelable parcelable) {
        Parcel obtain = Parcel.obtain();
        parcelable.writeToParcel(obtain, 0);
        obtain.setDataPosition(0);
        ScanResult scanResult = (ScanResult) Reflect.m80on((Object) parcelable).field("CREATOR").call("createFromParcel", obtain).get();
        obtain.recycle();
        return scanResult;
    }

    /* access modifiers changed from: private */
    public static IPInfo getIPInfo() {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                Iterator it = Collections.list(networkInterface.getInetAddresses()).iterator();
                while (true) {
                    if (it.hasNext()) {
                        InetAddress inetAddress = (InetAddress) it.next();
                        if (!inetAddress.isLoopbackAddress()) {
                            String upperCase = inetAddress.getHostAddress().toUpperCase();
                            if (isIPv4Address(upperCase)) {
                                IPInfo iPInfo = new IPInfo();
                                iPInfo.addr = inetAddress;
                                iPInfo.intf = networkInterface;
                                iPInfo.f177ip = upperCase;
                                iPInfo.ip_hex = InetAddress_to_hex(inetAddress);
                                iPInfo.netmask_hex = netmask_to_hex(((InterfaceAddress) networkInterface.getInterfaceAddresses().get(0)).getNetworkPrefixLength());
                                return iPInfo;
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isIPv4Address(String str) {
        return Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$").matcher(str).matches();
    }

    private static int InetAddress_to_hex(InetAddress inetAddress) {
        byte[] address = inetAddress.getAddress();
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            i |= (address[i2] & 255) << (i2 * 8);
        }
        return i;
    }

    /* access modifiers changed from: private */
    public DhcpInfo createDhcpInfo(IPInfo iPInfo) {
        DhcpInfo dhcpInfo = new DhcpInfo();
        dhcpInfo.ipAddress = iPInfo.ip_hex;
        dhcpInfo.netmask = iPInfo.netmask_hex;
        dhcpInfo.dns1 = 67372036;
        dhcpInfo.dns2 = 134744072;
        return dhcpInfo;
    }

    /* access modifiers changed from: private */
    public static WifiInfo createWifiInfo() throws Exception {
        WifiInfo wifiInfo = (WifiInfo) mirror.android.net.wifi.WifiInfo.ctor.newInstance();
        IPInfo iPInfo = getIPInfo();
        InetAddress inetAddress = iPInfo != null ? iPInfo.addr : null;
        mirror.android.net.wifi.WifiInfo.mNetworkId.set(wifiInfo, 1);
        mirror.android.net.wifi.WifiInfo.mSupplicantState.set(wifiInfo, SupplicantState.COMPLETED);
        mirror.android.net.wifi.WifiInfo.mBSSID.set(wifiInfo, Wifi.BSSID);
        mirror.android.net.wifi.WifiInfo.mMacAddress.set(wifiInfo, Wifi.MAC);
        mirror.android.net.wifi.WifiInfo.mIpAddress.set(wifiInfo, inetAddress);
        mirror.android.net.wifi.WifiInfo.mLinkSpeed.set(wifiInfo, 65);
        if (VERSION.SDK_INT >= 21) {
            mirror.android.net.wifi.WifiInfo.mFrequency.set(wifiInfo, 5000);
        }
        mirror.android.net.wifi.WifiInfo.mRssi.set(wifiInfo, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        if (mirror.android.net.wifi.WifiInfo.mWifiSsid != null) {
            mirror.android.net.wifi.WifiInfo.mWifiSsid.set(wifiInfo, WifiSsid.createFromAsciiEncoded.call(Wifi.SSID));
        } else {
            mirror.android.net.wifi.WifiInfo.mSSID.set(wifiInfo, Wifi.SSID);
        }
        return wifiInfo;
    }
}
