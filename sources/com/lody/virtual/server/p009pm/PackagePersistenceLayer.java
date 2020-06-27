package com.lody.virtual.server.p009pm;

import android.os.Parcel;
import com.lody.virtual.helper.PersistenceLayer;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.server.p009pm.parser.VPackage;
import java.util.Arrays;

/* renamed from: com.lody.virtual.server.pm.PackagePersistenceLayer */
class PackagePersistenceLayer extends PersistenceLayer {
    private static final int CURRENT_VERSION = 3;
    private static final char[] MAGIC = {'v', 'p', 'k', 'g'};
    private VAppManagerService mService;

    public int getCurrentVersion() {
        return 3;
    }

    public boolean onVersionConflict(int i, int i2) {
        return false;
    }

    PackagePersistenceLayer(VAppManagerService vAppManagerService) {
        super(VEnvironment.getPackageListFile());
        this.mService = vAppManagerService;
    }

    public void writeMagic(Parcel parcel) {
        parcel.writeCharArray(MAGIC);
    }

    public boolean verifyMagic(Parcel parcel) {
        return Arrays.equals(parcel.createCharArray(), MAGIC);
    }

    public void writePersistenceData(Parcel parcel) {
        synchronized (PackageCacheManager.PACKAGE_CACHE) {
            parcel.writeInt(PackageCacheManager.PACKAGE_CACHE.size());
            for (VPackage vPackage : PackageCacheManager.PACKAGE_CACHE.values()) {
                ((PackageSetting) vPackage.mExtras).writeToParcel(parcel, 0);
            }
        }
    }

    public void readPersistenceData(Parcel parcel) {
        int readInt = parcel.readInt();
        while (true) {
            int i = readInt - 1;
            if (readInt > 0) {
                this.mService.loadPackage(new PackageSetting(parcel));
                readInt = i;
            } else {
                return;
            }
        }
    }

    public void onPersistenceFileDamage() {
        getPersistenceFile().delete();
        VAppManagerService.get().restoreFactoryState();
    }
}
