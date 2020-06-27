package mirror.android.content.p016pm;

import mirror.MethodReflectParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.android.content.pm.PackageParserNougat */
public class PackageParserNougat {
    public static Class<?> TYPE = RefClass.load(PackageParserNougat.class, "android.content.pm.PackageParser");
    @MethodReflectParams({"android.content.pm.PackageParser$Package", "int"})
    public static RefStaticMethod<Void> collectCertificates;
}
