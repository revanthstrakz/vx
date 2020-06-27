package com.lody.virtual.server.p010vs;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/* renamed from: com.lody.virtual.server.vs.VSConfig */
public class VSConfig implements Parcelable {
    public static final Creator<VSConfig> CREATOR = new Creator<VSConfig>() {
        public VSConfig createFromParcel(Parcel parcel) {
            return new VSConfig(parcel);
        }

        public VSConfig[] newArray(int i) {
            return new VSConfig[i];
        }
    };
    public boolean enable;
    public String vsPath;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.enable ? (byte) 1 : 0);
        parcel.writeString(this.vsPath);
    }

    public VSConfig() {
    }

    protected VSConfig(Parcel parcel) {
        this.enable = parcel.readByte() != 0;
        this.vsPath = parcel.readString();
    }
}
