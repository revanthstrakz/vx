package com.lody.virtual.remote;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class SyncInfo {
    public static final Creator<SyncInfo> CREATOR = new Creator<SyncInfo>() {
        public SyncInfo createFromParcel(Parcel parcel) {
            return new SyncInfo(parcel);
        }

        public SyncInfo[] newArray(int i) {
            return new SyncInfo[i];
        }
    };
    public final Account account;
    public final String authority;
    public final int authorityId;
    public final long startTime;

    public int describeContents() {
        return 0;
    }

    public SyncInfo(int i, Account account2, String str, long j) {
        this.authorityId = i;
        this.account = account2;
        this.authority = str;
        this.startTime = j;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.authorityId);
        this.account.writeToParcel(parcel, 0);
        parcel.writeString(this.authority);
        parcel.writeLong(this.startTime);
    }

    SyncInfo(Parcel parcel) {
        this.authorityId = parcel.readInt();
        this.account = new Account(parcel);
        this.authority = parcel.readString();
        this.startTime = parcel.readLong();
    }

    public android.content.SyncInfo create() {
        return (android.content.SyncInfo) mirror.android.content.SyncInfo.ctor.newInstance(Integer.valueOf(this.authorityId), this.account, this.authority, Long.valueOf(this.startTime));
    }
}
