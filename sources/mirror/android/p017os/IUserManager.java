package mirror.android.p017os;

import android.os.IBinder;
import android.os.IInterface;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.android.os.IUserManager */
public class IUserManager {
    public static Class<?> TYPE = RefClass.load(IUserManager.class, "android.os.IUserManager");

    /* renamed from: mirror.android.os.IUserManager$Stub */
    public static class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "android.os.IUserManager$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
