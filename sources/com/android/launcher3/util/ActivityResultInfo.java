package com.android.launcher3.util;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ActivityResultInfo implements Parcelable {
    public static final Creator<ActivityResultInfo> CREATOR = new Creator<ActivityResultInfo>() {
        public ActivityResultInfo createFromParcel(Parcel parcel) {
            return new ActivityResultInfo(parcel);
        }

        public ActivityResultInfo[] newArray(int i) {
            return new ActivityResultInfo[i];
        }
    };
    public final Intent data;
    public final int requestCode;
    public final int resultCode;

    public int describeContents() {
        return 0;
    }

    public ActivityResultInfo(int i, int i2, Intent intent) {
        this.requestCode = i;
        this.resultCode = i2;
        this.data = intent;
    }

    private ActivityResultInfo(Parcel parcel) {
        this.requestCode = parcel.readInt();
        this.resultCode = parcel.readInt();
        this.data = parcel.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(parcel) : null;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.requestCode);
        parcel.writeInt(this.resultCode);
        if (this.data != null) {
            parcel.writeInt(1);
            this.data.writeToParcel(parcel, i);
            return;
        }
        parcel.writeInt(0);
    }
}
