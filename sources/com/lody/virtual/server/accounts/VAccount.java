package com.lody.virtual.server.accounts;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class VAccount implements Parcelable {
    public static final Creator<VAccount> CREATOR = new Creator<VAccount>() {
        public VAccount createFromParcel(Parcel parcel) {
            return new VAccount(parcel);
        }

        public VAccount[] newArray(int i) {
            return new VAccount[i];
        }
    };
    public Map<String, String> authTokens;
    public long lastAuthenticatedTime;
    public String name;
    public String password;
    public String previousName;
    public String type;
    public Map<String, String> userDatas;
    public int userId;

    public int describeContents() {
        return 0;
    }

    public VAccount(int i, Account account) {
        this.userId = i;
        this.name = account.name;
        this.type = account.type;
        this.authTokens = new HashMap();
        this.userDatas = new HashMap();
    }

    public VAccount(Parcel parcel) {
        this.userId = parcel.readInt();
        this.name = parcel.readString();
        this.previousName = parcel.readString();
        this.type = parcel.readString();
        this.password = parcel.readString();
        this.lastAuthenticatedTime = parcel.readLong();
        int readInt = parcel.readInt();
        this.authTokens = new HashMap(readInt);
        for (int i = 0; i < readInt; i++) {
            this.authTokens.put(parcel.readString(), parcel.readString());
        }
        int readInt2 = parcel.readInt();
        this.userDatas = new HashMap(readInt2);
        for (int i2 = 0; i2 < readInt2; i2++) {
            this.userDatas.put(parcel.readString(), parcel.readString());
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.userId);
        parcel.writeString(this.name);
        parcel.writeString(this.previousName);
        parcel.writeString(this.type);
        parcel.writeString(this.password);
        parcel.writeLong(this.lastAuthenticatedTime);
        parcel.writeInt(this.authTokens.size());
        for (Entry entry : this.authTokens.entrySet()) {
            parcel.writeString((String) entry.getKey());
            parcel.writeString((String) entry.getValue());
        }
        parcel.writeInt(this.userDatas.size());
        for (Entry entry2 : this.userDatas.entrySet()) {
            parcel.writeString((String) entry2.getKey());
            parcel.writeString((String) entry2.getValue());
        }
    }
}
