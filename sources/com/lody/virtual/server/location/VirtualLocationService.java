package com.lody.virtual.server.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.lody.virtual.helper.PersistenceLayer;
import com.lody.virtual.helper.collection.SparseArray;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.remote.vloc.VCell;
import com.lody.virtual.remote.vloc.VLocation;
import com.lody.virtual.server.IVirtualLocationManager.Stub;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualLocationService extends Stub {
    private static final int MODE_CLOSE = 0;
    private static final int MODE_USE_GLOBAL = 1;
    private static final int MODE_USE_SELF = 2;
    private static final VirtualLocationService sInstance = new VirtualLocationService();
    /* access modifiers changed from: private */
    public final VLocConfig mGlobalConfig = new VLocConfig();
    /* access modifiers changed from: private */
    public final SparseArray<Map<String, VLocConfig>> mLocConfigs = new SparseArray<>();
    private final PersistenceLayer mPersistenceLayer = new PersistenceLayer(VEnvironment.getVirtualLocationFile()) {
        public int getCurrentVersion() {
            return 1;
        }

        public void writePersistenceData(Parcel parcel) {
            VirtualLocationService.this.mGlobalConfig.writeToParcel(parcel, 0);
            parcel.writeInt(VirtualLocationService.this.mLocConfigs.size());
            for (int i = 0; i < VirtualLocationService.this.mLocConfigs.size(); i++) {
                Map map = (Map) VirtualLocationService.this.mLocConfigs.valueAt(i);
                parcel.writeInt(VirtualLocationService.this.mLocConfigs.keyAt(i));
                parcel.writeMap(map);
            }
        }

        public void readPersistenceData(Parcel parcel) {
            VirtualLocationService.this.mGlobalConfig.set(new VLocConfig(parcel));
            VirtualLocationService.this.mLocConfigs.clear();
            int readInt = parcel.readInt();
            while (true) {
                int i = readInt - 1;
                if (readInt > 0) {
                    VirtualLocationService.this.mLocConfigs.put(parcel.readInt(), parcel.readHashMap(getClass().getClassLoader()));
                    readInt = i;
                } else {
                    return;
                }
            }
        }
    };

    private static class VLocConfig implements Parcelable {
        public static final Creator<VLocConfig> CREATOR = new Creator<VLocConfig>() {
            public VLocConfig createFromParcel(Parcel parcel) {
                return new VLocConfig(parcel);
            }

            public VLocConfig[] newArray(int i) {
                return new VLocConfig[i];
            }
        };
        List<VCell> allCell;
        VCell cell;
        VLocation location;
        int mode;
        List<VCell> neighboringCell;

        public int describeContents() {
            return 0;
        }

        public void set(VLocConfig vLocConfig) {
            this.mode = vLocConfig.mode;
            this.cell = vLocConfig.cell;
            this.allCell = vLocConfig.allCell;
            this.neighboringCell = vLocConfig.neighboringCell;
            this.location = vLocConfig.location;
        }

        VLocConfig() {
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mode);
            parcel.writeParcelable(this.cell, i);
            parcel.writeTypedList(this.allCell);
            parcel.writeTypedList(this.neighboringCell);
            parcel.writeParcelable(this.location, i);
        }

        VLocConfig(Parcel parcel) {
            this.mode = parcel.readInt();
            this.cell = (VCell) parcel.readParcelable(VCell.class.getClassLoader());
            this.allCell = parcel.createTypedArrayList(VCell.CREATOR);
            this.neighboringCell = parcel.createTypedArrayList(VCell.CREATOR);
            this.location = (VLocation) parcel.readParcelable(VLocation.class.getClassLoader());
        }
    }

    public static VirtualLocationService get() {
        return sInstance;
    }

    private VirtualLocationService() {
        this.mPersistenceLayer.read();
    }

    public int getMode(int i, String str) throws RemoteException {
        int i2;
        synchronized (this.mLocConfigs) {
            VLocConfig orCreateConfig = getOrCreateConfig(i, str);
            this.mPersistenceLayer.save();
            i2 = orCreateConfig.mode;
        }
        return i2;
    }

    public void setMode(int i, String str, int i2) throws RemoteException {
        synchronized (this.mLocConfigs) {
            getOrCreateConfig(i, str).mode = i2;
            this.mPersistenceLayer.save();
        }
    }

    private VLocConfig getOrCreateConfig(int i, String str) {
        Map map = (Map) this.mLocConfigs.get(i);
        if (map == null) {
            map = new HashMap();
            this.mLocConfigs.put(i, map);
        }
        VLocConfig vLocConfig = (VLocConfig) map.get(str);
        if (vLocConfig != null) {
            return vLocConfig;
        }
        VLocConfig vLocConfig2 = new VLocConfig();
        vLocConfig2.mode = 0;
        map.put(str, vLocConfig2);
        return vLocConfig2;
    }

    public void setCell(int i, String str, VCell vCell) throws RemoteException {
        getOrCreateConfig(i, str).cell = vCell;
        this.mPersistenceLayer.save();
    }

    public void setAllCell(int i, String str, List<VCell> list) throws RemoteException {
        getOrCreateConfig(i, str).allCell = list;
        this.mPersistenceLayer.save();
    }

    public void setNeighboringCell(int i, String str, List<VCell> list) throws RemoteException {
        getOrCreateConfig(i, str).neighboringCell = list;
        this.mPersistenceLayer.save();
    }

    public void setGlobalCell(VCell vCell) throws RemoteException {
        this.mGlobalConfig.cell = vCell;
        this.mPersistenceLayer.save();
    }

    public void setGlobalAllCell(List<VCell> list) throws RemoteException {
        this.mGlobalConfig.allCell = list;
        this.mPersistenceLayer.save();
    }

    public void setGlobalNeighboringCell(List<VCell> list) throws RemoteException {
        this.mGlobalConfig.neighboringCell = list;
        this.mPersistenceLayer.save();
    }

    public VCell getCell(int i, String str) throws RemoteException {
        VLocConfig orCreateConfig = getOrCreateConfig(i, str);
        this.mPersistenceLayer.save();
        switch (orCreateConfig.mode) {
            case 1:
                return this.mGlobalConfig.cell;
            case 2:
                return orCreateConfig.cell;
            default:
                return null;
        }
    }

    public List<VCell> getAllCell(int i, String str) throws RemoteException {
        VLocConfig orCreateConfig = getOrCreateConfig(i, str);
        this.mPersistenceLayer.save();
        switch (orCreateConfig.mode) {
            case 1:
                return this.mGlobalConfig.allCell;
            case 2:
                return orCreateConfig.allCell;
            default:
                return null;
        }
    }

    public List<VCell> getNeighboringCell(int i, String str) throws RemoteException {
        VLocConfig orCreateConfig = getOrCreateConfig(i, str);
        this.mPersistenceLayer.save();
        switch (orCreateConfig.mode) {
            case 1:
                return this.mGlobalConfig.neighboringCell;
            case 2:
                return orCreateConfig.neighboringCell;
            default:
                return null;
        }
    }

    public void setLocation(int i, String str, VLocation vLocation) throws RemoteException {
        getOrCreateConfig(i, str).location = vLocation;
        this.mPersistenceLayer.save();
    }

    public VLocation getLocation(int i, String str) throws RemoteException {
        VLocConfig orCreateConfig = getOrCreateConfig(i, str);
        this.mPersistenceLayer.save();
        switch (orCreateConfig.mode) {
            case 1:
                return this.mGlobalConfig.location;
            case 2:
                return orCreateConfig.location;
            default:
                return null;
        }
    }

    public void setGlobalLocation(VLocation vLocation) throws RemoteException {
        this.mGlobalConfig.location = vLocation;
    }

    public VLocation getGlobalLocation() throws RemoteException {
        return this.mGlobalConfig.location;
    }
}
