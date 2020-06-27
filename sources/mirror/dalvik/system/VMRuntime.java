package mirror.dalvik.system;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefMethod;
import mirror.RefStaticMethod;

public class VMRuntime {
    public static Class<?> TYPE = RefClass.load(VMRuntime.class, "dalvik.system.VMRuntime");
    public static RefStaticMethod<String> getCurrentInstructionSet;
    public static RefStaticMethod<Object> getRuntime;
    public static RefMethod<Boolean> is64Bit;
    @MethodParams({int.class})
    public static RefMethod<Void> setTargetSdkVersion;
}
