package mirror.android.app;

import android.content.Intent;
import android.os.IBinder;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefMethod;

public class IActivityManagerL {
    public static Class<?> TYPE = RefClass.load(IActivityManagerL.class, "android.app.IActivityManager");
    @MethodParams({IBinder.class, int.class, Intent.class, boolean.class})
    public static RefMethod<Boolean> finishActivity;
}
