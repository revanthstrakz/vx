package mirror.android.ddm;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

public class DdmHandleAppNameJBMR1 {
    public static Class Class = RefClass.load(DdmHandleAppNameJBMR1.class, "android.ddm.DdmHandleAppName");
    @MethodParams({String.class, int.class})
    public static RefStaticMethod<Void> setAppName;
}
