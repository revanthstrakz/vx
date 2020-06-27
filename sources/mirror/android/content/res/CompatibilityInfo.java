package mirror.android.content.res;

import android.content.pm.ApplicationInfo;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefStaticObject;

public class CompatibilityInfo {
    public static RefStaticObject<Object> DEFAULT_COMPATIBILITY_INFO;
    public static Class<?> TYPE = RefClass.load(CompatibilityInfo.class, "android.content.res.CompatibilityInfo");
    @MethodParams({ApplicationInfo.class, int.class, int.class, boolean.class})
    public static RefConstructor ctor;
}
