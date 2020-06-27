package com.lody.virtual.server.accounts;

import android.accounts.Account;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VSyncRecord {
    public Map<SyncExtras, PeriodicSyncConfig> configs = new HashMap();
    public List<SyncExtras> extras = new ArrayList();
    public boolean isPeriodic = false;
    public SyncRecordKey key;
    public int syncable = -1;
    public int userId;

    static class PeriodicSyncConfig implements Parcelable {
        public static final Creator<PeriodicSyncConfig> CREATOR = new Creator<PeriodicSyncConfig>() {
            public PeriodicSyncConfig createFromParcel(Parcel parcel) {
                return new PeriodicSyncConfig(parcel);
            }

            public PeriodicSyncConfig[] newArray(int i) {
                return new PeriodicSyncConfig[i];
            }
        };
        long syncRunTimeSecs;

        public int describeContents() {
            return 0;
        }

        public PeriodicSyncConfig(long j) {
            this.syncRunTimeSecs = j;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(this.syncRunTimeSecs);
        }

        PeriodicSyncConfig(Parcel parcel) {
            this.syncRunTimeSecs = parcel.readLong();
        }
    }

    public static class SyncExtras implements Parcelable {
        public static final Creator<SyncExtras> CREATOR = new Creator<SyncExtras>() {
            public SyncExtras createFromParcel(Parcel parcel) {
                return new SyncExtras(parcel);
            }

            public SyncExtras[] newArray(int i) {
                return new SyncExtras[i];
            }
        };
        Bundle extras;

        public int describeContents() {
            return 0;
        }

        public SyncExtras(Bundle bundle) {
            this.extras = bundle;
        }

        SyncExtras(Parcel parcel) {
            this.extras = parcel.readBundle(getClass().getClassLoader());
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeBundle(this.extras);
        }

        public boolean equals(Object obj) {
            return VSyncRecord.equals(this.extras, ((SyncExtras) obj).extras, false);
        }
    }

    public static class SyncRecordKey implements Parcelable {
        public static final Creator<SyncRecordKey> CREATOR = new Creator<SyncRecordKey>() {
            public SyncRecordKey createFromParcel(Parcel parcel) {
                return new SyncRecordKey(parcel);
            }

            public SyncRecordKey[] newArray(int i) {
                return new SyncRecordKey[i];
            }
        };
        Account account;
        String authority;

        public int describeContents() {
            return 0;
        }

        SyncRecordKey(Account account2, String str) {
            this.account = account2;
            this.authority = str;
        }

        SyncRecordKey(Parcel parcel) {
            this.account = (Account) parcel.readParcelable(Account.class.getClassLoader());
            this.authority = parcel.readString();
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.account, i);
            parcel.writeString(this.authority);
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            SyncRecordKey syncRecordKey = (SyncRecordKey) obj;
            if (this.account == null ? syncRecordKey.account != null : !this.account.equals(syncRecordKey.account)) {
                return false;
            }
            if (this.authority != null) {
                z = this.authority.equals(syncRecordKey.authority);
            } else if (syncRecordKey.authority != null) {
                z = false;
            }
            return z;
        }
    }

    public VSyncRecord(int i, Account account, String str) {
        this.userId = i;
        this.key = new SyncRecordKey(account, str);
    }

    public static boolean equals(Bundle bundle, Bundle bundle2, boolean z) {
        if (bundle == bundle2) {
            return true;
        }
        if (z && bundle.size() != bundle2.size()) {
            return false;
        }
        if (bundle.size() <= bundle2.size()) {
            Bundle bundle3 = bundle2;
            bundle2 = bundle;
            bundle = bundle3;
        }
        for (String str : bundle.keySet()) {
            if ((z || !isIgnoredKey(str)) && (!bundle2.containsKey(str) || !bundle.get(str).equals(bundle2.get(str)))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isIgnoredKey(String str) {
        return str.equals("expedited") || str.equals("ignore_settings") || str.equals("ignore_backoff") || str.equals("do_not_retry") || str.equals("force") || str.equals("upload") || str.equals("deletions_override") || str.equals("discard_deletions") || str.equals("expected_upload") || str.equals("expected_download") || str.equals("sync_priority") || str.equals("allow_metered") || str.equals("initialize");
    }
}
