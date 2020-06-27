package mirror.android.content.res;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefMethod;

public class AssetManager {
    public static Class<?> TYPE = RefClass.load(AssetManager.class, android.content.res.AssetManager.class);
    @MethodParams({String.class})
    public static RefMethod<Integer> addAssetPath;
    public static RefConstructor<android.content.res.AssetManager> ctor;
}
