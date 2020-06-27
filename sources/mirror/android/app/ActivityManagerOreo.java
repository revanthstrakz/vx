package mirror.android.app;

import android.os.IInterface;
import mirror.RefClass;
import mirror.RefStaticMethod;
import mirror.RefStaticObject;

public class ActivityManagerOreo {
    public static RefStaticObject<Object> IActivityManagerSingleton;
    public static Class<?> TYPE = RefClass.load(ActivityManagerOreo.class, "android.app.ActivityManager");
    public static RefStaticMethod<IInterface> getService;
}
