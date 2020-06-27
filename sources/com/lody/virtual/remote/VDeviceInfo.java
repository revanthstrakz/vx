package com.lody.virtual.remote;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.lody.virtual.p007os.VEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class VDeviceInfo implements Parcelable {
    public static final Creator<VDeviceInfo> CREATOR = new Creator<VDeviceInfo>() {
        public VDeviceInfo createFromParcel(Parcel parcel) {
            return new VDeviceInfo(parcel);
        }

        public VDeviceInfo[] newArray(int i) {
            return new VDeviceInfo[i];
        }
    };
    public String androidId;
    public String bluetoothMac;
    public String deviceId;
    public String gmsAdId;
    public String iccId;
    public String serial;
    public String wifiMac;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.deviceId);
        parcel.writeString(this.androidId);
        parcel.writeString(this.wifiMac);
        parcel.writeString(this.bluetoothMac);
        parcel.writeString(this.iccId);
        parcel.writeString(this.serial);
        parcel.writeString(this.gmsAdId);
    }

    public VDeviceInfo() {
    }

    public VDeviceInfo(Parcel parcel) {
        this.deviceId = parcel.readString();
        this.androidId = parcel.readString();
        this.wifiMac = parcel.readString();
        this.bluetoothMac = parcel.readString();
        this.iccId = parcel.readString();
        this.serial = parcel.readString();
        this.gmsAdId = parcel.readString();
    }

    public File getWifiFile(int i) {
        File wifiMacFile = VEnvironment.getWifiMacFile(i);
        if (!wifiMacFile.exists()) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(wifiMacFile, "rws");
                StringBuilder sb = new StringBuilder();
                sb.append(this.wifiMac);
                sb.append("\n");
                randomAccessFile.write(sb.toString().getBytes());
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wifiMacFile;
    }
}
