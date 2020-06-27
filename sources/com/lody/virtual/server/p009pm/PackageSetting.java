package com.lody.virtual.server.p009pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.lody.virtual.remote.InstalledAppInfo;

/* renamed from: com.lody.virtual.server.pm.PackageSetting */
public class PackageSetting implements Parcelable {
    public static final Creator<PackageSetting> CREATOR = new Creator<PackageSetting>() {
        public PackageSetting createFromParcel(Parcel parcel) {
            return new PackageSetting(parcel);
        }

        public PackageSetting[] newArray(int i) {
            return new PackageSetting[i];
        }
    };
    private static final PackageUserState DEFAULT_USER_STATE = new PackageUserState();
    public String apkPath;
    public int appId;
    public boolean dependSystem;
    public long firstInstallTime;
    public long lastUpdateTime;
    public String libPath;
    public String packageName;
    @Deprecated
    public boolean skipDexOpt;
    private SparseArray<PackageUserState> userState = new SparseArray<>();

    public int describeContents() {
        return 0;
    }

    public PackageSetting() {
    }

    protected PackageSetting(Parcel parcel) {
        this.packageName = parcel.readString();
        this.apkPath = parcel.readString();
        this.libPath = parcel.readString();
        boolean z = false;
        this.dependSystem = parcel.readByte() != 0;
        this.appId = parcel.readInt();
        this.userState = parcel.readSparseArray(PackageUserState.class.getClassLoader());
        if (parcel.readByte() != 0) {
            z = true;
        }
        this.skipDexOpt = z;
    }

    public InstalledAppInfo getAppInfo() {
        InstalledAppInfo installedAppInfo = new InstalledAppInfo(this.packageName, this.apkPath, this.libPath, this.dependSystem, this.skipDexOpt, this.appId);
        return installedAppInfo;
    }

    /* access modifiers changed from: 0000 */
    public PackageUserState modifyUserState(int i) {
        PackageUserState packageUserState = (PackageUserState) this.userState.get(i);
        if (packageUserState != null) {
            return packageUserState;
        }
        PackageUserState packageUserState2 = new PackageUserState();
        this.userState.put(i, packageUserState2);
        return packageUserState2;
    }

    /* access modifiers changed from: 0000 */
    public void setUserState(int i, boolean z, boolean z2, boolean z3) {
        PackageUserState modifyUserState = modifyUserState(i);
        modifyUserState.launched = z;
        modifyUserState.hidden = z2;
        modifyUserState.installed = z3;
    }

    /* access modifiers changed from: 0000 */
    public PackageUserState readUserState(int i) {
        PackageUserState packageUserState = (PackageUserState) this.userState.get(i);
        if (packageUserState != null) {
            return packageUserState;
        }
        return DEFAULT_USER_STATE;
    }

    /* access modifiers changed from: 0000 */
    public void removeUser(int i) {
        this.userState.delete(i);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.packageName);
        parcel.writeString(this.apkPath);
        parcel.writeString(this.libPath);
        parcel.writeByte(this.dependSystem ? (byte) 1 : 0);
        parcel.writeInt(this.appId);
        parcel.writeSparseArray(this.userState);
        parcel.writeByte(this.skipDexOpt ? (byte) 1 : 0);
    }

    public boolean isLaunched(int i) {
        return readUserState(i).launched;
    }

    public boolean isHidden(int i) {
        return readUserState(i).hidden;
    }

    public boolean isInstalled(int i) {
        return readUserState(i).installed;
    }

    public void setLaunched(int i, boolean z) {
        modifyUserState(i).launched = z;
    }

    public void setHidden(int i, boolean z) {
        modifyUserState(i).hidden = z;
    }

    public void setInstalled(int i, boolean z) {
        modifyUserState(i).installed = z;
    }
}
