package mirror.com.android.internal.p018os;

import android.os.IBinder;
import android.os.IInterface;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.com.android.internal.os.UserManager */
public class UserManager {
    public static Class<?> TYPE = RefClass.load(UserManager.class, "android.os.UserManager");

    /* renamed from: mirror.com.android.internal.os.UserManager$Stub */
    public static class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "android.os.IUserManager$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
