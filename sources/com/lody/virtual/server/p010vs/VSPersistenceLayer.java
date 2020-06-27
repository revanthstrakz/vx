package com.lody.virtual.server.p010vs;

import android.os.Parcel;
import android.util.SparseArray;
import com.lody.virtual.helper.PersistenceLayer;
import com.lody.virtual.p007os.VEnvironment;
import java.util.Arrays;
import java.util.Map;

/* renamed from: com.lody.virtual.server.vs.VSPersistenceLayer */
class VSPersistenceLayer extends PersistenceLayer {
    private static final int CURRENT_VERSION = 1;
    private static final char[] MAGIC = {'v', 's', 'a'};
    private final VirtualStorageService mService;

    public int getCurrentVersion() {
        return 1;
    }

    public void onPersistenceFileDamage() {
    }

    public boolean onVersionConflict(int i, int i2) {
        return false;
    }

    VSPersistenceLayer(VirtualStorageService virtualStorageService) {
        super(VEnvironment.getVSConfigFile());
        this.mService = virtualStorageService;
    }

    public void writeMagic(Parcel parcel) {
        parcel.writeCharArray(MAGIC);
    }

    public boolean verifyMagic(Parcel parcel) {
        return Arrays.equals(parcel.createCharArray(), MAGIC);
    }

    public void writePersistenceData(Parcel parcel) {
        SparseArray configs = this.mService.getConfigs();
        int size = configs.size();
        parcel.writeInt(size);
        while (true) {
            int i = size - 1;
            if (size > 0) {
                Map map = (Map) configs.valueAt(i);
                parcel.writeInt(configs.keyAt(i));
                parcel.writeMap(map);
                size = i;
            } else {
                return;
            }
        }
    }

    public void readPersistenceData(Parcel parcel) {
        SparseArray configs = this.mService.getConfigs();
        int readInt = parcel.readInt();
        while (true) {
            int i = readInt - 1;
            if (readInt > 0) {
                configs.put(parcel.readInt(), parcel.readHashMap(VSConfig.class.getClassLoader()));
                readInt = i;
            } else {
                return;
            }
        }
    }
}
