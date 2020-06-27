package com.lody.virtual.remote;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Problem implements Parcelable {
    public static final Creator<Problem> CREATOR = new Creator<Problem>() {
        public Problem createFromParcel(Parcel parcel) {
            return new Problem(parcel);
        }

        public Problem[] newArray(int i) {
            return new Problem[i];
        }
    };

    /* renamed from: e */
    public Throwable f181e;

    public int describeContents() {
        return 0;
    }

    public Problem(Throwable th) {
        this.f181e = th;
    }

    protected Problem(Parcel parcel) {
        this.f181e = (Throwable) parcel.readSerializable();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(this.f181e);
    }
}
