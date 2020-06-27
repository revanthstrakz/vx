package mirror.android.telephony;

import android.annotation.TargetApi;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefInt;

@TargetApi(17)
public class CellSignalStrengthGsm {
    public static Class<?> TYPE = RefClass.load(CellSignalStrengthGsm.class, android.telephony.CellSignalStrengthGsm.class);
    public static RefConstructor<android.telephony.CellSignalStrengthGsm> ctor;
    public static RefInt mBitErrorRate;
    public static RefInt mSignalStrength;
}
