package mirror.android.p017os;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.android.os.Process */
public class Process {
    public static Class<?> TYPE = RefClass.load(Process.class, android.os.Process.class);
    @MethodParams({String.class})
    public static RefStaticMethod<Void> setArgV0;
}
