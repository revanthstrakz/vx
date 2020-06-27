package mirror.android.p017os;

import android.annotation.TargetApi;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefMethod;

@TargetApi(17)
/* renamed from: mirror.android.os.UserHandle */
public class UserHandle {
    public static Class<?> TYPE = RefClass.load(UserHandle.class, android.os.UserHandle.class);
    @MethodParams({int.class})
    public static RefConstructor<android.os.UserHandle> ctor;
    public static RefMethod<Integer> getIdentifier;
}
