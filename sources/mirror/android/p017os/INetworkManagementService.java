package mirror.android.p017os;

import android.os.IBinder;
import android.os.IInterface;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.android.os.INetworkManagementService */
public class INetworkManagementService {
    public static Class<?> TYPE = RefClass.load(INetworkManagementService.class, "android.os.INetworkManagementService");

    /* renamed from: mirror.android.os.INetworkManagementService$Stub */
    public static class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "android.os.INetworkManagementService$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
