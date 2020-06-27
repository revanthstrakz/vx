package mirror.android.content.p016pm;

import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.List;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefMethod;
import mirror.RefStaticObject;

/* renamed from: mirror.android.content.pm.ParceledListSliceJBMR2 */
public class ParceledListSliceJBMR2 {
    public static RefStaticObject<Creator> CREATOR;
    public static Class<?> TYPE = RefClass.load(ParceledListSliceJBMR2.class, "android.content.pm.ParceledListSlice");
    @MethodParams({List.class})
    public static RefConstructor<Parcelable> ctor;
    public static RefMethod<List> getList;
}
