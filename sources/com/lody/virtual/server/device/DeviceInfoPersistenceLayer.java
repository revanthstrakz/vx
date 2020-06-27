package com.lody.virtual.server.device;

import android.os.Parcel;
import com.lody.virtual.helper.PersistenceLayer;
import com.lody.virtual.helper.collection.SparseArray;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.remote.VDeviceInfo;

public class DeviceInfoPersistenceLayer extends PersistenceLayer {
    private VDeviceManagerService mService;

    public int getCurrentVersion() {
        return 1;
    }

    public boolean onVersionConflict(int i, int i2) {
        return false;
    }

    public boolean verifyMagic(Parcel parcel) {
        return true;
    }

    public void writeMagic(Parcel parcel) {
    }

    public DeviceInfoPersistenceLayer(VDeviceManagerService vDeviceManagerService) {
        super(VEnvironment.getDeviceInfoFile());
        this.mService = vDeviceManagerService;
    }

    public void writePersistenceData(Parcel parcel) {
        SparseArray deviceInfos = this.mService.getDeviceInfos();
        int size = deviceInfos.size();
        parcel.writeInt(size);
        for (int i = 0; i < size; i++) {
            VDeviceInfo vDeviceInfo = (VDeviceInfo) deviceInfos.valueAt(i);
            parcel.writeInt(deviceInfos.keyAt(i));
            vDeviceInfo.writeToParcel(parcel, 0);
        }
    }

    public void readPersistenceData(Parcel parcel) {
        SparseArray deviceInfos = this.mService.getDeviceInfos();
        deviceInfos.clear();
        int readInt = parcel.readInt();
        while (true) {
            int i = readInt - 1;
            if (readInt > 0) {
                deviceInfos.put(parcel.readInt(), new VDeviceInfo(parcel));
                readInt = i;
            } else {
                return;
            }
        }
    }

    public void onPersistenceFileDamage() {
        getPersistenceFile().delete();
    }
}
