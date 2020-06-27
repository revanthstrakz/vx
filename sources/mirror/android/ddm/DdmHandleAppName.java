package mirror.android.ddm;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

public class DdmHandleAppName {
    public static Class Class = RefClass.load(DdmHandleAppName.class, "android.ddm.DdmHandleAppName");
    @MethodParams({String.class})
    public static RefStaticMethod<Void> setAppName;
}
