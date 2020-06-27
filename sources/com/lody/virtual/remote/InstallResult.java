package com.lody.virtual.remote;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class InstallResult implements Parcelable {
    public static final Creator<InstallResult> CREATOR = new Creator<InstallResult>() {
        public InstallResult createFromParcel(Parcel parcel) {
            return new InstallResult(parcel);
        }

        public InstallResult[] newArray(int i) {
            return new InstallResult[i];
        }
    };
    public String error;
    public boolean isSuccess;
    public boolean isUpdate;
    public String packageName;

    public int describeContents() {
        return 0;
    }

    public InstallResult() {
    }

    protected InstallResult(Parcel parcel) {
        boolean z = false;
        this.isSuccess = parcel.readByte() != 0;
        if (parcel.readByte() != 0) {
            z = true;
        }
        this.isUpdate = z;
        this.packageName = parcel.readString();
        this.error = parcel.readString();
    }

    public static InstallResult makeFailure(String str) {
        InstallResult installResult = new InstallResult();
        installResult.error = str;
        return installResult;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.isSuccess ? (byte) 1 : 0);
        parcel.writeByte(this.isUpdate ? (byte) 1 : 0);
        parcel.writeString(this.packageName);
        parcel.writeString(this.error);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("InstallResult{isSuccess=");
        sb.append(this.isSuccess);
        sb.append(", isUpdate=");
        sb.append(this.isUpdate);
        sb.append(", packageName='");
        sb.append(this.packageName);
        sb.append('\'');
        sb.append(", error='");
        sb.append(this.error);
        sb.append('\'');
        sb.append('}');
        return sb.toString();
    }
}
