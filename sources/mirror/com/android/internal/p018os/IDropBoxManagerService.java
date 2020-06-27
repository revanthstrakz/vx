package mirror.com.android.internal.p018os;

import android.os.IBinder;
import android.os.IInterface;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.com.android.internal.os.IDropBoxManagerService */
public class IDropBoxManagerService {
    public static Class<?> TYPE = RefClass.load(IDropBoxManagerService.class, "com.android.internal.os.IDropBoxManagerService");

    /* renamed from: mirror.com.android.internal.os.IDropBoxManagerService$Stub */
    public static class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "com.android.internal.os.IDropBoxManagerService$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
