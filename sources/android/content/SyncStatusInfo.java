package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

public class SyncStatusInfo implements Parcelable {
    public static final Creator<SyncStatusInfo> CREATOR = new Creator<SyncStatusInfo>() {
        public SyncStatusInfo createFromParcel(Parcel parcel) {
            return new SyncStatusInfo(parcel);
        }

        public SyncStatusInfo[] newArray(int i) {
            return new SyncStatusInfo[i];
        }
    };
    private static final String TAG = "Sync";
    static final int VERSION = 2;
    public final int authorityId;
    public long initialFailureTime;
    public boolean initialize;
    public String lastFailureMesg;
    public int lastFailureSource;
    public long lastFailureTime;
    public int lastSuccessSource;
    public long lastSuccessTime;
    public int numSourceLocal;
    public int numSourcePeriodic;
    public int numSourcePoll;
    public int numSourceServer;
    public int numSourceUser;
    public int numSyncs;
    public boolean pending;
    private ArrayList<Long> periodicSyncTimes;
    public long totalElapsedTime;

    public int describeContents() {
        return 0;
    }

    public int getLastFailureMesgAsInt(int i) {
        return 0;
    }

    public SyncStatusInfo(int i) {
        this.authorityId = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(2);
        parcel.writeInt(this.authorityId);
        parcel.writeLong(this.totalElapsedTime);
        parcel.writeInt(this.numSyncs);
        parcel.writeInt(this.numSourcePoll);
        parcel.writeInt(this.numSourceServer);
        parcel.writeInt(this.numSourceLocal);
        parcel.writeInt(this.numSourceUser);
        parcel.writeLong(this.lastSuccessTime);
        parcel.writeInt(this.lastSuccessSource);
        parcel.writeLong(this.lastFailureTime);
        parcel.writeInt(this.lastFailureSource);
        parcel.writeString(this.lastFailureMesg);
        parcel.writeLong(this.initialFailureTime);
        parcel.writeInt(this.pending ? 1 : 0);
        parcel.writeInt(this.initialize ? 1 : 0);
        if (this.periodicSyncTimes != null) {
            parcel.writeInt(this.periodicSyncTimes.size());
            Iterator it = this.periodicSyncTimes.iterator();
            while (it.hasNext()) {
                parcel.writeLong(((Long) it.next()).longValue());
            }
            return;
        }
        parcel.writeInt(-1);
    }

    public SyncStatusInfo(Parcel parcel) {
        int readInt = parcel.readInt();
        if (!(readInt == 2 || readInt == 1)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown version: ");
            sb.append(readInt);
            Log.w("SyncStatusInfo", sb.toString());
        }
        this.authorityId = parcel.readInt();
        this.totalElapsedTime = parcel.readLong();
        this.numSyncs = parcel.readInt();
        this.numSourcePoll = parcel.readInt();
        this.numSourceServer = parcel.readInt();
        this.numSourceLocal = parcel.readInt();
        this.numSourceUser = parcel.readInt();
        this.lastSuccessTime = parcel.readLong();
        this.lastSuccessSource = parcel.readInt();
        this.lastFailureTime = parcel.readLong();
        this.lastFailureSource = parcel.readInt();
        this.lastFailureMesg = parcel.readString();
        this.initialFailureTime = parcel.readLong();
        this.pending = parcel.readInt() != 0;
        this.initialize = parcel.readInt() != 0;
        if (readInt == 1) {
            this.periodicSyncTimes = null;
            return;
        }
        int readInt2 = parcel.readInt();
        if (readInt2 < 0) {
            this.periodicSyncTimes = null;
            return;
        }
        this.periodicSyncTimes = new ArrayList<>();
        for (int i = 0; i < readInt2; i++) {
            this.periodicSyncTimes.add(Long.valueOf(parcel.readLong()));
        }
    }

    public SyncStatusInfo(SyncStatusInfo syncStatusInfo) {
        this.authorityId = syncStatusInfo.authorityId;
        this.totalElapsedTime = syncStatusInfo.totalElapsedTime;
        this.numSyncs = syncStatusInfo.numSyncs;
        this.numSourcePoll = syncStatusInfo.numSourcePoll;
        this.numSourceServer = syncStatusInfo.numSourceServer;
        this.numSourceLocal = syncStatusInfo.numSourceLocal;
        this.numSourceUser = syncStatusInfo.numSourceUser;
        this.numSourcePeriodic = syncStatusInfo.numSourcePeriodic;
        this.lastSuccessTime = syncStatusInfo.lastSuccessTime;
        this.lastSuccessSource = syncStatusInfo.lastSuccessSource;
        this.lastFailureTime = syncStatusInfo.lastFailureTime;
        this.lastFailureSource = syncStatusInfo.lastFailureSource;
        this.lastFailureMesg = syncStatusInfo.lastFailureMesg;
        this.initialFailureTime = syncStatusInfo.initialFailureTime;
        this.pending = syncStatusInfo.pending;
        this.initialize = syncStatusInfo.initialize;
        if (syncStatusInfo.periodicSyncTimes != null) {
            this.periodicSyncTimes = new ArrayList<>(syncStatusInfo.periodicSyncTimes);
        }
    }

    public void setPeriodicSyncTime(int i, long j) {
        ensurePeriodicSyncTimeSize(i);
        this.periodicSyncTimes.set(i, Long.valueOf(j));
    }

    public long getPeriodicSyncTime(int i) {
        if (this.periodicSyncTimes == null || i >= this.periodicSyncTimes.size()) {
            return 0;
        }
        return ((Long) this.periodicSyncTimes.get(i)).longValue();
    }

    public void removePeriodicSyncTime(int i) {
        if (this.periodicSyncTimes != null && i < this.periodicSyncTimes.size()) {
            this.periodicSyncTimes.remove(i);
        }
    }

    private void ensurePeriodicSyncTimeSize(int i) {
        if (this.periodicSyncTimes == null) {
            this.periodicSyncTimes = new ArrayList<>(0);
        }
        int i2 = i + 1;
        if (this.periodicSyncTimes.size() < i2) {
            for (int size = this.periodicSyncTimes.size(); size < i2; size++) {
                this.periodicSyncTimes.add(Long.valueOf(0));
            }
        }
    }
}
