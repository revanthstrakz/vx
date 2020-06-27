package mirror.android.net;

import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import mirror.MethodParams;
import mirror.RefBoolean;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefInt;
import mirror.RefObject;

public class NetworkInfo {
    public static Class<?> TYPE = RefClass.load(NetworkInfo.class, android.net.NetworkInfo.class);
    @MethodParams({int.class, int.class, String.class, String.class})
    public static RefConstructor<android.net.NetworkInfo> ctor;
    @MethodParams({int.class})
    public static RefConstructor<android.net.NetworkInfo> ctorOld;
    public static RefObject<DetailedState> mDetailedState;
    public static RefBoolean mIsAvailable;
    public static RefInt mNetworkType;
    public static RefObject<State> mState;
    public static RefObject<String> mTypeName;
}
