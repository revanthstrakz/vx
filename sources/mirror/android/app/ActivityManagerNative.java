package mirror.android.app;

import android.os.IInterface;
import mirror.RefClass;
import mirror.RefStaticMethod;
import mirror.RefStaticObject;

public class ActivityManagerNative {
    public static Class<?> TYPE = RefClass.load(ActivityManagerNative.class, "android.app.ActivityManagerNative");
    public static RefStaticObject<Object> gDefault;
    public static RefStaticMethod<IInterface> getDefault;
}
