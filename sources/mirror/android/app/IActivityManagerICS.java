package mirror.android.app;

import android.content.Intent;
import android.os.IBinder;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefMethod;

public class IActivityManagerICS {
    public static Class<?> TYPE = RefClass.load(IActivityManagerICS.class, "android.app.IActivityManager");
    @MethodParams({IBinder.class, int.class, Intent.class})
    public static RefMethod<Boolean> finishActivity;
}
