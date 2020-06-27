package com.lody.virtual.server.p009pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/* renamed from: com.lody.virtual.server.pm.PackageUserState */
public class PackageUserState implements Parcelable {
    public static final Creator<PackageUserState> CREATOR = new Creator<PackageUserState>() {
        public PackageUserState createFromParcel(Parcel parcel) {
            return new PackageUserState(parcel);
        }

        public PackageUserState[] newArray(int i) {
            return new PackageUserState[i];
        }
    };
    public boolean hidden;
    public boolean installed;
    public boolean launched;

    public int describeContents() {
        return 0;
    }

    public PackageUserState() {
        this.installed = false;
        this.launched = true;
        this.hidden = false;
    }

    protected PackageUserState(Parcel parcel) {
        boolean z = false;
        this.launched = parcel.readByte() != 0;
        this.hidden = parcel.readByte() != 0;
        if (parcel.readByte() != 0) {
            z = true;
        }
        this.installed = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.launched ? (byte) 1 : 0);
        parcel.writeByte(this.hidden ? (byte) 1 : 0);
        parcel.writeByte(this.installed ? (byte) 1 : 0);
    }
}
