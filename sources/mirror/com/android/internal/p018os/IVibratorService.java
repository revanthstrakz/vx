package mirror.com.android.internal.p018os;

import android.os.IBinder;
import android.os.IInterface;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.com.android.internal.os.IVibratorService */
public class IVibratorService {
    public static Class<?> TYPE = RefClass.load(IVibratorService.class, "android.os.IVibratorService");

    /* renamed from: mirror.com.android.internal.os.IVibratorService$Stub */
    public static class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "android.os.IVibratorService$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
