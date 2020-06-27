package mirror.android.telephony;

import android.annotation.TargetApi;
import android.telephony.CellIdentityGsm;
import android.telephony.CellSignalStrengthGsm;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefObject;

@TargetApi(17)
public class CellInfoGsm {
    public static Class<?> TYPE = RefClass.load(CellInfoGsm.class, android.telephony.CellInfoGsm.class);
    public static RefConstructor<android.telephony.CellInfoGsm> ctor;
    public static RefObject<CellIdentityGsm> mCellIdentityGsm;
    public static RefObject<CellSignalStrengthGsm> mCellSignalStrengthGsm;
}
