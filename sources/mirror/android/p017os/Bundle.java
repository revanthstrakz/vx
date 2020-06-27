package mirror.android.p017os;

import android.os.IBinder;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefMethod;

/* renamed from: mirror.android.os.Bundle */
public class Bundle {
    public static Class<?> TYPE = RefClass.load(Bundle.class, android.os.Bundle.class);
    @MethodParams({String.class})
    public static RefMethod<IBinder> getIBinder;
    @MethodParams({String.class, IBinder.class})
    public static RefMethod<Void> putIBinder;
}
