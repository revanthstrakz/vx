package mirror.android.p017os.storage;

import android.os.IBinder;
import android.os.IInterface;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.android.os.storage.IStorageManager */
public class IStorageManager {
    public static Class<?> Class = RefClass.load(IStorageManager.class, "android.os.storage.IStorageManager");

    /* renamed from: mirror.android.os.storage.IStorageManager$Stub */
    public static class Stub {
        public static Class<?> Class = RefClass.load(Stub.class, "android.os.storage.IStorageManager$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
