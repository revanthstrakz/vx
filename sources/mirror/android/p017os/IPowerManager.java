package mirror.android.p017os;

import android.os.IBinder;
import android.os.IInterface;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.android.os.IPowerManager */
public class IPowerManager {
    public static Class<?> TYPE = RefClass.load(IPowerManager.class, "android.os.IPowerManager");

    /* renamed from: mirror.android.os.IPowerManager$Stub */
    public static class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "android.os.IPowerManager$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
