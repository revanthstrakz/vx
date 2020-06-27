package com.lody.virtual.remote;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ReceiverInfo implements Parcelable {
    public static final Creator<ReceiverInfo> CREATOR = new Creator<ReceiverInfo>() {
        public ReceiverInfo createFromParcel(Parcel parcel) {
            return new ReceiverInfo(parcel);
        }

        public ReceiverInfo[] newArray(int i) {
            return new ReceiverInfo[i];
        }
    };
    public ComponentName component;
    public IntentFilter[] filters;
    public String permission;

    public int describeContents() {
        return 0;
    }

    public ReceiverInfo(ComponentName componentName, IntentFilter[] intentFilterArr, String str) {
        this.component = componentName;
        this.filters = intentFilterArr;
        this.permission = str;
    }

    protected ReceiverInfo(Parcel parcel) {
        this.component = (ComponentName) parcel.readParcelable(ComponentName.class.getClassLoader());
        this.filters = (IntentFilter[]) parcel.createTypedArray(IntentFilter.CREATOR);
        this.permission = parcel.readString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.component, i);
        parcel.writeTypedArray(this.filters, i);
        parcel.writeString(this.permission);
    }
}
