package mirror.android.content.p016pm;

import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.List;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefMethod;
import mirror.RefStaticObject;

/* renamed from: mirror.android.content.pm.ParceledListSlice */
public class ParceledListSlice {
    public static RefStaticObject<Creator> CREATOR;
    public static Class<?> TYPE = RefClass.load(ParceledListSlice.class, "android.content.pm.ParceledListSlice");
    public static RefMethod<Boolean> append;
    public static RefConstructor<Parcelable> ctor;
    public static RefMethod<List<?>> getList;
    public static RefMethod<Boolean> isLastSlice;
    public static RefMethod<Parcelable> populateList;
    public static RefMethod<Void> setLastSlice;
}
