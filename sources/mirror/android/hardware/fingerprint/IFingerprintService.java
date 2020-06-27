package mirror.android.hardware.fingerprint;

import android.os.IBinder;
import android.os.IInterface;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

public class IFingerprintService {
    public static Class<?> TYPE = RefClass.load(IFingerprintService.class, "android.hardware.fingerprint.IFingerprintService");

    public static class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "android.hardware.fingerprint.IFingerprintService$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
