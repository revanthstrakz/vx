package mirror.android.telephony;

import android.annotation.TargetApi;
import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefInt;

@TargetApi(17)
public class CellIdentityCdma {
    public static Class<?> TYPE = RefClass.load(CellIdentityCdma.class, android.telephony.CellIdentityCdma.class);
    public static RefConstructor<android.telephony.CellIdentityCdma> ctor;
    public static RefInt mBasestationId;
    public static RefInt mNetworkId;
    public static RefInt mSystemId;
}
