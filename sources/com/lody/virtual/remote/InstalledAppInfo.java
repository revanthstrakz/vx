package com.lody.virtual.remote;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VPackageManager;
import com.lody.virtual.p007os.VEnvironment;
import java.io.File;

public final class InstalledAppInfo implements Parcelable {
    public static final Creator<InstalledAppInfo> CREATOR = new Creator<InstalledAppInfo>() {
        public InstalledAppInfo createFromParcel(Parcel parcel) {
            return new InstalledAppInfo(parcel);
        }

        public InstalledAppInfo[] newArray(int i) {
            return new InstalledAppInfo[i];
        }
    };
    public String apkPath;
    public int appId;
    public boolean dependSystem;
    public String libPath;
    public String packageName;

    public int describeContents() {
        return 0;
    }

    public InstalledAppInfo(String str, String str2, String str3, boolean z, boolean z2, int i) {
        this.packageName = str;
        this.apkPath = str2;
        this.libPath = str3;
        this.dependSystem = z;
        this.appId = i;
    }

    public File getOdexFile() {
        return VEnvironment.getOdexFile(this.packageName);
    }

    public ApplicationInfo getApplicationInfo(int i) {
        return VPackageManager.get().getApplicationInfo(this.packageName, 0, i);
    }

    public PackageInfo getPackageInfo(int i) {
        return VPackageManager.get().getPackageInfo(this.packageName, 0, i);
    }

    public int[] getInstalledUsers() {
        return VirtualCore.get().getPackageInstalledUsers(this.packageName);
    }

    public boolean isLaunched(int i) {
        return VirtualCore.get().isPackageLaunched(i, this.packageName);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.packageName);
        parcel.writeString(this.apkPath);
        parcel.writeString(this.libPath);
        parcel.writeByte(this.dependSystem ? (byte) 1 : 0);
        parcel.writeInt(this.appId);
    }

    protected InstalledAppInfo(Parcel parcel) {
        this.packageName = parcel.readString();
        this.apkPath = parcel.readString();
        this.libPath = parcel.readString();
        this.dependSystem = parcel.readByte() != 0;
        this.appId = parcel.readInt();
    }
}
