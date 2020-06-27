package com.lody.virtual.remote;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.lody.virtual.p007os.VUserHandle;

public class BadgerInfo implements Parcelable {
    public static final Creator<BadgerInfo> CREATOR = new Creator<BadgerInfo>() {
        public BadgerInfo createFromParcel(Parcel parcel) {
            return new BadgerInfo(parcel);
        }

        public BadgerInfo[] newArray(int i) {
            return new BadgerInfo[i];
        }
    };
    public int badgerCount;
    public String className;
    public String packageName;
    public int userId;

    public int describeContents() {
        return 0;
    }

    public BadgerInfo() {
        this.userId = VUserHandle.myUserId();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.userId);
        parcel.writeString(this.packageName);
        parcel.writeInt(this.badgerCount);
        parcel.writeString(this.className);
    }

    protected BadgerInfo(Parcel parcel) {
        this.userId = parcel.readInt();
        this.packageName = parcel.readString();
        this.badgerCount = parcel.readInt();
        this.className = parcel.readString();
    }
}
