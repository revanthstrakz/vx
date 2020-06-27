package mirror.android.net.wifi;

import android.annotation.TargetApi;
import mirror.RefClass;
import mirror.RefStaticMethod;

@TargetApi(19)
public class WifiSsid {
    public static final Class<?> TYPE = RefClass.load(WifiSsid.class, "android.net.wifi.WifiSsid");
    public static RefStaticMethod<Object> createFromAsciiEncoded;
}
