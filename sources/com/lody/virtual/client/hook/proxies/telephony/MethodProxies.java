package com.lody.virtual.client.hook.proxies.telephony;

import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.NeighboringCellInfo;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import com.lody.virtual.client.ipc.VirtualLocationManager;
import com.lody.virtual.remote.vloc.VCell;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class MethodProxies {

    static class GetAllCellInfo extends ReplaceCallingPkgMethodProxy {
        public GetAllCellInfo() {
            super("getAllCellInfo");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                List<VCell> allCell = VirtualLocationManager.get().getAllCell(getAppUserId(), getAppPkg());
                if (allCell != null) {
                    ArrayList arrayList = new ArrayList();
                    for (VCell access$100 : allCell) {
                        arrayList.add(MethodProxies.createCellInfo(access$100));
                    }
                    return arrayList;
                }
            }
            return super.call(obj, method, objArr);
        }
    }

    static class GetCellLocation extends ReplaceCallingPkgMethodProxy {
        public GetCellLocation() {
            super("getCellLocation");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                VCell cell = VirtualLocationManager.get().getCell(getAppUserId(), getAppPkg());
                if (cell != null) {
                    return MethodProxies.getCellLocationInternal(cell);
                }
            }
            return super.call(obj, method, objArr);
        }
    }

    static class GetDeviceId extends StaticMethodProxy {
        public GetDeviceId() {
            super("getDeviceId");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return getDeviceInfo().deviceId;
        }
    }

    static class GetNeighboringCellInfo extends ReplaceCallingPkgMethodProxy {
        public GetNeighboringCellInfo() {
            super("getNeighboringCellInfo");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                List<VCell> neighboringCell = VirtualLocationManager.get().getNeighboringCell(getAppUserId(), getAppPkg());
                if (neighboringCell != null) {
                    ArrayList arrayList = new ArrayList();
                    for (VCell vCell : neighboringCell) {
                        NeighboringCellInfo neighboringCellInfo = new NeighboringCellInfo();
                        mirror.android.telephony.NeighboringCellInfo.mLac.set(neighboringCellInfo, vCell.lac);
                        mirror.android.telephony.NeighboringCellInfo.mCid.set(neighboringCellInfo, vCell.cid);
                        mirror.android.telephony.NeighboringCellInfo.mRssi.set(neighboringCellInfo, 6);
                        arrayList.add(neighboringCellInfo);
                    }
                    return arrayList;
                }
            }
            return super.call(obj, method, objArr);
        }
    }

    static class getAllCellInfoUsingSubId extends ReplaceCallingPkgMethodProxy {
        public getAllCellInfoUsingSubId() {
            super("getAllCellInfoUsingSubId");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                return null;
            }
            return super.call(obj, method, objArr);
        }
    }

    MethodProxies() {
    }

    /* access modifiers changed from: private */
    public static Bundle getCellLocationInternal(VCell vCell) {
        if (vCell == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        if (vCell.type == 2) {
            try {
                CdmaCellLocation cdmaCellLocation = new CdmaCellLocation();
                cdmaCellLocation.setCellLocationData(vCell.baseStationId, Integer.MAX_VALUE, Integer.MAX_VALUE, vCell.systemId, vCell.networkId);
                cdmaCellLocation.fillInNotifierBundle(bundle);
            } catch (Throwable unused) {
                bundle.putInt("baseStationId", vCell.baseStationId);
                bundle.putInt("baseStationLatitude", Integer.MAX_VALUE);
                bundle.putInt("baseStationLongitude", Integer.MAX_VALUE);
                bundle.putInt("systemId", vCell.systemId);
                bundle.putInt("networkId", vCell.networkId);
            }
        } else {
            try {
                GsmCellLocation gsmCellLocation = new GsmCellLocation();
                gsmCellLocation.setLacAndCid(vCell.lac, vCell.cid);
                gsmCellLocation.fillInNotifierBundle(bundle);
            } catch (Throwable unused2) {
                bundle.putInt("lac", vCell.lac);
                bundle.putInt("cid", vCell.cid);
                bundle.putInt("psc", vCell.psc);
            }
        }
        return bundle;
    }

    /* access modifiers changed from: private */
    public static CellInfo createCellInfo(VCell vCell) {
        if (vCell.type == 2) {
            CellInfoCdma cellInfoCdma = (CellInfoCdma) mirror.android.telephony.CellInfoCdma.ctor.newInstance();
            CellIdentityCdma cellIdentityCdma = (CellIdentityCdma) mirror.android.telephony.CellInfoCdma.mCellIdentityCdma.get(cellInfoCdma);
            CellSignalStrengthCdma cellSignalStrengthCdma = (CellSignalStrengthCdma) mirror.android.telephony.CellInfoCdma.mCellSignalStrengthCdma.get(cellInfoCdma);
            mirror.android.telephony.CellIdentityCdma.mNetworkId.set(cellIdentityCdma, vCell.networkId);
            mirror.android.telephony.CellIdentityCdma.mSystemId.set(cellIdentityCdma, vCell.systemId);
            mirror.android.telephony.CellIdentityCdma.mBasestationId.set(cellIdentityCdma, vCell.baseStationId);
            mirror.android.telephony.CellSignalStrengthCdma.mCdmaDbm.set(cellSignalStrengthCdma, -74);
            mirror.android.telephony.CellSignalStrengthCdma.mCdmaEcio.set(cellSignalStrengthCdma, -91);
            mirror.android.telephony.CellSignalStrengthCdma.mEvdoDbm.set(cellSignalStrengthCdma, -64);
            mirror.android.telephony.CellSignalStrengthCdma.mEvdoSnr.set(cellSignalStrengthCdma, 7);
            return cellInfoCdma;
        }
        CellInfoGsm cellInfoGsm = (CellInfoGsm) mirror.android.telephony.CellInfoGsm.ctor.newInstance();
        CellIdentityGsm cellIdentityGsm = (CellIdentityGsm) mirror.android.telephony.CellInfoGsm.mCellIdentityGsm.get(cellInfoGsm);
        CellSignalStrengthGsm cellSignalStrengthGsm = (CellSignalStrengthGsm) mirror.android.telephony.CellInfoGsm.mCellSignalStrengthGsm.get(cellInfoGsm);
        mirror.android.telephony.CellIdentityGsm.mMcc.set(cellIdentityGsm, vCell.mcc);
        mirror.android.telephony.CellIdentityGsm.mMnc.set(cellIdentityGsm, vCell.mnc);
        mirror.android.telephony.CellIdentityGsm.mLac.set(cellIdentityGsm, vCell.lac);
        mirror.android.telephony.CellIdentityGsm.mCid.set(cellIdentityGsm, vCell.cid);
        mirror.android.telephony.CellSignalStrengthGsm.mSignalStrength.set(cellSignalStrengthGsm, 20);
        mirror.android.telephony.CellSignalStrengthGsm.mBitErrorRate.set(cellSignalStrengthGsm, 0);
        return cellInfoGsm;
    }
}
