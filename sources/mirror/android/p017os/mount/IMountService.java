package mirror.android.p017os.mount;

import android.os.IBinder;
import android.os.IInterface;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.android.os.mount.IMountService */
public class IMountService {
    public static Class<?> TYPE = RefClass.load(IMountService.class, "android.os.storage.IMountService");

    /* renamed from: mirror.android.os.mount.IMountService$Stub */
    public static class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "android.os.storage.IMountService$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
