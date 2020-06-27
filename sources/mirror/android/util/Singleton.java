package mirror.android.util;

import mirror.RefClass;
import mirror.RefMethod;
import mirror.RefObject;

public class Singleton {
    public static Class<?> TYPE = RefClass.load(Singleton.class, "android.util.Singleton");
    public static RefMethod<Object> get;
    public static RefObject<Object> mInstance;
}
